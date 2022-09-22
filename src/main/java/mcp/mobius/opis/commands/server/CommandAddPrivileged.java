package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

public class CommandAddPrivileged extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_addpriv";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        PlayerTracker.INSTANCE.addPrivilegedPlayer(astring[0]);
        icommandsender.addChatMessage(
                new ChatComponentText(String.format("Player %s added to Opis user list", astring[0])));
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
        return "Add a user to the privileged list of users.";
    }

    @Override
    public String getCommandNameOpis() {
        return "opis_addpriv";
    }
}
