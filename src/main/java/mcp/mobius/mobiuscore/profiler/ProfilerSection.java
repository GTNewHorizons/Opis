package mcp.mobius.mobiuscore.profiler;

import java.util.EnumSet;

import cpw.mods.fml.relauncher.Side;

public enum ProfilerSection implements IProfilerBase {

    DIMENSION_TICK(RunType.REALTIME, Side.SERVER), // Global section around the ticks for each dim (Blocks & ents).
    DIMENSION_BLOCKTICK(RunType.ONREQUEST, Side.SERVER), // Subsection for dimension block tick.
    ENTITY_UPDATETIME(RunType.ONREQUEST, Side.SERVER), // Profiling of the entity tick time, per entity.
    TICK(RunType.REALTIME, Side.SERVER), // Tick timing profiling
    TILEENT_UPDATETIME(RunType.ONREQUEST, Side.SERVER), // Profiling of the TileEntity tick time, per TE.
    // HANDLER_TICKSTART (RunType.ONREQUEST, EnumSet.of(Side.CLIENT, Side.SERVER)), //Server handler for ServerTick
    // start.
    // HANDLER_TICKSTOP (RunType.ONREQUEST, EnumSet.of(Side.CLIENT, Side.SERVER)), //Server handler for ServerTick
    // stop.
    PACKET_INBOUND(RunType.REALTIME, Side.SERVER), // Outbound packet analysis
    PACKET_OUTBOUND(RunType.REALTIME, Side.SERVER), // Inbound packet analysis
    // PACKETFML_INBOUND (RunType.REALTIME, Side.SERVER), //Outbound packet analysis
    // PACKETFML_OUTBOUND (RunType.REALTIME, Side.SERVER), //Inbound packet analysis

    NETWORK_TICK(RunType.ONREQUEST, Side.SERVER), // The time it takes for the server to handle the packets during a
                                                  // tick.
    EVENT_INVOKE(RunType.ONREQUEST, EnumSet.of(Side.CLIENT, Side.SERVER)), // Timing of the event invokation

    RENDER_TILEENTITY(RunType.ONREQUEST, Side.CLIENT), // Profiler for TileEnt rendering
    RENDER_ENTITY(RunType.ONREQUEST, Side.CLIENT), // Profiler for Entity rendering
    RENDER_BLOCK(RunType.ONREQUEST, Side.CLIENT); // Profiler for Block rendering

    public enum RunType {
        REALTIME,
        ONREQUEST;
    }

    private EnumSet<Side> sides;
    private RunType runType;
    private IProfilerBase profiler = new DummyProfiler();;
    private IProfilerBase profilerSuspended = new DummyProfiler();;

    public static long timeStampLastRun;

    private ProfilerSection(RunType runType, Side side) {
        this.runType = runType;
        this.profiler = new DummyProfiler();
        this.sides = EnumSet.of(side);
    }

    private ProfilerSection(RunType runType, EnumSet<Side> sides) {
        this.runType = runType;
        this.profiler = new DummyProfiler();
        this.sides = sides;
    }

    public RunType getRunType() {
        return this.runType;
    }

    public EnumSet<Side> getSide() {
        return this.sides;
    }

    public IProfilerBase getProfiler() {
        return this.profilerSuspended;
    }

    public void setProfiler(IProfilerBase profiler) {
        this.profilerSuspended = profiler;
        if (this.runType == RunType.REALTIME) this.profiler = profiler;
    }

    public void activate() {
        this.profiler = profilerSuspended;
        this.timeStampLastRun = System.currentTimeMillis();
    }

    public void desactivate() {
        if (this.runType == RunType.ONREQUEST) this.profiler = new DummyProfiler();
    }

    public static void activateAll(Side trgside) {
        for (ProfilerSection section : ProfilerSection.values())
            if (section.sides.contains(trgside)) section.activate();
    }

    public static void desactivateAll(Side trgside) {
        for (ProfilerSection section : ProfilerSection.values())
            if (section.sides.contains(trgside)) section.desactivate();
    }

    public static void resetAll(Side trgside) {
        for (ProfilerSection section : ProfilerSection.values()) if (section.sides.contains(trgside)) section.reset();
    }

    public static String getClassName() {
        return ProfilerSection.class.getCanonicalName().replace(".", "/");
    }

    public static String getTypeName() {
        return "L" + ProfilerSection.getClassName() + ";";
    }

    @Override
    public void reset() {
        this.profiler.reset();
        this.profilerSuspended.reset();
    }

    @Override
    public void start() {
        this.profiler.start();
    }

    @Override
    public void stop() {
        this.profiler.stop();
    }

    @Override
    public void start(Object key) {
        this.profiler.start(key);
    }

    @Override
    public void stop(Object key) {
        this.profiler.stop(key);
    }

    @Override
    public void start(Object key1, Object key2) {
        this.profiler.start(key1, key2);
    }

    @Override
    public void stop(Object key1, Object key2) {
        this.profiler.stop(key1, key2);
    }

    @Override
    public void start(Object key1, Object key2, Object key3) {
        this.profiler.start(key1, key2, key3);
    }

    @Override
    public void stop(Object key1, Object key2, Object key3) {
        this.profiler.stop(key1, key2, key3);
    }

    @Override
    public void start(Object key1, Object key2, Object key3, Object key4) {
        this.profiler.start(key1, key2, key3, key4);
    }

    @Override
    public void stop(Object key1, Object key2, Object key3, Object key4) {
        this.profiler.stop(key1, key2, key3, key4);
    }
}
