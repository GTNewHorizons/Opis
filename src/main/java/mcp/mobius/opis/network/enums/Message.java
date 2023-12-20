package mcp.mobius.opis.network.enums;

import static mcp.mobius.opis.network.enums.AccessLevel.PRIVILEGED;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayerMP;

import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.swing.SelectedTab;

public enum Message {

    LIST_CHUNK_LOADED,
    LIST_CHUNK_LOADED_CLEAR,
    LIST_TIMING_TILEENTS,
    LIST_TIMING_TILEENTS_PER_CLASS,
    LIST_TIMING_ENTITIES,
    LIST_TIMING_ENTITIES_PER_CLASS,
    LIST_TIMING_HANDLERS,
    LIST_TIMING_CHUNK,
    LIST_TIMING_EVENTS,
    LIST_AMOUNT_ENTITIES,
    LIST_AMOUNT_TILEENTS,
    LIST_PLAYERS(EnumSet.of(SelectedTab.PLAYERS)),
    LIST_DIMENSION_DATA(EnumSet.of(SelectedTab.DIMENSIONS)),
    LIST_PACKETS_OUTBOUND(EnumSet.of(SelectedTab.PACKETOUTBOUND)),
    LIST_PACKETS_INBOUND(EnumSet.of(SelectedTab.PACKETINBOUND)),
    LIST_PACKETS_OUTBOUND_250(EnumSet.of(SelectedTab.PACKETOUTBOUND250)),
    LIST_PACKETS_INBOUND_250(EnumSet.of(SelectedTab.PACKETINBOUND250)),
    LIST_ORPHAN_TILEENTS,
    LIST_ORPHAN_TILEENTS_CLEAR,
    LIST_THREADS(EnumSet.of(SelectedTab.THREADS)),

    VALUE_TIMING_TILEENTS,
    VALUE_TIMING_ENTITIES,
    VALUE_TIMING_HANDLERS,
    VALUE_TIMING_WORLDTICK,
    VALUE_TIMING_ENTUPDATE,
    VALUE_TIMING_NETWORK,

    STATUS_START,
    STATUS_STOP,
    STATUS_RUN_UPDATE,
    STATUS_RUNNING,
    STATUS_CURRENT_TIME,
    STATUS_TIME_LAST_RUN,
    STATUS_ACCESS_LEVEL,
    STATUS_PING(EnumSet.of(SelectedTab.SUMMARY)),
    STATUS_STRINGUPD,
    STATUS_STRINGUPD_FULL,

    COMMAND_TELEPORT_BLOCK(PRIVILEGED),
    COMMAND_TELEPORT_CHUNK(PRIVILEGED),
    COMMAND_TELEPORT_TO_ENTITY(PRIVILEGED),
    COMMAND_TELEPORT_PULL_ENTITY(PRIVILEGED),
    COMMAND_START(PRIVILEGED),
    COMMAND_KILLALL(PRIVILEGED),
    COMMAND_KILLALL_BYCACHEID(PRIVILEGED),
    COMMAND_FILTERING_TRUE,
    COMMAND_FILTERING_FALSE,
    COMMAND_UNREGISTER,
    COMMAND_UNREGISTER_SWING,
    COMMAND_OPEN_SWING,

    COMMAND_KILL_HOSTILES_DIM(PRIVILEGED), // This will kill all hostile in the dim given as argument
    COMMAND_KILL_HOSTILES_ALL(PRIVILEGED), // This will kill all hostile in all the dimensions
    COMMAND_PURGE_CHUNKS_DIM(PRIVILEGED), // This will purge the chunks in the dim given as argument
    COMMAND_PURGE_CHUNKS_ALL(PRIVILEGED), // This will purge the chunks in all dimensions
    COMMAND_KILL_STACKS_DIM(PRIVILEGED), // This will kill all stacks in the dim given as argument
    COMMAND_KILL_STACKS_ALL(PRIVILEGED), // This will kill all stacks in all the dimensions

