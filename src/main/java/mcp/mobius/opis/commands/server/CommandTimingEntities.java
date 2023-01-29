package mcp.mobius.opis.commands.server;

import java.util.ArrayList;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.managers.EntityManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandTimingEntities extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_ent";
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

        ArrayList<DataEntity> ents = new ArrayList<DataEntity>();
        if (astring.length == 0) {
            ents = EntityManager.INSTANCE.getWorses(20);
        } else {
            try {
                ents = EntityManager.INSTANCE.getWorses(Integer.valueOf(astring[0]));
            } catch (Exception e) {
                return;
            }
        }

        icommandsender.addChatMessage(new ChatComponentText("[DIM X Z] Time NTEs"));
        for (DataEntity stat : ents) {
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
        return "Returns the 20 longest entities to update.";
    }
}
