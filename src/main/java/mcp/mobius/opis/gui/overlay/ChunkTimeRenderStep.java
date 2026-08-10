package mcp.mobius.opis.gui.overlay;

import java.util.List;

import com.gtnewhorizons.navigator.api.model.steps.UniversalLocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.modOpis;

public class ChunkTimeRenderStep extends UniversalLocationInteractableStep<ChunkTimeLocation> {

    public ChunkTimeRenderStep(ChunkTimeLocation location) {
        super(location);
    }

    @Override
    public void draw(double x, double y, float drawScale, double zoom) {
        DrawUtils.drawRect(x, y, getAdjustedWidth(), getAdjustedHeight(), location.getColor(), 200);
        DrawUtils.drawHollowRect(x, y, getAdjustedWidth(), getAdjustedHeight(), 0x000000, 255);
    }

    @Override
    public void getTooltip(List<String> list) {
        StatsChunk stats = location.getStats();
        CoordinatesChunk chunk = stats.getChunk();

        list.add(String.format("Chunk [%d, %d]", chunk.chunkX, chunk.chunkZ));
        list.add(
                modOpis.microseconds ? String.format("%.3f µs", stats.getDataSum() / 1000.0)
                        : String.format("%.5f ms", stats.getDataSum() / 1000.0 / 1000.0));
        list.add(String.format("%d tile entities, %d entities", stats.tileEntities, stats.entities));
    }
}
