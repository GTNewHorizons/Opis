package mcp.mobius.opis.commands.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.managers.MetaManager;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;

public class CommandReset extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_reset";
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
        MetaManager.reset();

        icommandsender.addChatMessage(new ChatComponentText(String.format("\u00A7oInternal data reseted.")));

        // PacketDispatcher.sendPacketToAllPlayers(NetDataCommand.create(Message.CLIENT_CLEAR_SELECTION));
        if (icommandsender instanceof EntityPlayerMP) PacketManager
                .validateAndSend(new NetDataCommand(Message.CLIENT_CLEAR_SELECTION), (EntityPlayerMP) icommandsender);
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
        return "Cleans up all profiling data.";
    }
}
