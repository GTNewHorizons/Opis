package mcp.mobius.opis.commands.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;

public class CommandRmPrivileged extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_rmpriv";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (PlayerTracker.INSTANCE.isInPrivilegedList(astring[0])) {
            PlayerTracker.INSTANCE.rmPrivilegedPlayer(astring[0]);
            icommandsender.addChatMessage(
                    new ChatComponentText(String.format("Player %s removed from Opis user list.", astring[0])));
        } else {
            icommandsender
                    .addChatMessage(new ChatComponentText(String.format("Player %s not found in list.", astring[0])));
        }
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

    @Override
    public String getDescription() {
        return "Remove a user from the privileged list of users.";
    }

    @Override
    public String getCommandNameOpis() {
        return "opis_rmpriv";
    }
}
