package mcp.mobius.opis.commands.server;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.managers.MetaManager;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandStart extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_start";
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

        MetaManager.reset();
        modOpis.profilerRun = true;
        ProfilerSection.activateAll(Side.SERVER);

        PacketManager.sendPacketToAllSwing(
                new NetDataValue(Message.STATUS_START, new SerialInt(modOpis.profilerMaxTicks)));
        icommandsender.addChatMessage(new ChatComponentText(
                String.format("\u00A7oOpis started with a tick delay %s.", modOpis.profilerDelay)));
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
        return "Starts a run.";
    }
}
