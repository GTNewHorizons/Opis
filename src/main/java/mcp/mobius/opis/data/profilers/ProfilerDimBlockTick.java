package mcp.mobius.opis.data.profilers;

import java.util.HashMap;

import net.minecraftforge.common.DimensionManager;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.mobiuscore.profiler.IProfilerBase;
import mcp.mobius.opis.data.profilers.Clock.IClock;

public class ProfilerDimBlockTick extends ProfilerAbstract implements IProfilerBase {

    private IClock clock = Clock.getNewClock();
    public HashMap<Integer, DescriptiveStatistics> data = new HashMap<Integer, DescriptiveStatistics>();

    @Override
    public void reset() {
        this.data.clear();
    }

    @Override
    public void start(Object key) {
        Integer dim = (Integer) key;
        if (DimensionManager.getWorld(dim).isRemote) return;

        if (!data.containsKey(dim)) data.put(dim, new DescriptiveStatistics());
        clock.start();
    }

    @Override
    public void stop(Object key) {
        Integer dim = (Integer) key;
        if (DimensionManager.getWorld(dim).isRemote) return;

        clock.stop();
        data.get(dim).addValue(clock.getDelta());
    }
}
