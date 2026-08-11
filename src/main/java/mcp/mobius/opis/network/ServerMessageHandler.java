package mcp.mobius.opis.network;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.basetypes.SerialString;
import mcp.mobius.opis.data.holders.basetypes.TargetEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTick;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.MetaManager;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.events.OpisServerTickHandler;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.swing.SelectedTab;

public class ServerMessageHandler {

    private static ServerMessageHandler _instance;

    private ServerMessageHandler() {}

    public static ServerMessageHandler instance() {
        if (_instance == null) _instance = new ServerMessageHandler();
        return _instance;
    }

    /** Requests that walk chunk or profiler collections and queue work onto the server tick. */
    private static final EnumSet<Message> THROTTLED = EnumSet.of(
            Message.LIST_CHUNK_LOADED,
            Message.LIST_TIMING_CHUNK,
            Message.LIST_TIMING_CHUNK_DIM,
            Message.LIST_TIMING_TILEENTS,
            Message.LIST_TIMING_ENTITIES,
            Message.LIST_AMOUNT_ENTITIES,
            Message.LIST_AMOUNT_TILEENTS);

    /** Below the map overlay's 250ms floor, leaving headroom for jitter. */
    private static final long MIN_REQUEST_INTERVAL_MS = 200;

    /** Weak so entries drop with the player; guarded as each connection has its own thread. */
    private final Map<EntityPlayerMP, EnumMap<Message, Long>> lastRequest = new WeakHashMap<>();

    /** Latest queued job per player and message: a stalled tick cannot build a backlog of stale scans. */
    private final Map<EntityPlayerMP, EnumMap<Message, Runnable>> queued = new WeakHashMap<>();

    private void schedule(Message msg, EntityPlayerMP player, Runnable job) {
        synchronized (this) {
            EnumMap<Message, Runnable> jobs = queued.computeIfAbsent(player, ignored -> new EnumMap<>(Message.class));
            if (jobs.put(msg, job) != null) return;
        }

        OpisServerTickHandler.INSTANCE.scheduleOnServerThread(() -> {
            Runnable latest;
            synchronized (this) {
                EnumMap<Message, Runnable> jobs = queued.get(player);
                latest = jobs.remove(msg);
                if (jobs.isEmpty()) queued.remove(player);
            }
            latest.run();
        });
    }

    public synchronized void clearSessionState() {
        lastRequest.clear();
        queued.clear();
    }

    private synchronized boolean isThrottled(Message msg, EntityPlayerMP player) {
        if (!THROTTLED.contains(msg)) return false;

        long now = System.currentTimeMillis();
        EnumMap<Message, Long> perMessage = lastRequest
                .computeIfAbsent(player, ignored -> new EnumMap<>(Message.class));
        Long last = perMessage.get(msg);

        if (last != null && now - last < MIN_REQUEST_INTERVAL_MS) return true;

        perMessage.put(msg, now);
        return false;
    }

