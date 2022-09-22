package mcp.mobius.opis.commands.server;

import java.util.ArrayList;
import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.managers.ChunkManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandChunkList extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_chunk";
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

        ArrayList<StatsChunk> chunks = new ArrayList<StatsChunk>();

        if (astring.length == 0) chunks = ChunkManager.INSTANCE.getTopChunks(20);
        else
            try {
                chunks = ChunkManager.INSTANCE.getTopChunks(Integer.valueOf(astring.length));
            } catch (Exception e) {
                return;
            }

        icommandsender.addChatMessage(new ChatComponentText("[DIM X Z] Time NTEs"));
        for (StatsChunk stat : chunks) {
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
        return "Shows the 20 slowest chunks, in respect to tile entities.";
    }
}
