package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandHelp extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_help";
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
        IOpisCommand[] commands = { new CommandStart(), new CommandStop(), new CommandReset(), new CommandFrequency(),
                new CommandTicks(), new CommandChunkList(), new CommandTimingTileEntities(),
                new CommandTimingEntities(), new CommandAmountEntities(), new CommandKill(), new CommandKillAll(),
                new CommandAddPrivileged(), new CommandRmPrivileged() };

        for (IOpisCommand cmd : commands) icommandsender.addChatMessage(
                new ChatComponentText(String.format("/%s : %s", cmd.getCommandNameOpis(), cmd.getDescription())));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getDescription() {
        return "This message.";
    }
}
