package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class CommandKill extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_kill";
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
        if (astring.length != 2) return;
        int dim = Integer.valueOf(astring[0]);
        int eid = Integer.valueOf(astring[1]);

        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            icommandsender
                    .addChatMessage(new ChatComponentText(String.format("\u00A7oCannot find dim %d in world", dim)));
            return;
        }

        Entity entity = world.getEntityByID(eid);
        if (entity == null) {
            icommandsender.addChatMessage(
                    new ChatComponentText(String.format("\u00A7oCannot find entity %d in dim %d", eid, dim)));
            return;
        }

        entity.setDead();
        icommandsender
                .addChatMessage(new ChatComponentText(String.format("\u00A7oKilled entity %d in dim %d", eid, dim)));
        return;
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
        return "Kills the given entity id in the given dimension.";
    }
}
