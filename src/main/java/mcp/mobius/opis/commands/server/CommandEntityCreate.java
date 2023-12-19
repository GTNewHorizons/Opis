package mcp.mobius.opis.commands.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

import mcp.mobius.opis.events.OpisServerEventHandler;
import mcp.mobius.opis.events.PlayerTracker;

public class CommandEntityCreate extends CommandBase {

    @Override
    public String getCommandName() {
        return "opis_enttrace";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {

        if (astring.length == 1 && astring[0].equals("full")) {
            OpisServerEventHandler.printEntityFull = true;
            OpisServerEventHandler.printEntityTrace = true;
        } else {
            OpisServerEventHandler.printEntityTrace = !OpisServerEventHandler.printEntityTrace;
            OpisServerEventHandler.printEntityFull = false;
        }

        icommandsender.addChatMessage(
                new ChatComponentText(String.format("Entity trace is %s", OpisServerEventHandler.printEntityTrace)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) return PlayerTracker.INSTANCE.isPrivileged((EntityPlayerMP) sender);

        if (sender instanceof DedicatedServer) return true;
        if (!(sender instanceof DedicatedServer) && !(sender instanceof EntityPlayerMP)) return true;

        return false;
    }
}
