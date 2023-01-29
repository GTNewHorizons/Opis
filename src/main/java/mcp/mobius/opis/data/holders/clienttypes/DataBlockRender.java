package mcp.mobius.opis.data.holders.clienttypes;

import java.util.HashMap;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.profilers.ProfilerRenderBlock;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataBlockRender extends DataBlockTileEntity {

    public DataBlockRender fill(CoordinatesBlock coord) {
        this.pos = coord;
        World world = Minecraft.getMinecraft().theWorld; // DimensionManager.getWorld(this.pos.dim);

        this.id = (short) Block.getIdFromBlock(world.getBlock(this.pos.x, this.pos.y, this.pos.z));
        this.meta = (short) world.getBlockMetadata(this.pos.x, this.pos.y, this.pos.z);

        HashMap<CoordinatesBlock, DescriptiveStatistics> data = ((ProfilerRenderBlock) (ProfilerSection.RENDER_BLOCK
                .getProfiler())).data;
        this.update = new DataTiming(data.containsKey(coord) ? data.get(coord).getGeometricMean() : 0.0D);

        return this;
    }
}
