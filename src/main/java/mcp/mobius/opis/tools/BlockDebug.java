package mcp.mobius.opis.tools;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDebug extends BlockContainer {

    public BlockDebug(Material par2Material) {
        super(par2Material);
        // this.setUnlocalizedName("Debug Companion Cube");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileDebug();
    }
}
