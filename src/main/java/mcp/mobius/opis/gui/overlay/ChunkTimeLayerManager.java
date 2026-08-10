package mcp.mobius.opis.gui.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.gtnewhorizons.navigator.api.NavigatorApi;
import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.UniversalInteractableRenderer;
import com.gtnewhorizons.navigator.api.util.Util;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;

/**
 * Navigator layer showing per-chunk server update time as a heatmap. Replaces the MapWriter overlay Opis used to ship.
 */
public class ChunkTimeLayerManager extends InteractableLayerManager implements IMessageHandler {

    private static final long REQUEST_INTERVAL_MS = 1000;

    public static final ChunkTimeLayerManager INSTANCE = new ChunkTimeLayerManager();

    /** Written from the network thread, read during recache. Replaced wholesale, never mutated. */
    private volatile List<StatsChunk> chunkStats = Collections.emptyList();
    private volatile boolean dirty = false;
    private volatile long lastFingerprint = 0;
    private long lastRequest = 0;
    private static boolean shown = false;

    private ChunkTimeLayerManager() {
        super(ChunkTimeButtonManager.INSTANCE);
    }

    /** Starts listening for timing data without putting a button on any map yet. */
    public static void init() {
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_CHUNK, INSTANCE);
    }

    /**
     * Makes the layer visible to the installed map mods. Deferred until the player actually uses Opis so the button
     * does not clutter everyone's map. Idempotent, and must run on the client thread because Navigator's layer list is
     * iterated while rendering.
     */
    public static void show() {
        if (shown) return;
        shown = true;
        NavigatorApi.registerLayerManager(INSTANCE);
    }

    @Override
    protected @Nullable LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        UniversalInteractableRenderer renderer = new UniversalInteractableRenderer(manager);
        renderer.withRenderStep(location -> new ChunkTimeRenderStep((ChunkTimeLocation) location));

        if (mod == SupportedMods.JourneyMap && Util.isJourneyMapV6Installed()) {
            // Native overlays are the only route onto the JM6 minimap, and they replace the fullscreen render step.
            renderer.withJourneyMapV6Overlays(
                    location -> ChunkTimeOverlayJM6.create((ChunkTimeLocation) location),
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
            // Timings are replaced wholesale and chunks drop out of the top-100 list, so rebuild every location.
            clearFullCache();
        }

        long now = System.currentTimeMillis();
        if (now - lastRequest < REQUEST_INTERVAL_MS) return;

        lastRequest = now;
        PacketManager.sendToServer(new PacketReqData(Message.LIST_TIMING_CHUNK));
    }

    @Override
    protected Collection<ChunkTimeLocation> generateVisibleLocations(int minBlockX, int minBlockZ, int maxBlockX,
            int maxBlockZ, int dimension) {
        List<StatsChunk> stats = chunkStats;

        double maxTime = 0;
        for (StatsChunk stat : stats) maxTime = Math.max(maxTime, stat.getDataSum());
        if (maxTime <= 0) return Collections.emptyList();

        List<ChunkTimeLocation> locations = new ArrayList<>();
        for (StatsChunk stat : stats) {
            CoordinatesChunk chunk = stat.getChunk();
            if (chunk.dim != dimension) continue;
            if (chunk.x + 15 < minBlockX || chunk.x > maxBlockX) continue;
            if (chunk.z + 15 < minBlockZ || chunk.z > maxBlockZ) continue;

            locations.add(new ChunkTimeLocation(stat, stat.getDataSum() / maxTime));
        }
        return locations;
    }

    @Override
    public void onLayerToggled(boolean toEnable) {
        super.onLayerToggled(toEnable);
        // Keep the last snapshot so re-enabling redraws immediately.
        if (toEnable) lastRequest = 0; // request on the next recache
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        if (msg != Message.LIST_TIMING_CHUNK) return false;

        List<StatsChunk> stats = new ArrayList<>(rawdata.array.size());
        long fingerprint = 1;
        for (ISerializable data : rawdata.array) {
            StatsChunk stat = (StatsChunk) data;
            stats.add(stat);
            fingerprint = fingerprint * 31 + stat.getChunk().hashCode();
            fingerprint = fingerprint * 31 + Double.doubleToLongBits(stat.getDataSum());
        }

        // Timings only change when a profiler run finishes, but we poll every second. Rebuilding on an
        // identical snapshot would churn every JourneyMap 6 overlay once a second for nothing.
        if (fingerprint == lastFingerprint) return true;

        lastFingerprint = fingerprint;
        chunkStats = stats;
        dirty = true;
        return true;
    }
}
