package mcp.mobius.opis.commands.server;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.modOpis;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

public class CommandStop extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_stop";
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
        modOpis.profilerRun = false;
        ProfilerSection.desactivateAll(Side.SERVER);
        icommandsender.addChatMessage(new ChatComponentText(String.format("\u00A7oOpis stopped.")));
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
        return "Ends a run before completion.";
    }
}
