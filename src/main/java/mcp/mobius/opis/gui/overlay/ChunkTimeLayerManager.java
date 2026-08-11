package mcp.mobius.opis.gui.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.SwingUtilities;

import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.UniversalInteractableRenderer;
import com.gtnewhorizons.navigator.api.model.steps.LocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.ClickPos;
import com.gtnewhorizons.navigator.api.util.Util;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.SwingUI;
import mcp.mobius.opis.swing.panels.timingserver.PanelTimingChunks;
import mcp.mobius.opis.swing.widgets.JTableStats;

/** Navigator layer showing per-chunk server update time as a heatmap. */
public class ChunkTimeLayerManager extends InteractableLayerManager implements IMessageHandler {

    public static final ChunkTimeLayerManager INSTANCE = new ChunkTimeLayerManager();

    /** Written from the network thread, read during recache. Replaced wholesale, never mutated. */
    private volatile List<StatsChunk> chunkStats = Collections.emptyList();
    private volatile boolean dirty = false;
    private volatile long lastFingerprint = 0;
    private long lastRequest = 0;

    private ChunkTimeLayerManager() {
        super(ChunkTimeButtonManager.INSTANCE);
    }

    /** Listens for data without showing a button yet. */
    public static void init() {
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_CHUNK, INSTANCE);
    }

    @Override
    protected @Nullable LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        UniversalInteractableRenderer renderer = new UniversalInteractableRenderer(manager)
                .withClickAction(this::onClick);
        renderer.withRenderStep(location -> new ChunkTimeRenderStep((ChunkTimeLocation) location));

        if (mod == SupportedMods.JourneyMap && Util.isJourneyMapV6Installed()) {
            // Native overlays are the only route onto the JM6 minimap.
            renderer.withJourneyMapV6Overlays(
                    location -> ChunkPolygonJM6.create(
                            location,
                            ((ChunkTimeLocation) location).getColor(),
                            modOpis.overlayAlphaTiming / 255f),
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
            // Chunks drop out of the top-100 list, so rebuild every location.
            clearFullCache();
        }

        long now = System.currentTimeMillis();
        if (now - lastRequest < modOpis.overlayRefreshInterval) return;

        lastRequest = now;
        PacketManager.sendToServer(new PacketReqData(Message.LIST_TIMING_CHUNK));
    }

    @Override
    protected Collection<ChunkTimeLocation> generateVisibleLocations(int minBlockX, int minBlockZ, int maxBlockX,
            int maxBlockZ, int dimension) {
        List<StatsChunk> stats = chunkStats;

        // Per dimension: the server's top 100 is global, so a hotter dimension would wash this one out.
        double maxTime = 0;
        for (StatsChunk stat : stats) {
            if (stat.getChunk().dim == dimension) maxTime = Math.max(maxTime, stat.getDataSum());
        }
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

    private boolean onClick(ClickPos click) {
        if (!click.isDoubleClick()) return false;

        LocationInteractableStep step = click.getLocationRenderStep();
        if (step == null || !(step.getLocation() instanceof ChunkTimeLocation)) return false;

        CoordinatesChunk chunk = ((ChunkTimeLocation) step.getLocation()).getStats().getChunk();
        SwingUI.instance().showTab(SelectedTab.TIMINGCHUNKS);
        selectChunkRow(chunk);
        return true;
    }

    /** The chunk is only in the table if the server reported it this run. */
    private static void selectChunkRow(CoordinatesChunk chunk) {
        SwingUtilities.invokeLater(() -> {
            PanelTimingChunks panel = (PanelTimingChunks) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.TIMINGCHUNKS);
            if (panel == null || panel.getTable() == null) return;

            JTableStats table = panel.getTable();
            List<ISerializable> rows = table.getTableData();
            if (rows == null) return;

            for (int i = 0; i < rows.size(); i++) {
                if (!chunk.equals(((StatsChunk) rows.get(i)).getChunk())) continue;

                int view = table.convertRowIndexToView(i);
                if (view < 0) return;
                table.setRowSelectionInterval(view, view);
                table.scrollRectToVisible(table.getCellRect(view, 0, true));
                return;
            }
        });
    }

    @Override
    public void onLayerToggled(boolean toEnable) {
        super.onLayerToggled(toEnable);
        // Snapshot is kept so re-enabling redraws immediately.
        if (toEnable) lastRequest = 0;
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

        // Polled every second but only changes per profiler run; rebuilding identical data churns the JM6 overlays.
        if (fingerprint == lastFingerprint) return true;

        lastFingerprint = fingerprint;
        chunkStats = stats;
        dirty = true;
        return true;
    }
}
