package mcp.mobius.mobiuscore.profiler;

public class DummyProfiler implements IProfilerBase {
    @Override
    public void reset() {}

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public void start(Object key) {}

    @Override
    public void stop(Object key) {}

    @Override
    public void start(Object key1, Object key2) {}

    @Override
    public void stop(Object key1, Object key2) {}

    @Override
    public void start(Object key1, Object key2, Object key3) {}

    @Override
    public void stop(Object key1, Object key2, Object key3) {}

    @Override
    public void start(Object key1, Object key2, Object key3, Object key4) {}

    @Override
    public void stop(Object key1, Object key2, Object key3, Object key4) {}
}
