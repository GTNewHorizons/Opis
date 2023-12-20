package mcp.mobius.opis.data.profilers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.data.profilers.Clock.IClock;

public class ProfilerTick extends ProfilerAbstract {

    private IClock clock = Clock.getNewClock();
    public DescriptiveStatistics data = new DescriptiveStatistics(20);

    @Override
    public void reset() {
        // this.data.clear();
    }

    @Override
    public void start() {
        // DeadManSwitch.instance.setTimer(System.nanoTime());
        this.clock.start();
    }

    @Override
    public void stop() {
        this.clock.stop();
        this.data.addValue(clock.getDelta());
    }
}
