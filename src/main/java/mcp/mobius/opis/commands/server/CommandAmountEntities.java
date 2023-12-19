package mcp.mobius.opis.commands.server;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.managers.EntityManager;

public class CommandAmountEntities extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_nent";
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

        ArrayList<AmountHolder> ents;

        if (astring.length == 1 && astring[0].equals("all")) ents = EntityManager.INSTANCE.getCumulativeEntities(false);
        else ents = EntityManager.INSTANCE.getCumulativeEntities(true);

        for (AmountHolder s : ents)
            icommandsender.addChatMessage(new ChatComponentText(String.format("%s : %s", s.key, s.value)));
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
        return "Opens a summary of the number of entities on the server, by type.";
    }
}
