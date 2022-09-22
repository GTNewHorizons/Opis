package mcp.mobius.opis.tools;

import net.minecraft.tileentity.TileEntity;

public class TileLag extends TileEntity {

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
