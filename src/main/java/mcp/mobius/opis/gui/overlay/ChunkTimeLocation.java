package mcp.mobius.opis.gui.overlay;

import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;

import mcp.mobius.opis.data.holders.stats.StatsChunk;

public class ChunkTimeLocation implements ILocationProvider {

    private final StatsChunk stats;
    private final double heat;

    /**
     * @param stats server-side timing for one chunk
     * @param heat  update time normalized against the slowest reported chunk, 0..1
     */
    public ChunkTimeLocation(StatsChunk stats, double heat) {
        this.stats = stats;
        this.heat = heat;
    }

    @Override
    public int getDimensionId() {
        return stats.getChunk().dim;
    }

    @Override
    public double getBlockX() {
        return stats.getChunk().x + 0.5;
    }

    @Override
    public double getBlockZ() {
        return stats.getChunk().z + 0.5;
    }

    public StatsChunk getStats() {
        return stats;
    }

    /** Blue for the fastest chunks, red for the slowest one in the snapshot. */
    public int getColor() {
        int red = (int) Math.ceil(heat * 255.0);
        return (red << 16) | (255 - red);
    }
}
