package mcp.mobius.opis.gui.overlay;

import java.util.List;

import com.gtnewhorizons.navigator.api.model.steps.UniversalLocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public class LoadedChunkRenderStep extends UniversalLocationInteractableStep<LoadedChunkLocation> {

    /** Low, because this layer covers large areas and the map underneath still has to be readable. */
    static final int FILL_ALPHA = 80;

    public LoadedChunkRenderStep(LoadedChunkLocation location) {
        super(location);
    }

    @Override
    public void draw(double x, double y, float drawScale, double zoom) {
        DrawUtils.drawRect(x, y, getAdjustedWidth(), getAdjustedHeight(), location.getColor(), FILL_ALPHA);
        DrawUtils.drawHollowRect(x, y, getAdjustedWidth(), getAdjustedHeight(), 0x000000, 255);
    }

    @Override
    public void getTooltip(List<String> list) {
        CoordinatesChunk chunk = location.getChunk();

        list.add(String.format("Chunk [%d, %d]", chunk.chunkX, chunk.chunkZ));
        list.add(location.isForced() ? "Force loaded" : "Game loaded");
    }
}
