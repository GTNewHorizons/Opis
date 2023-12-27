package mcp.mobius.opis.commands.server;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.Level;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.modOpis;

public class CommandChunkDump extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "chunkdump";
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
        modOpis.log.log(Level.INFO, "== CHUNK DUMP ==");

        HashMap<ChunkCoordIntPair, Boolean> chunkStatus = new HashMap<ChunkCoordIntPair, Boolean>();

        Integer[] worldIDs = DimensionManager.getIDs();
        for (Integer worldID : worldIDs) {
            Set<ChunkCoordIntPair> persistantChunks = DimensionManager.getWorld(worldID).getPersistentChunks().keySet();
            Set<ChunkCoordIntPair> chunks = (Set<ChunkCoordIntPair>) DimensionManager.getWorld(worldID).activeChunkSet;

            for (ChunkCoordIntPair chunk : chunks) {
                modOpis.log.log(
                        Level.INFO,
                        String.format("Dim : %s, %s, Forced : %s", worldID, chunk, persistantChunks.contains(chunk)));
                chunkStatus.put(chunk, persistantChunks.contains(chunk));
            }
        }

        // ((EntityPlayerMP)icommandsender).playerNetServerHandler.sendPacketToPlayer(Packet_LoadedChunks.create(chunkStatus));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return false;
    }

    @Override
    public String getDescription() {
        return "Unused";
    }
}
