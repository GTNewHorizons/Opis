package mcp.mobius.opis.gui.overlay;

import java.util.Collection;
import java.util.Collections;

import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.common.Context;
import journeymap.api.v2.common.util.BlockPos;

/**
 * Native JourneyMap 6 rendering for the chunk timing layer. Universal render steps only reach the JourneyMap 6
 * fullscreen map, so the minimap needs a real overlay. Loads only when JourneyMap 6 is installed.
 */
public final class ChunkTimeOverlayJM6 {

    private static final int CHUNK_SIZE = 16;

    private ChunkTimeOverlayJM6() {}

    public static Collection<PolygonOverlay> create(ChunkTimeLocation location) {
        int x = (int) Math.floor(location.getBlockX());
        int z = (int) Math.floor(location.getBlockZ());

        // Matches the universal render step: filled heat colour with a solid black chunk border.
        ShapeProperties shape = new ShapeProperties().setFillColor(location.getColor()).setFillOpacity(200f / 255f)
                .setStrokeColor(0x000000).setStrokeOpacity(1f).setStrokeWidth(1f);

        MapPolygon area = new MapPolygon(
                new BlockPos(x, 64, z),
                new BlockPos(x + CHUNK_SIZE, 64, z),
                new BlockPos(x + CHUNK_SIZE, 64, z + CHUNK_SIZE),
                new BlockPos(x, 64, z + CHUNK_SIZE));

        PolygonOverlay overlay = new PolygonOverlay("Opis", location.getDimensionId(), shape, area);
        overlay.setOverlayGroupName(ChunkTimeLocation.class.getName())
                .setActiveUIs(Context.UI.Fullscreen, Context.UI.Minimap);

        return Collections.singletonList(overlay);
    }
}
