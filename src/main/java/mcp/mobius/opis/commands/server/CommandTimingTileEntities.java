package mcp.mobius.opis.commands.server;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.managers.TileEntityManager;

public class CommandTimingTileEntities extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_te";
    }

    @Override
    public String getCommandNameOpis() {
        return this.getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (icommandsender instanceof EntityPlayerMP) {
            icommandsender.addChatMessage(new ChatComponentText("DEPRECATED ! Please run /opis instead."));
            return;
        }

        ArrayList<DataBlockTileEntity> tes = new ArrayList<DataBlockTileEntity>();
        if (astring.length == 0) {
            tes = TileEntityManager.INSTANCE.getWorses(20);
        } else {
            try {
                tes = TileEntityManager.INSTANCE.getWorses(Integer.valueOf(astring[0]));
            } catch (Exception e) {
                return;
            }
        }

        for (DataBlockTileEntity stat : tes) {
            icommandsender.addChatMessage(new ChatComponentText(stat.toString()));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getDescription() {
        return "Returns the 20 longest TEs to update.";
    }
}
