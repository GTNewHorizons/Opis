package mcp.mobius.opis.data.profilers;

import java.util.HashMap;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.Clock.IClock;

import net.minecraft.tileentity.TileEntity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProfilerTileEntityUpdate extends ProfilerAbstract {

    private IClock clock = Clock.getNewClock();
    public HashMap<CoordinatesBlock, DescriptiveStatistics> data = new HashMap<CoordinatesBlock, DescriptiveStatistics>();
    public HashMap<CoordinatesBlock, Class> refs = new HashMap<CoordinatesBlock, Class>();

    @Override
    public void reset() {
        this.data.clear();
        this.refs.clear();
    }

    @Override
    public void start(Object key) {
        TileEntity tileent = (TileEntity) key;
        if (tileent.getWorldObj().isRemote) return;

        CoordinatesBlock coord = new CoordinatesBlock(tileent);

        if (!data.containsKey(coord) || (refs.get(coord) != tileent.getClass())) {
            data.put(coord, new DescriptiveStatistics());
            refs.put(coord, tileent.getClass());
        }
        clock.start();
    }

    @Override
    public void stop(Object key) {
        TileEntity tileent = (TileEntity) key;
        if (tileent.getWorldObj().isRemote) return;

        clock.stop();
        CoordinatesBlock coord = new CoordinatesBlock(tileent);

        data.get(coord).addValue(clock.getDelta());
    }
}
