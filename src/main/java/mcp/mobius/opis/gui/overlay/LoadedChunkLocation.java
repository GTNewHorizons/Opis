package mcp.mobius.opis.gui.overlay;

import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public class LoadedChunkLocation implements ILocationProvider {

    private final CoordinatesChunk chunk;

    public LoadedChunkLocation(CoordinatesChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int getDimensionId() {
        return chunk.dim;
    }

    @Override
    public double getBlockX() {
        return chunk.x + 0.5;
    }

    @Override
    public double getBlockZ() {
        return chunk.z + 0.5;
    }

    public CoordinatesChunk getChunk() {
        return chunk;
    }

    /** Chunk held by a ticket rather than by normal gameplay loading. */
    public boolean isForced() {
        return chunk.metadata != 0;
    }

    public int getColor() {
        return isForced() ? 0x0000FF : 0x00FF00;
    }
}
