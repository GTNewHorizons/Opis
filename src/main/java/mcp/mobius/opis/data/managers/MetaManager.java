package mcp.mobius.opis.data.managers;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.events.OpisServerTickHandler;
import mcp.mobius.opis.modOpis;
import cpw.mods.fml.relauncher.Side;

public class MetaManager {

    public static void reset() {
        modOpis.profilerRun = false;
        modOpis.selectedBlock = null;
        OpisServerTickHandler.INSTANCE.profilerRunningTicks = 0;

        ProfilerSection.resetAll(Side.SERVER);
        ProfilerSection.desactivateAll(Side.SERVER);
        // ProfilerSection.resetAll(Side.CLIENT);
        // ProfilerSection.desactivateAll(Side.CLIENT);
    }
}
