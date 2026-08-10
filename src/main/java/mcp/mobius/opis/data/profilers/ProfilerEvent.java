package mcp.mobius.opis.data.profilers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.HashBasedTable;

import cpw.mods.fml.common.ModContainer;
import mcp.mobius.opis.data.profilers.Clock.IClock;

/**
 * Times Forge event invocations. This profiler runs on both sides, so a single instance is shared by the client and
 * server threads: the tables are only safe under the instance lock, and readers must synchronize on the profiler while
 * iterating them.
 */
public class ProfilerEvent extends ProfilerAbstract {

    /** One clock per thread; a shared clock would interleave start/stop between sides and yield junk deltas. */
    private final ThreadLocal<IClock> clock = ThreadLocal.withInitial(Clock::getNewClock);

    public HashBasedTable<Class<?>, String, DescriptiveStatistics> data = HashBasedTable.create();
    public HashBasedTable<Class<?>, String, String> dataMod = HashBasedTable.create();

    public HashBasedTable<Class<?>, String, DescriptiveStatistics> dataTick = HashBasedTable.create();
    public HashBasedTable<Class<?>, String, String> dataModTick = HashBasedTable.create();

    @Override
    public synchronized void reset() {
        data.clear();
    }

    @Override
    public void start() {
        clock.get().start();
    }

    @Override
    public void stop(Object event, Object pkg, Object handler, Object mod) {
        clock.get().stop();
        long delta = clock.get().getDelta();

        Class<?> type = event.getClass();
        String name = pkg + "|" + handler.getClass().getSimpleName();
        boolean isTick = type.getSimpleName().contains("TickEvent");

        synchronized (this) {
            HashBasedTable<Class<?>, String, DescriptiveStatistics> stats = isTick ? dataTick : data;
            DescriptiveStatistics measures = stats.get(type, name);

            if (measures == null) {
                measures = new DescriptiveStatistics(250);
                stats.put(type, name, measures);
                (isTick ? dataModTick : dataMod)
                        .put(type, name, mod instanceof ModContainer ? ((ModContainer) mod).getName() : "unknown");
            }
            measures.addValue(delta);
        }
    }
}
