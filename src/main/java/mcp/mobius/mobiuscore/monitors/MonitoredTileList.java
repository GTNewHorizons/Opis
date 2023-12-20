package mcp.mobius.mobiuscore.monitors;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import mcp.mobius.mobiuscore.asm.CoreDescription;

public class MonitoredTileList<E> extends MonitoredList<E> {

    // private Map<String, Integer> count = new HashMap<String, Integer>();
    private Table<Block, Integer, Integer> count = HashBasedTable.create();
    private Map<Object, BlockData> blockdata = new WeakHashMap<>();

    private static class BlockData {

        public final Block block;
        public final int meta;

        public BlockData(Block b, int m) {
            this.block = b;
            this.meta = m;
        }
    }

    @Override
    protected void addCount(E e) {
        TileEntity te = ((TileEntity) e);
        if (te == null || te.getWorldObj() == null) return;

        Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
        int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

        blockdata.put(te, new BlockData(block, meta));

        try {
            count.put(block, meta, count.get(block, meta) + 1);
        } catch (NullPointerException ex) {
            count.put(block, meta, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            count.put(block, meta, 1);
        }
    }

    @Override
    protected void removeCount(int index) {
        this.removeCount(this.get(index));
    }

    @Override
    protected void removeCount(Object o) {
        // TODO THIS IS WHERE THE CHUNK KEEP GETTING LOADED.
        // TODO SHOULD STORE BLOCK/META IN A TABLE ALONG WITH THE TE IN ORDER TO DECREASE THOSE
        // WITHOUT ACCESSING THE WORLD !!!!

        /*
         * TileEntity te = ((TileEntity)o); if (te == null || te.getWorldObj() == null) return; Block block =
         * te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord); int meta =
         * te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
         */

        if (blockdata.containsKey(o)) {
            BlockData d = blockdata.get(o);
            try {
                this.count.put(d.block, d.meta, this.count.get(d.block, d.meta) - 1);
            } catch (NullPointerException e) {
                this.count.put(d.block, d.meta, 0);
            }
        }
    }

    @Override
    protected void clearCount() {
        this.count.clear();
    }

    @Override
    public void printCount() {
        for (Cell<Block, Integer, Integer> c : this.count.cellSet())
            CoreDescription.log.info(String.format("%s | %s : %s", c.getRowKey(), c.getColumnKey(), c.getValue()));
    }

    public Table<Block, Integer, Integer> getCount() {
        return this.count;
    }
}
