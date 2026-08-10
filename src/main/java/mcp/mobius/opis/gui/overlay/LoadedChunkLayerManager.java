package mcp.mobius.opis.gui.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;

import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.UniversalInteractableRenderer;
import com.gtnewhorizons.navigator.api.util.Util;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;

/**
 * Navigator layer showing which chunks the server currently has loaded, and which of those are held by a ticket.
 * Replaces the MapWriter loaded-chunk overlay Opis used to ship.
 */
public class LoadedChunkLayerManager extends InteractableLayerManager implements IMessageHandler {

    private static final long REQUEST_INTERVAL_MS = 1000;

    public static final LoadedChunkLayerManager INSTANCE = new LoadedChunkLayerManager();

    /** Set from the network thread; the snapshot itself is only read on the client thread. */
    private volatile boolean dirty = false;
    private List<CoordinatesChunk> chunks = Collections.emptyList();
    private long lastRequest = 0;
    private long lastFingerprint = 0;

    private LoadedChunkLayerManager() {
        super(LoadedChunkButtonManager.INSTANCE);
    }

    /** Starts listening for chunk data without putting a button on any map yet. */
    public static void init() {
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED, INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED_CLEAR, INSTANCE);
    }

    @Override
    protected @Nullable LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        UniversalInteractableRenderer renderer = new UniversalInteractableRenderer(manager);
        renderer.withRenderStep(location -> new LoadedChunkRenderStep((LoadedChunkLocation) location));

        if (mod == SupportedMods.JourneyMap && Util.isJourneyMapV6Installed()) {
            renderer.withJourneyMapV6Overlays(
                    location -> ChunkPolygonJM6.create(
                            location,
                            ((LoadedChunkLocation) location).getColor(),
                            LoadedChunkRenderStep.FILL_ALPHA / 255f),
                    true);
        }
        return renderer;
    }

    @Override
    public void onUpdatePre(int minX, int maxX, int minZ, int maxZ) {
        // Fullscreen integrations recache enabled layers whether or not they are toggled on.
        if (!isLayerActive()) return;

        if (dirty) {
            dirty = false;
            List<CoordinatesChunk> updated = ChunkManager.INSTANCE.getClientLoadedChunks();
            long fingerprint = fingerprint(updated);

            // The set is resent every second whether or not it moved; rebuilding an identical one would churn
            // every JourneyMap 6 overlay for nothing.
            if (fingerprint != lastFingerprint) {
                lastFingerprint = fingerprint;
                chunks = updated;
                clearFullCache();
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastRequest < REQUEST_INTERVAL_MS) return;

        lastRequest = now;
        PacketManager.sendToServer(
                new PacketReqData(
                        Message.LIST_CHUNK_LOADED,
                        new SerialInt(Minecraft.getMinecraft().thePlayer.dimension)));
    }

    @Override
    protected Collection<LoadedChunkLocation> generateVisibleLocations(int minBlockX, int minBlockZ, int maxBlockX,
            int maxBlockZ, int dimension) {
        List<LoadedChunkLocation> locations = new ArrayList<>();

        for (CoordinatesChunk chunk : chunks) {
            if (chunk.dim != dimension) continue;
            if (chunk.x + 15 < minBlockX || chunk.x > maxBlockX) continue;
            if (chunk.z + 15 < minBlockZ || chunk.z > maxBlockZ) continue;

            locations.add(new LoadedChunkLocation(chunk));
        }
        return locations;
    }

    @Override
    public void onLayerToggled(boolean toEnable) {
        super.onLayerToggled(toEnable);
        // Keep the last snapshot so re-enabling redraws immediately.
        if (toEnable) lastRequest = 0; // request on the next recache
    }

    /** Order-independent, because the server builds the list from a HashSet. Metadata matters: it is the colour. */
    private static long fingerprint(List<CoordinatesChunk> chunks) {
        long hash = chunks.size();
        for (CoordinatesChunk chunk : chunks) hash += 31L * chunk.hashCode() + chunk.metadata;
        return hash;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        if (msg != Message.LIST_CHUNK_LOADED && msg != Message.LIST_CHUNK_LOADED_CLEAR) return false;

        // ChunkManager accumulates the batches. A clear means the previous batch finished arriving, so that is the
        // only point where a complete set is available to read.
        if (msg == Message.LIST_CHUNK_LOADED_CLEAR) dirty = true;
        return true;
    }
}
