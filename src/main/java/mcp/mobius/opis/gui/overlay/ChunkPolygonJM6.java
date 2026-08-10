package mcp.mobius.opis.gui.overlay;

import java.util.Collection;
import java.util.Collections;

import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;

import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.common.Context;
import journeymap.api.v2.common.util.BlockPos;

/**
 * Builds a chunk-sized native JourneyMap 6 overlay. Universal render steps only reach the JourneyMap 6 fullscreen map,
 * so the minimap needs one of these. Loads only when JourneyMap 6 is installed.
 */
public final class ChunkPolygonJM6 {

    private static final int CHUNK_SIZE = 16;

    private ChunkPolygonJM6() {}

    public static Collection<PolygonOverlay> create(ILocationProvider location, int fillColor, float fillOpacity) {
        int x = (int) Math.floor(location.getBlockX());
        int z = (int) Math.floor(location.getBlockZ());

        // Matches the universal render step: heat/status fill with a solid black chunk border.
        ShapeProperties shape = new ShapeProperties().setFillColor(fillColor).setFillOpacity(fillOpacity)
                .setStrokeColor(0x000000).setStrokeOpacity(1f).setStrokeWidth(1f);

        MapPolygon area = new MapPolygon(
                new BlockPos(x, 64, z),
                new BlockPos(x + CHUNK_SIZE, 64, z),
                new BlockPos(x + CHUNK_SIZE, 64, z + CHUNK_SIZE),
                new BlockPos(x, 64, z + CHUNK_SIZE));

        PolygonOverlay overlay = new PolygonOverlay("Opis", location.getDimensionId(), shape, area);
        overlay.setOverlayGroupName(location.getClass().getName())
                .setActiveUIs(Context.UI.Fullscreen, Context.UI.Minimap);

        return Collections.singletonList(overlay);
    }
}
