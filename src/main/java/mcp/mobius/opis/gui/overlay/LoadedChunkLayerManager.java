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
import com.gtnewhorizons.navigator.api.model.steps.LocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.ClickPos;
import com.gtnewhorizons.navigator.api.util.Util;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.SwingUI;

/** Navigator layer showing which chunks the server has loaded, and which are held by a ticket. */
public class LoadedChunkLayerManager extends InteractableLayerManager implements IMessageHandler {

    public static final LoadedChunkLayerManager INSTANCE = new LoadedChunkLayerManager();

    /** Network thread only: the server splits a set across several packets and ends it with a clear. */
    private final List<CoordinatesChunk> pending = new ArrayList<>();
    private volatile List<CoordinatesChunk> committed = Collections.emptyList();
    private volatile boolean dirty = false;

    private List<CoordinatesChunk> chunks = Collections.emptyList();
    private long lastRequest = 0;
    private long lastFingerprint = 0;

    private LoadedChunkLayerManager() {
        super(LoadedChunkButtonManager.INSTANCE);
    }

    /** Listens for data without showing a button yet. */
    public static void init() {
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED, INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED_CLEAR, INSTANCE);
    }

    @Override
    protected @Nullable LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        UniversalInteractableRenderer renderer = new UniversalInteractableRenderer(manager)
                .withClickAction(this::onClick);
        renderer.withRenderStep(location -> new LoadedChunkRenderStep((LoadedChunkLocation) location));

        if (mod == SupportedMods.JourneyMap && Util.isJourneyMapV6Installed()) {
            renderer.withJourneyMapV6Overlays(
                    location -> ChunkPolygonJM6.create(
                            location,
                            ((LoadedChunkLocation) location).getColor(),
                            modOpis.overlayAlphaLoaded / 255f),
                    true);
        }
        return renderer;
    }

    @Override
    public void onUpdatePre(int minX, int maxX, int minZ, int maxZ) {
        // Fullscreen integrations recache enabled layers even when toggled off.
        if (!isLayerActive()) return;

        if (dirty) {
            dirty = false;
            List<CoordinatesChunk> updated = committed;
            long fingerprint = fingerprint(updated);

            // Resent every second even when unchanged; rebuilding identical data churns the JM6 overlays.
            if (fingerprint != lastFingerprint) {
                lastFingerprint = fingerprint;
                chunks = updated;
                clearFullCache();
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastRequest < modOpis.overlayRefreshInterval) return;

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

    /** Only forced chunks have a table to open, so other clicks are left unconsumed. */
    private boolean onClick(ClickPos click) {
        if (!click.isDoubleClick()) return false;

        LocationInteractableStep step = click.getLocationRenderStep();
        if (step == null || !(step.getLocation() instanceof LoadedChunkLocation)) return false;
        if (!((LoadedChunkLocation) step.getLocation()).isForced()) return false;

        SwingUI.instance().showTab(SelectedTab.FORCELOADS);
        return true;
    }

    @Override
    public void onLayerToggled(boolean toEnable) {
        super.onLayerToggled(toEnable);
        // Snapshot is kept so re-enabling redraws immediately.
        if (toEnable) lastRequest = 0;
    }

    /**
     * Order-independent, as the server builds the list from a HashSet. The offset keeps chunk 0,0 of dimension 0 from
     * hashing to zero, which would swallow its metadata.
     */
    private static long fingerprint(List<CoordinatesChunk> chunks) {
        long hash = chunks.size();
        for (CoordinatesChunk chunk : chunks) hash += (chunk.hashCode() + 0x9E3779B9L) * (31L + chunk.metadata);
        return hash;
    }

    /** Wipes everything tied to one server, so a later session cannot show its chunks. */
    public void clearState() {
        pending.clear();
        committed = Collections.emptyList();
        dirty = false;
        chunks = Collections.emptyList();
        lastFingerprint = 0;
        clearFullCache();
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        if (msg == Message.LIST_CHUNK_LOADED) {
            for (ISerializable chunk : rawdata.array) pending.add((CoordinatesChunk) chunk);
            return true;
        }
        if (msg != Message.LIST_CHUNK_LOADED_CLEAR) return false;

        // Sent after a batch, so it marks the set complete. Accumulating here rather than in ChunkManager keeps
        // commit and dirty in one handler, where message dispatch order cannot split them.
        committed = new ArrayList<>(pending);
        pending.clear();
        dirty = true;
        return true;
    }
}
