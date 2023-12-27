package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import mcp.mobius.mobiuscore.monitors.MonitoredTileList;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.modOpis;

public enum TileEntityManager {

    INSTANCE;

    public HashMap<CoordinatesChunk, StatsChunk> getTimes(int dim) {
        HashMap<CoordinatesChunk, StatsChunk> chunks = new HashMap<CoordinatesChunk, StatsChunk>();

        for (CoordinatesBlock coord : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data
                .keySet()) {
            if (coord.dim == dim) {

                CoordinatesChunk coordC = new CoordinatesChunk(coord);
                if (!(chunks.containsKey(coordC))) chunks.put(coordC, new StatsChunk());

                chunks.get(coordC).addEntity();
                chunks.get(coordC).addMeasure(
                        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coord)
                                .getGeometricMean());
            }
        }
        return chunks;
    }

    private void cleanUpStats() {

        /*
         * HashSet<CoordinatesBlock> dirty = new HashSet<CoordinatesBlock>(); for (CoordinatesBlock tecoord :
         * TileEntityManager.stats.keySet()){ World world = DimensionManager.getWorld(tecoord.dim); int blockID =
         * world.getBlockId(tecoord.x, tecoord.y, tecoord.z); short blockMeta = (short)world.getBlockMetadata(tecoord.x,
         * tecoord.y, tecoord.z); if ((blockID != TileEntityManager.stats.get(tecoord).getID()) || (blockMeta !=
         * TileEntityManager.stats.get(tecoord).getMeta())){ dirty.add(tecoord); } } for (CoordinatesBlock tecoord :
         * dirty){ stats.remove(tecoord); references.remove(tecoord); }
         */

    }

    public ArrayList<DataBlockTileEntity> getTileEntitiesInChunk(CoordinatesChunk coord) {
        cleanUpStats();

        ArrayList<DataBlockTileEntity> returnList = new ArrayList<DataBlockTileEntity>();

        for (CoordinatesBlock tecoord : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME
                .getProfiler()).data.keySet()) {
            if (coord.equals(tecoord.asCoordinatesChunk())) {
                DataBlockTileEntity testats = new DataBlockTileEntity().fill(tecoord);

                returnList.add(testats);
            }
        }

        return returnList;
    }

    public ArrayList<DataBlockTileEntity> getWorses(int amount) {
        ArrayList<DataBlockTileEntity> sorted = new ArrayList<DataBlockTileEntity>();
        ArrayList<DataBlockTileEntity> topEntities = new ArrayList<DataBlockTileEntity>();

        for (CoordinatesBlock coord : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data
                .keySet())
            sorted.add(new DataBlockTileEntity().fill(coord));

        Collections.sort(sorted);

        for (int i = 0; i < Math.min(amount, sorted.size()); i++) topEntities.add(sorted.get(i));

        return topEntities;
    }

    public DataTiming getTotalUpdateTime() {
        double updateTime = 0D;
        for (CoordinatesBlock coords : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME
                .getProfiler()).data.keySet()) {
            updateTime += ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coords)
                    .getGeometricMean();
        }
        return new DataTiming(updateTime);
    }

    public int getAmountTileEntities() {
        int amountTileEntities = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            amountTileEntities += world.loadedTileEntityList.size();
        }
        return amountTileEntities;
    }

    public ArrayList<DataTileEntity> getOrphans() {
        ArrayList<DataTileEntity> orphans = new ArrayList<DataTileEntity>();
        HashMap<CoordinatesBlock, DataTileEntity> coordHashset = new HashMap<CoordinatesBlock, DataTileEntity>();
        HashSet<Integer> registeredEntities = new HashSet<Integer>();

        for (WorldServer world : DimensionManager.getWorlds()) {
            for (Object o : world.loadedTileEntityList.toArray()) {
                TileEntity tileEntity = (TileEntity) o;
                if (tileEntity == null) continue;
                CoordinatesBlock coord = new CoordinatesBlock(
                        world.provider.dimensionId,
                        tileEntity.xCoord,
                        tileEntity.yCoord,
                        tileEntity.zCoord);
                int hash = System.identityHashCode(tileEntity);

                if (registeredEntities.contains(hash)) continue; // This entitie has already been seen;

                Block block = world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                if (block == Blocks.air || block == null
                        || !block.hasTileEntity(0)
                        || world.getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) == null
                        || world.getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord).getClass()
                                != tileEntity.getClass()) {

                    orphans.add(new DataTileEntity().fill(tileEntity, "Orphan"));
                    registeredEntities.add(hash);
                }

                if (coordHashset.containsKey(coord)) {
                    if (!registeredEntities.contains(hash))
                        orphans.add(new DataTileEntity().fill(tileEntity, "Duplicate"));

                    if (!registeredEntities.contains(coordHashset.get(coord).hashCode))
                        orphans.add(coordHashset.get(coord));
                }

                if (!coordHashset.containsKey(coord)) {
                    coordHashset.put(coord, new DataTileEntity().fill(tileEntity, "Duplicate"));
                }
            }
        }

        modOpis.log.warn(String.format("Found %d potential orphans !", orphans.size()));

        return orphans;
    }

    public ArrayList<AmountHolder> getCumulativeAmountTileEntities() {
        ArrayList<AmountHolder> cumData = new ArrayList<AmountHolder>();

        for (int dim : DimensionManager.getIDs()) {
            World world = DimensionManager.getWorld(dim);
            if (world == null) continue;

            if (!(world.loadedTileEntityList instanceof MonitoredTileList)) {
                modOpis.log.info(
                        "Improper type detected for tile entity list in world " + dim
                                + ". Regenerating tracking list.");

                /*
                 * MonitoredTileList will, whenever a tile entity is added to it, load the block at the tile entity's
                 * position. This can cause Minecraft to load chunks, which will result in more tile entities being
                 * added to world.loadedTileEntityList. If we iterate through world.loadedTileEntityList and add its
                 * entries to newList, this would result in getting a ConcurrentModificationException. We get around
                 * this by using a regular for-loop instead of a for-each loop. As a bonus, since the for-loop checks
                 * world.loadedTileEntityList.size() at the start of each loop iteration, it'll pick up any new entries
                 * in world.loadedTileEntityList immediately, and will add them to newList without requiring us to check
                 * separately. However, this does require a few assumptions about what can happen in the for-loop body:
                 * 1. New entries are always added at the end of world.loadedTileEntityList 2. Entries are never removed
                 * from world.loadedTileEntityList WARNING: if any of those assumptions are broken, we will likely get
                 * unexpected behavior that is also extremely difficult to debug!
                 */

                List<TileEntity> newList = new MonitoredTileList<TileEntity>();
                for (int i = 0; i < world.loadedTileEntityList.size(); i++) {
                    newList.add((TileEntity) world.loadedTileEntityList.get(i));
                }

                world.loadedTileEntityList = newList;
            }

            Table<Block, Integer, Integer> count = ((MonitoredTileList<?>) world.loadedTileEntityList).getCount();
            Table<String, String, Integer> reducedCount = HashBasedTable.create();

            for (Cell<Block, Integer, Integer> c : count.cellSet()) {
                if (c.getValue() > 0) {
                    String stackName = ModIdentification.getStackName(c.getRowKey(), c.getColumnKey());
                    String modName = ModIdentification.getModStackName(c.getRowKey(), c.getColumnKey());

                    try {
                        reducedCount.put(stackName, modName, reducedCount.get(stackName, modName) + c.getValue());
                    } catch (Exception e) {
                        reducedCount.put(stackName, modName, c.getValue());
                    }
                }
            }

            for (Cell<String, String, Integer> c : reducedCount.cellSet()) {
                cumData.add(new AmountHolder(c.getRowKey(), c.getValue(), c.getColumnKey()));
            }
        }
        return cumData;
    }

    public ArrayList<DataBlockTileEntityPerClass> getCumulativeTimingTileEntities() {
        HashBasedTable<Integer, Integer, DataBlockTileEntityPerClass> data = HashBasedTable.create();

        for (CoordinatesBlock coord : ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data
                .keySet()) {
            World world = DimensionManager.getWorld(coord.dim);
            int id = Block.getIdFromBlock(world.getBlock(coord.x, coord.y, coord.z));
            int meta = world.getBlockMetadata(coord.x, coord.y, coord.z);

            if (!data.contains(id, meta)) data.put(id, meta, new DataBlockTileEntityPerClass(id, meta));

            data.get(id, meta).add(
                    ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coord)
                            .getGeometricMean());
        }

        return new ArrayList<DataBlockTileEntityPerClass>(data.values());
    }
}
