package mcp.mobius.opis.commands.client;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;

public class CommandClientShowRenderTick extends CommandBase {

    @Override
    public String getCommandName() {
        return "opis_ctick";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (icommandsender instanceof EntityPlayerMP) PacketManager
                .validateAndSend(new NetDataCommand(Message.CLIENT_SHOW_RENDER_TICK), (EntityPlayerMP) icommandsender);
        // ((EntityPlayerMP)icommandsender).playerNetServerHandler.sendPacketToPlayer(NetDataCommand.create(Message.CLIENT_SHOW_RENDER_TICK));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
