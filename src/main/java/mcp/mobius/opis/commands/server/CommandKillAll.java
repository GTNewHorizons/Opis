package mcp.mobius.opis.commands.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

import org.apache.commons.lang3.StringUtils;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.events.PlayerTracker;

public class CommandKillAll extends CommandBase implements IOpisCommand {

    @Override
    public String getCommandName() {
        return "opis_killall";
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
        if (astring.length < 1) return;

        String searchname = StringUtils.join(astring, " ").toLowerCase();

        int nkilled = EntityManager.INSTANCE.killAll(searchname);

        if (nkilled == -1) {
            icommandsender.addChatMessage(
                    new ChatComponentText(
                            String.format(
                                    "\u00A7oSeriously ? I can't, seriously, I can't. I should remove you from the OP list !")));
            return;
        }

        icommandsender.addChatMessage(
                new ChatComponentText(String.format("\u00A7oKilled %d entities of type %s", nkilled, searchname)));

        /*
         * World world = DimensionManager.getWorld(dim); if (world == null){ PacketDispatcher.sendPacketToPlayer( new
         * Packet3Chat(ChatMessageComponent.createFromText(String.format("\u00A7oCannot find dim %d in world %d",
         * dim))), (Player)icommandsender); return; } Entity entity = world.getEntityByID(eid); if (entity == null) {
         * PacketDispatcher.sendPacketToPlayer( new
         * Packet3Chat(ChatMessageComponent.createFromText(String.format("\u00A7oCannot find entity %d in dim %d", eid,
         * dim))), (Player)icommandsender); return; } entity.setDead(); PacketDispatcher.sendPacketToPlayer( new
         * Packet3Chat(ChatMessageComponent.createFromText(String.format("\u00A7oKilled entity %d in dim %d", eid,
         * dim))), (Player)icommandsender); return;
         */
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
        return "Kills all entities of the given name.";
    }
}
