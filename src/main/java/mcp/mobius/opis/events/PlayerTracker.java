package mcp.mobius.opis.events;

import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.newtypes.PlayerStatus;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.enums.PlayerEv;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.RConHandler;
import mcp.mobius.opis.swing.SelectedTab;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

// public class PlayerTracker implements IPlayerTracker{
public enum PlayerTracker {

    INSTANCE;

    private PlayerTracker() {}

    public HashSet<EntityPlayerMP> playersSwing = new HashSet<EntityPlayerMP>(); // This is the list of players who have
                                                                                 // opened the UI
    // public HashSet<Player> playersOpis = new HashSet<Player>(); //This is the list of players who have opened the
    // UI or used the command line
    public HashMap<String, Boolean> filteredAmount = new HashMap<String, Boolean>(); // Should the entity amount be
                                                                                     // filtered or not
    public HashMap<EntityPlayerMP, Integer> playerDimension = new HashMap<EntityPlayerMP, Integer>();
    public HashMap<EntityPlayerMP, SelectedTab> playerTab = new HashMap<EntityPlayerMP, SelectedTab>();
    private HashSet<String> playerPrivileged = new HashSet<String>();

    public SelectedTab getPlayerSelectedTab(EntityPlayerMP player) {
        return this.playerTab.get(player);
    }

    public AccessLevel getPlayerAccessLevel(EntityPlayerMP player) {
        if (player instanceof FakePlayer && RConHandler.isPriviledge((FakePlayer) player))
            return AccessLevel.PRIVILEGED;

        return this.getPlayerAccessLevel(player.getGameProfile().getName());
    }

    public AccessLevel getPlayerAccessLevel(String name) {

        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(name);
        GameProfile profile = player.getGameProfile();

        if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(profile)
                || MinecraftServer.getServer().isSinglePlayer())
            return AccessLevel.ADMIN;
        else if (playerPrivileged.contains(name)) return AccessLevel.PRIVILEGED;
        else return AccessLevel.NONE;
    }

    public void addPrivilegedPlayer(String name, boolean save) {
        this.playerPrivileged.add(name);
        if (save) {
            modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[] {}, modOpis.commentPrivileged)
                    .set(playerPrivileged.toArray(new String[] {}));
            modOpis.instance.config.save();
        }
    }

    public void addPrivilegedPlayer(String name) {
        this.addPrivilegedPlayer(name, true);
    }

    public void rmPrivilegedPlayer(String name) {
        this.playerPrivileged.remove(name);
        modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[] {}, modOpis.commentPrivileged)
                .set(playerPrivileged.toArray(new String[] {}));
        modOpis.instance.config.save();
    }

    public void reloeadPriviligedPlayers() {
        String[] users = modOpis.instance.config
                .get("ACCESS_RIGHTS", "privileged", new String[] {}, modOpis.commentPrivileged).getStringList();
        for (String s : users) PlayerTracker.INSTANCE.addPrivilegedPlayer(s, false);
    }

    public boolean isAdmin(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isPrivileged(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    public boolean isInPrivilegedList(String name) {
        return this.playerPrivileged.contains(name);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        this.playerDimension.remove(event.player);
        // this.playersOpis.remove(player);
        this.playersSwing.remove(event.player);
        PacketManager.sendPacketToAllSwing(
                new NetDataValue(
                        Message.PLAYER_STATUS_UPDATE,
                        new PlayerStatus(
                                event.player.getGameProfile().getName(),
                                PlayerEv.LOGOUT,
                                event.player.worldObj.provider.dimensionId,
                                (int) event.player.posX,
                                (int) event.player.posY,
                                (int) event.player.posZ)));
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PacketManager.validateAndSend(
                new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())),
                (EntityPlayerMP) event.player);

        /*
         * if (manager instanceof MemoryConnection){
         * System.out.printf("Adding SSP player to list of privileged users\n");
         * PlayerTracker.INSTANCE.addPrivilegedPlayer(((EntityPlayer)event.player).getGameProfile().getName(), false); }
         */

        StringCache.INSTANCE.syncCache((EntityPlayerMP) event.player);
        PacketManager.sendPacketToAllSwing(
                new NetDataValue(
                        Message.PLAYER_STATUS_UPDATE,
                        new PlayerStatus(
                                event.player.getGameProfile().getName(),
                                PlayerEv.LOGIN,
                                event.player.worldObj.provider.dimensionId,
                                (int) event.player.posX,
                                (int) event.player.posY,
                                (int) event.player.posZ)));
    }
}