    public void handle(Message maintype, ISerializable param1, ISerializable param2, EntityPlayerMP player) {
        String name = player.getGameProfile().getName();

        // Dropped rather than punished: access can be revoked while a client is still polling.
        if (isThrottled(maintype, player)) return;

        if (maintype == Message.LIST_CHUNK_LOADED) {
            int dim = ((SerialInt) param1).value;
            schedule(maintype, player, () -> {
                PlayerTracker.INSTANCE.playerDimension.put(player, dim);
                PacketManager
                        .splitAndSend(Message.LIST_CHUNK_LOADED, ChunkManager.INSTANCE.getLoadedChunks(dim), player);
                // Sent last: the client treats it as "batch complete" and only then shows it.
                PacketManager.validateAndSend(new NetDataCommand(Message.LIST_CHUNK_LOADED_CLEAR), player);
            });
        } else if (maintype == Message.LIST_TIMING_TILEENTS) {
            ArrayList<DataBlockTileEntity> timingTileEnts = TileEntityManager.INSTANCE.getWorses(100);
            DataTiming totalTime = TileEntityManager.INSTANCE.getTotalUpdateTime();
            PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS, timingTileEnts), player);
            PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_TILEENTS, totalTime), player);
        } else if (maintype == Message.LIST_TIMING_ENTITIES) {
            ArrayList<DataEntity> timingEntities = EntityManager.INSTANCE.getWorses(100);
            DataTiming totalTime = EntityManager.INSTANCE.getTotalUpdateTime();
            PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES, timingEntities), player);
            PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_ENTITIES, totalTime), player);
        } else if (maintype == Message.LIST_TIMING_HANDLERS) {
            // ArrayList<DataHandler> timingHandlers = TickHandlerManager.getCumulatedStatsServer();
            // DataTiming totalTime = TickHandlerManager.getTotalUpdateTime();
            // OpisPacketHandler_OLD.validateAndSend(NetDataList_OLD.create(Message.LIST_TIMING_HANDLERS,
            // timingHandlers), player);
            // OpisPacketHandler_OLD.validateAndSend(NetDataValue_OLD.create(Message.VALUE_TIMING_HANDLERS, totalTime),
            // player);
        } else if (maintype == Message.LIST_TIMING_CHUNK) {
            schedule(maintype, player, () -> {
                ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);
                PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
            });
        } else if (maintype == Message.LIST_TIMING_CHUNK_DIM) {
            // Ranked within one dimension, and answered on its own message so the Swing table keeps the global list.
            int dim = ((SerialInt) param1).value;
            schedule(maintype, player, () -> {
                ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100, dim);
                PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK_DIM, timingChunks), player);
            });
        } else if (maintype == Message.VALUE_TIMING_WORLDTICK) {
            PacketManager.validateAndSend(
                    new NetDataValue(Message.VALUE_TIMING_WORLDTICK, new DataBlockTick().fill()),
                    player);
        } else if (maintype == Message.VALUE_TIMING_ENTUPDATE) {} else if (maintype == Message.LIST_AMOUNT_ENTITIES) {
            boolean filtered = false;
            if (PlayerTracker.INSTANCE.filteredAmount.containsKey(name))
                filtered = PlayerTracker.INSTANCE.filteredAmount.get(name);

            ArrayList<AmountHolder> ents = EntityManager.INSTANCE.getCumulativeEntities(filtered);
            PacketManager.validateAndSend(new NetDataList(Message.LIST_AMOUNT_ENTITIES, ents), player);
        } else if (maintype == Message.LIST_AMOUNT_TILEENTS) {
            PacketManager.validateAndSend(
                    new NetDataList(
                            Message.LIST_AMOUNT_TILEENTS,
                            TileEntityManager.INSTANCE.getCumulativeAmountTileEntities()),
                    player);
        } else if (maintype == Message.COMMAND_FILTERING_TRUE) {
            PlayerTracker.INSTANCE.filteredAmount.put(name, true);
        } else if (maintype == Message.COMMAND_FILTERING_FALSE) {
            PlayerTracker.INSTANCE.filteredAmount.put(name, false);
        } else if (maintype == Message.COMMAND_OPEN_SWING) {
            SelectedTab tab = SelectedTab.values()[((SerialInt) param1).value];
            schedule(maintype, player, () -> {
                PlayerTracker.INSTANCE.playerTab.put(player, tab);
                if (PlayerTracker.INSTANCE.playersSwing.add(player)) PacketManager.sendFullUpdate(player);
            });
        } else if (maintype == Message.COMMAND_UNREGISTER) {
            schedule(maintype, player, () -> PlayerTracker.INSTANCE.playerDimension.remove(player));
        } else if (maintype == Message.COMMAND_START) {
            MetaManager.reset();
            modOpis.profilerRun = true;
            ProfilerSection.activateAll(Side.SERVER);
            PacketManager.sendPacketToAllSwing(
                    new NetDataValue(Message.STATUS_START, new SerialInt(modOpis.profilerMaxTicks)));
        } else if (maintype == Message.COMMAND_TELEPORT_BLOCK) {
            EntityManager.INSTANCE.teleportPlayer((CoordinatesBlock) param1, player);
            PacketManager.validateAndSend(new NetDataValue(Message.CLIENT_HIGHLIGHT_BLOCK, param1), player);
        } else if (maintype == Message.COMMAND_TELEPORT_TO_ENTITY) {
            EntityManager.INSTANCE.teleportEntity(
                    player,
                    EntityManager.INSTANCE.getEntity(((TargetEntity) param1).entityID, ((TargetEntity) param1).dim),
                    player);
        } else if (maintype == Message.COMMAND_TELEPORT_PULL_ENTITY) {
            EntityManager.INSTANCE.teleportEntity(
                    EntityManager.INSTANCE.getEntity(((TargetEntity) param1).entityID, ((TargetEntity) param1).dim),
                    player,
                    player);
        } else if (maintype == Message.COMMAND_TELEPORT_CHUNK) {
            CoordinatesChunk chunkCoord = (CoordinatesChunk) param1;
            World world = DimensionManager.getWorld(chunkCoord.dim);
            if (world == null) return;

            CoordinatesBlock blockCoord = new CoordinatesBlock(
                    chunkCoord.dim,
                    chunkCoord.x + 8,
                    world.getTopSolidOrLiquidBlock(chunkCoord.x, chunkCoord.z),
                    chunkCoord.z + 8);

            EntityManager.INSTANCE.teleportPlayer(blockCoord, player);
        } else if (maintype == Message.COMMAND_KILLALL) {
            EntityManager.INSTANCE.killAll(((SerialString) param1).value);
            // this.handle(Message.LIST_AMOUNT_ENTITIES, null, null, player);
        } else if (maintype == Message.COMMAND_KILLALL_BYCACHEID) {
            String entname = StringCache.INSTANCE.getString(((SerialInt) param1).value);
            EntityManager.INSTANCE.killAll(entname);
        } else if (maintype == Message.COMMAND_UNREGISTER_SWING) {
            schedule(maintype, player, () -> PlayerTracker.INSTANCE.playersSwing.remove(player));
        } else if (maintype == Message.STATUS_TIME_LAST_RUN) {
            PacketManager.validateAndSend(
                    new NetDataValue(Message.STATUS_TIME_LAST_RUN, new SerialLong(ProfilerSection.timeStampLastRun)),
                    player);
        } else if (maintype == Message.COMMAND_KILL_HOSTILES_ALL) {
            for (int dim : DimensionManager.getIDs()) EntityManager.INSTANCE.killAllPerClass(dim, EntityMob.class);
        } else if (maintype == Message.COMMAND_KILL_HOSTILES_DIM) {
            EntityManager.INSTANCE.killAllPerClass(((SerialInt) param1).value, EntityMob.class);
        } else if (maintype == Message.COMMAND_KILL_STACKS_ALL) {
            for (int dim : DimensionManager.getIDs()) EntityManager.INSTANCE.killAllPerClass(dim, EntityItem.class);
        } else if (maintype == Message.COMMAND_KILL_STACKS_DIM) {
            EntityManager.INSTANCE.killAllPerClass(((SerialInt) param1).value, EntityItem.class);
        } else if (maintype == Message.COMMAND_PURGE_CHUNKS_ALL) {
            for (int dim : DimensionManager.getIDs()) ChunkManager.INSTANCE.purgeChunks(dim);
        } else if (maintype == Message.COMMAND_PURGE_CHUNKS_DIM) {
            ChunkManager.INSTANCE.purgeChunks(((SerialInt) param1).value);
        } else if (maintype == Message.STATUS_PING) {
            PacketManager.validateAndSend(new NetDataValue(Message.STATUS_PING, param1), player);
        } else if (maintype == Message.SWING_TAB_CHANGED) {
            SelectedTab tab = SelectedTab.values()[((SerialInt) param1).value];
            schedule(maintype, player, () -> PlayerTracker.INSTANCE.playerTab.put(player, tab));
        } else if (maintype == Message.LIST_ORPHAN_TILEENTS) {
            PacketManager.validateAndSend(new NetDataCommand(Message.LIST_ORPHAN_TILEENTS_CLEAR), player);
            PacketManager.splitAndSend(Message.LIST_ORPHAN_TILEENTS, TileEntityManager.INSTANCE.getOrphans(), player);
        } else {
            modOpis.log.log(Level.WARN, String.format("Unknown data request : %s ", maintype));
        }
    }
}
