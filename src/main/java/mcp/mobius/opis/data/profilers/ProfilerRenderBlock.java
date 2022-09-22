package mcp.mobius.opis.data.profilers;

import java.util.HashMap;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.Clock.IClock;
import net.minecraft.client.Minecraft;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProfilerRenderBlock extends ProfilerAbstract {

    private IClock clock = Clock.getNewClock();
    public HashMap<CoordinatesBlock, DescriptiveStatistics> data =
            new HashMap<CoordinatesBlock, DescriptiveStatistics>();

    @Override
    public void reset() {}

    @Override
    public void start(Object key1, Object key2, Object key3, Object key4) {
        CoordinatesBlock coord = new CoordinatesBlock(
                Minecraft.getMinecraft().theWorld.provider.dimensionId, (Integer) key2, (Integer) key3, (Integer) key4);

        if (!data.containsKey(coord)) data.put(coord, new DescriptiveStatistics());
        clock.start();
    }

    @Override
    public void stop(Object key1, Object key2, Object key3, Object key4) {
        CoordinatesBlock coord = new CoordinatesBlock(
                Minecraft.getMinecraft().theWorld.provider.dimensionId, (Integer) key2, (Integer) key3, (Integer) key4);

        clock.stop();
        data.get(coord).addValue(clock.getDelta());
    }
}
