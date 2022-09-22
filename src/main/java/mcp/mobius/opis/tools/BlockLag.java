package mcp.mobius.opis.tools;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLag extends BlockContainer {

    public BlockLag(Material par2Material) {
        super(par2Material);
        // this.setUnlocalizedName("Lag Generator");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileLag();
    }
}