    CLIENT_START_PROFILING,
    CLIENT_STOP_PROFILING,
    CLIENT_RESET_PROFILING,
    CLIENT_SHOW_RENDER_TICK,
    CLIENT_SHOW_SWING,
    CLIENT_CLEAR_SELECTION,
    CLIENT_HIGHLIGHT_BLOCK,

    SWING_TAB_CHANGED,
    NEXUS_UUID, // Login data for the nexus system
    NEXUS_DATA, // Data blob with everything needed to update the summary screen in realtime
    PLAYER_STATUS_UPDATE, // Update for the players (name, position, offline/online, etc)
    CONNECTION_STATUS,

    CHAT_MSG;

    private AccessLevel accessLevel = AccessLevel.NONE;
    private EnumSet<SelectedTab> tabEnum;

    private Message() {
        accessLevel = AccessLevel.NONE;
        tabEnum = EnumSet.of(SelectedTab.ANY);
    }

    private Message(AccessLevel level) {
        accessLevel = level;
        tabEnum = EnumSet.of(SelectedTab.ANY);
    }

    private Message(EnumSet<SelectedTab> _tabEnum) {
        accessLevel = AccessLevel.NONE;
        tabEnum = _tabEnum;
    }

    private Message(AccessLevel level, EnumSet<SelectedTab> _tabEnum) {
        accessLevel = level;
        tabEnum = _tabEnum;
    }

    public AccessLevel getAccessLevel() {
        return this.accessLevel;
    }

    public void setAccessLevel(AccessLevel level) {
        this.accessLevel = level;
    }

    public boolean canPlayerUseCommand(EntityPlayerMP player) {
        return PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal() >= this.accessLevel.ordinal();
    }

    public boolean isDisplayActive(SelectedTab tab) {
        if (tab == SelectedTab.ALL) return true;
        if (this.tabEnum.contains(SelectedTab.ANY)) return true;
        if (this.tabEnum.contains(SelectedTab.NONE)) return false;
        if (this.tabEnum.contains(tab)) return true;
        return false;
    }

    public static void setTablesMinimumLevel(AccessLevel level) {
        Message.LIST_CHUNK_LOADED.setAccessLevel(level);
        Message.LIST_TIMING_TILEENTS.setAccessLevel(level);
        Message.LIST_TIMING_ENTITIES.setAccessLevel(level);
        Message.LIST_TIMING_HANDLERS.setAccessLevel(level);
        Message.LIST_TIMING_CHUNK.setAccessLevel(level);
        Message.LIST_TIMING_TILEENTS_PER_CLASS.setAccessLevel(level);
        Message.LIST_TIMING_ENTITIES_PER_CLASS.setAccessLevel(level);
        Message.LIST_TIMING_HANDLERS.setAccessLevel(level);
        Message.LIST_TIMING_EVENTS.setAccessLevel(level);
        Message.LIST_AMOUNT_ENTITIES.setAccessLevel(level);
        Message.LIST_AMOUNT_TILEENTS.setAccessLevel(level);
        Message.LIST_PLAYERS.setAccessLevel(level);
        Message.LIST_DIMENSION_DATA.setAccessLevel(level);
        Message.LIST_PACKETS_OUTBOUND.setAccessLevel(level);
        Message.LIST_PACKETS_INBOUND.setAccessLevel(level);
        Message.LIST_PACKETS_OUTBOUND_250.setAccessLevel(level);
        Message.LIST_PACKETS_INBOUND_250.setAccessLevel(level);
        Message.LIST_ORPHAN_TILEENTS.setAccessLevel(level);
        Message.LIST_THREADS.setAccessLevel(level);
    }

    public static void setOpisMinimumLevel(AccessLevel level) {
        Message.COMMAND_OPEN_SWING.setAccessLevel(level);
    }
}
