package mcp.mobius.opis.data.profilers;

import java.util.HashMap;

import mcp.mobius.mobiuscore.profiler.IProfilerBase;
import mcp.mobius.opis.data.profilers.Clock.IClock;

import net.minecraft.world.World;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProfilerDimTick extends ProfilerAbstract implements IProfilerBase {

    private IClock clock = Clock.getNewClock();
    public HashMap<Integer, DescriptiveStatistics> data = new HashMap<Integer, DescriptiveStatistics>();

    @Override
    public void reset() {
        this.data.clear();
    }

    @Override
    public void start(Object key) {
        World world = (World) key;
        if (world.isRemote) return;

        if (!data.containsKey(world.provider.dimensionId))
            data.put(world.provider.dimensionId, new DescriptiveStatistics(20));
        clock.start();
    }

    @Override
    public void stop(Object key) {
        World world = (World) key;
        if (world.isRemote) return;

        clock.stop();
        data.get(world.provider.dimensionId).addValue(clock.getDelta());
    }
}
