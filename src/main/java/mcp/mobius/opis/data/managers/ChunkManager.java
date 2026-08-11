package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;

public enum ChunkManager {

    INSTANCE;

    public synchronized ArrayList<CoordinatesChunk> getLoadedChunks(int dimension) {
        HashSet<CoordinatesChunk> chunkStatus = new HashSet<CoordinatesChunk>();
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world != null) {
            for (ChunkCoordIntPair coord : world.getPersistentChunks().keySet()) {
                chunkStatus.add(new CoordinatesChunk(dimension, coord, (byte) 1));
            }

            for (Object o : ((ChunkProviderServer) world.getChunkProvider()).loadedChunks) {
                Chunk chunk = (Chunk) o;

                chunkStatus.add(new CoordinatesChunk(dimension, chunk.getChunkCoordIntPair(), (byte) 0));
            }
        }

        return new ArrayList<>(chunkStatus);
    }

    /**
     * Reads the profiler maps directly: the Data*.fill() holders do per-element world and name lookups this ignores.
     */
    public synchronized ArrayList<StatsChunk> getChunksUpdateTime() {
        HashMap<CoordinatesChunk, StatsChunk> chunks = new HashMap<CoordinatesChunk, StatsChunk>();

        for (Map.Entry<CoordinatesBlock, DescriptiveStatistics> entry : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME
                .getProfiler()).data.entrySet()) {
            CoordinatesChunk chunk = entry.getKey().asCoordinatesChunk();

            if (!chunks.containsKey(chunk)) chunks.put(chunk, new StatsChunk(chunk));

            chunks.get(chunk).addTileEntity();
            chunks.get(chunk).addMeasure(entry.getValue().getGeometricMean());
        }

        for (Map.Entry<Entity, DescriptiveStatistics> entry : ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME
                .getProfiler()).data.entrySet()) {
            CoordinatesChunk chunk = new CoordinatesBlock(entry.getKey()).asCoordinatesChunk();

            if (!chunks.containsKey(chunk)) chunks.put(chunk, new StatsChunk(chunk));

            chunks.get(chunk).addEntity();
            chunks.get(chunk).addMeasure(entry.getValue().getGeometricMean());
        }

        return new ArrayList<>(chunks.values());
    }

    public ArrayList<StatsChunk> getTopChunks(int quantity) {
        return getTopChunks(quantity, null);
    }

    /** @param dimension restricts the ranking to one world, so a busier one cannot crowd it out entirely */
    public ArrayList<StatsChunk> getTopChunks(int quantity, Integer dimension) {
        ArrayList<StatsChunk> chunks = this.getChunksUpdateTime();
        if (dimension != null) chunks.removeIf(stat -> stat.getChunk().dim != dimension);

        ArrayList<StatsChunk> outList = new ArrayList<>(quantity);
        Collections.sort(chunks);

        for (int i = 0; i < Math.min(quantity, chunks.size()); i++) outList.add(chunks.get(i));

        return outList;
    }

    public int getLoadedChunkAmount() {
        int loadedChunks = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            int loadedChunksForDim = world.getChunkProvider().getLoadedChunkCount();
            loadedChunks += loadedChunksForDim;
            // System.out.printf("[ %2d ] %d chunks\n", world.provider.dimensionId, loadedChunksForDim);
        }
        // System.out.printf("Total : %d chunks\n", loadedChunks);
        return loadedChunks;
    }

    public int getForcedChunkAmount() {
        int forcedChunks = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            forcedChunks += world.getPersistentChunks().size();
        }
        return forcedChunks;
    }

    public void purgeChunks(int dim) {
        WorldServer world = DimensionManager.getWorld(dim);
        if (world == null) return;

        int loadedChunksDelta = 100;

        ((ChunkProviderServer) world.getChunkProvider()).unloadAllChunks();

        while (loadedChunksDelta >= 100) {
            int loadedBefore = world.getChunkProvider().getLoadedChunkCount();
            world.getChunkProvider().unloadQueuedChunks();
            loadedChunksDelta = loadedBefore - world.getChunkProvider().getLoadedChunkCount();
        }
    }

}
