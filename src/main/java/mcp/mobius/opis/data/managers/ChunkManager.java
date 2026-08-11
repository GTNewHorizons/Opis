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
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public enum ChunkManager implements IMessageHandler {

    INSTANCE;

    private ArrayList<CoordinatesChunk> chunksLoad = new ArrayList<CoordinatesChunk>();
    private ArrayList<CoordinatesChunk> chunksLoadComplete = new ArrayList<CoordinatesChunk>();

    public synchronized void addLoadedChunks(ArrayList<ISerializable> data) {
        // chunksLoad.clear();
        for (ISerializable chunk : data) {
            chunksLoad.add((CoordinatesChunk) chunk);
        }
    }

    /** The clear arrives after a batch, so it commits it. Readers never see a half-delivered set. */
    public synchronized void swapLoadedChunks() {
        chunksLoadComplete = chunksLoad;
        chunksLoad = new ArrayList<CoordinatesChunk>();
    }

    /** Last complete set reported to this client, unlike {@link #getLoadedChunks(int)} which collects server-side. */
    public synchronized ArrayList<CoordinatesChunk> getClientLoadedChunks() {
        return new ArrayList<>(chunksLoadComplete);
    }

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
        ArrayList<StatsChunk> chunks = this.getChunksUpdateTime();
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

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_CHUNK_LOADED: {
                this.addLoadedChunks(rawdata.array);
                break;
            }
            case LIST_CHUNK_LOADED_CLEAR: {
                this.swapLoadedChunks();
                break;
            }
            default:
                return false;
        }
        return true;
    }
}
