package mcp.mobius.opis.data.holders.newtypes;

import java.util.WeakHashMap;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.ProfilerRenderTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataTileEntityRender extends DataBlockTileEntity {

    public DataTileEntityRender fill(TileEntity ent) {
        this.pos = new CoordinatesBlock(ent.getWorldObj().provider.dimensionId, ent.xCoord, ent.yCoord, ent.zCoord);
        World world = Minecraft.getMinecraft().theWorld; // DimensionManager.getWorld(this.pos.dim);

        this.id = (short) Block.getIdFromBlock(world.getBlock(this.pos.x, this.pos.y, this.pos.z));
        this.meta = (short) world.getBlockMetadata(this.pos.x, this.pos.y, this.pos.z);

        WeakHashMap<TileEntity, DescriptiveStatistics> data =
                ((ProfilerRenderTileEntity) (ProfilerSection.RENDER_TILEENTITY.getProfiler())).data;
        this.update = new DataTiming(data.containsKey(ent) ? data.get(ent).getGeometricMean() : 0.0D);

        return this;
    }
}
