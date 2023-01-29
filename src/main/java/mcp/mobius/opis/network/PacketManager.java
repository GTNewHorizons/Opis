package mcp.mobius.opis.network;

import static cpw.mods.fml.relauncher.Side.CLIENT;
import static cpw.mods.fml.relauncher.Side.SERVER;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumMap;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.newtypes.*;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.data.profilers.ProfilerEvent;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqChunks;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.RConHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.FakePlayer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table.Cell;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
// public class PacketManager extends FMLIndexedMessageToMessageCodec<PacketBase>
public class PacketManager {

    // private static final PacketManager INSTANCE = new PacketManager();
    // private static final Logger LOGGER = LogManager.getLogger();
    private static final EnumMap<Side, FMLEmbeddedChannel> channels = Maps.newEnumMap(Side.class);

    public static void init() {
        if (!channels.isEmpty()) // avoid duplicate inits..
            return;

        Codec codec = new Codec();

        codec.addDiscriminator(0, PacketReqChunks.class);
        codec.addDiscriminator(1, PacketReqData.class);
        codec.addDiscriminator(2, NetDataCommand.class);
        codec.addDiscriminator(3, NetDataList.class);
        codec.addDiscriminator(4, NetDataValue.class);

        channels.putAll(NetworkRegistry.INSTANCE.newChannel("Opis", codec));

        // add handlers
        if (FMLCommonHandler.instance().getSide().isClient()) {
            // for the client
            FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
            String codecName = channel.findChannelHandlerNameForType(Codec.class);
            channel.pipeline().addAfter(codecName, "ClientHandler", new HandlerClient());
        }
        // else
        {
            // for the server
            FMLEmbeddedChannel channel = channels.get(Side.SERVER);
            String codecName = channel.findChannelHandlerNameForType(Codec.class);
            channel.pipeline().addAfter(codecName, "ServerHandler", new HandlerServer());
        }
    }

    /**
     * @author abrarsyed This class converts FMLMessage to PacketBase for my system to handle.
     */
    private static final class Codec extends FMLIndexedMessageToMessageCodec<PacketBase> {

        @Override
        public void encodeInto(ChannelHandlerContext ctx, PacketBase packet, ByteBuf target) throws Exception {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            packet.encode(output);
            target.writeBytes(output.toByteArray());
        }

        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, PacketBase packet) {
            ByteArrayDataInput input = ByteStreams.newDataInput(source.array());
            input.skipBytes(1); // skip the packet identifier byte
            packet.decode(input);

            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                actionClient(packet);
            } else {
                actionServer(ctx, packet);
            }
        }

        @SideOnly(Side.CLIENT)
        private void actionClient(PacketBase packet) {
            Minecraft mc = Minecraft.getMinecraft();
            packet.actionClient(mc.theWorld, mc.thePlayer);
        }

        private void actionServer(ChannelHandlerContext ctx, PacketBase packet) {
            EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER)
                    .get()).playerEntity;
            packet.actionServer(player.worldObj, player);
        }

        @Override
        public FMLIndexedMessageToMessageCodec<PacketBase> addDiscriminator(int discriminator,
                Class<? extends PacketBase> type) {
            // double check it has an empty constructor. or fail early.
            // This is to guard myself against my own stupidity.

            if (!hasEmptyContructor(type)) {
                LogManager.getLogger().log(Level.FATAL, type.getName() + "does not have an empty constructor!");
            }

            return super.addDiscriminator(discriminator, type);
        }

        @SuppressWarnings("rawtypes")
        private static boolean hasEmptyContructor(Class type) {
            try {
                for (Constructor c : type.getConstructors()) {
                    if (c.getParameterTypes().length == 0) {
                        return true;
                    }
                }
            } catch (SecurityException e) {
                // really?
            }

            return false;
        }
    }

    @Sharable
    @SideOnly(Side.CLIENT)
    private static final class HandlerClient extends SimpleChannelInboundHandler<PacketBase> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet) throws Exception {
            Minecraft mc = Minecraft.getMinecraft();
            packet.actionClient(mc.theWorld, mc.thePlayer);
        }
    }

    @Sharable
    private static final class HandlerServer extends SimpleChannelInboundHandler<PacketBase> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet) throws Exception {
            EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER)
                    .get()).playerEntity;
            packet.actionServer(player.worldObj, player);
        }
    }

    // UTIL SENDING METHODS

    public static void sendToServer(PacketBase packet) {
        channels.get(CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(CLIENT).writeAndFlush(packet);
    }

    public static void sendToPlayer(PacketBase packet, EntityPlayer player) {
        if ((player instanceof FakePlayer) && (RConHandler.fakePlayersRcon.containsKey(player))) {
            RConHandler.sendToPlayerRCon(packet, (FakePlayer) player);
        } else if ((player instanceof FakePlayer) && (RConHandler.fakePlayersNexus.containsKey(player))) {
            RConHandler.sendToPlayerNexus(packet, (FakePlayer) player);
        } else {
            channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
                    .set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            channels.get(SERVER).writeAndFlush(packet);
        }
    }

    public static void sendToPlayer(Packet packet, EntityPlayerMP player) {
        player.playerNetServerHandler.sendPacket(packet);
    }

    public static void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point) {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static void sendToDimension(PacketBase packet, int dimension) {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static void sendToAll(PacketBase packet) {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(SERVER).writeAndFlush(packet);

        for (Object fakePlayer : RConHandler.fakePlayersRcon.keySet().toArray()) {
            RConHandler.sendToPlayerRCon(packet, (FakePlayer) fakePlayer);
        }

        for (Object fakePlayer : RConHandler.fakePlayersNexus.keySet().toArray()) {
            RConHandler.sendToPlayerNexus(packet, (FakePlayer) fakePlayer);
        }
    }

    public static Packet toMcPacket(PacketBase packet) {
        return channels.get(FMLCommonHandler.instance().getEffectiveSide()).generatePacketFrom(packet);
    }

    /*
     * public void writeString(ByteArrayDataOutput buffer, String data) throws IOException { byte[] abyte =
     * data.getBytes(Charsets.UTF_8); buffer.writeShort(abyte.length); buffer.writeBytes(abyte); } public String
     * readString(ByteArrayDataInput buffer) throws IOException { int j = buffer.readShort(); String s = new
     * String(buffer.readBytes(j).array(), Charsets.UTF_8); return s; }
     */

    public static void validateAndSend(PacketBase capsule, EntityPlayerMP player) {
        if (!capsule.msg.isDisplayActive(PlayerTracker.INSTANCE.getPlayerSelectedTab(player))) return;

        if (capsule.msg.canPlayerUseCommand(player)) PacketManager.sendToPlayer(capsule, player);
    }

    public static void sendPacketToAllSwing(PacketBase capsule) {
        for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing)
            PacketManager.validateAndSend(capsule, player);
    }

    public static void sendChatMsg(String msg, EntityPlayerMP player) {
        PacketManager.sendToPlayer(new S02PacketChat(new ChatComponentText(msg)), player);
    }

    public static void splitAndSend(Message msg, ArrayList<? extends ISerializable> data, EntityPlayerMP player) {
        int i = 0;
        while (i < data.size()) {
            validateAndSend(new NetDataList(msg, data.subList(i, Math.min(i + 500, data.size()))), player);
            i += 500;
        }
    }

    public static void sendFullUpdate(EntityPlayerMP player) {
        ArrayList<DataEntity> timingEntities = EntityManager.INSTANCE.getWorses(100);
        ArrayList<DataBlockTileEntity> timingTileEnts = TileEntityManager.INSTANCE.getWorses(100);
        // ArrayList<DataHandler> timingHandlers = TickHandlerManager.getCumulatedStatsServer();
        ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);
        ArrayList<DataEntityPerClass> timingEntsClass = EntityManager.INSTANCE.getTotalPerClass();
        ArrayList<DataBlockTileEntityPerClass> timingTEsClass = TileEntityManager.INSTANCE
                .getCumulativeTimingTileEntities();

        DataTiming totalTimeTE = TileEntityManager.INSTANCE.getTotalUpdateTime();
        DataTiming totalTimeEnt = EntityManager.INSTANCE.getTotalUpdateTime();
        // DataTiming totalTimeHandler = TickHandlerManager.getTotalUpdateTime();
        DataNetworkTick totalNetwork = new DataNetworkTick().fill();
        DataBlockTick totalWorldTick = new DataBlockTick().fill();

        ArrayList<DataEvent> timingEvents = new ArrayList<DataEvent>();
        HashBasedTable<Class, String, DescriptiveStatistics> eventData = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).data;
        HashBasedTable<Class, String, String> eventMod = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).dataMod;
        for (Cell<Class, String, DescriptiveStatistics> cell : eventData.cellSet()) {
            timingEvents.add(new DataEvent().fill(cell, eventMod.get(cell.getRowKey(), cell.getColumnKey())));
        }

        ArrayList<DataEvent> timingTicks = new ArrayList<DataEvent>();
        HashBasedTable<Class, String, DescriptiveStatistics> eventTickData = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).dataTick;
        HashBasedTable<Class, String, String> eventTickMod = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).dataModTick;
        for (Cell<Class, String, DescriptiveStatistics> cell : eventTickData.cellSet()) {
            timingTicks.add(new DataEvent().fill(cell, eventTickMod.get(cell.getRowKey(), cell.getColumnKey())));
        }

        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_HANDLERS, timingTicks), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES, timingEntities), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS, timingTileEnts), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS_PER_CLASS, timingTEsClass), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_EVENTS, timingEvents), player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES_PER_CLASS, timingEntsClass), player);
        PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_TILEENTS, totalTimeTE), player);
        PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_ENTITIES, totalTimeEnt), player);
        // PacketManager.validateAndSend(NetDataValue_OLD.create(Message.VALUE_TIMING_HANDLERS, totalTimeHandler),
        // player);
        PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_WORLDTICK, totalWorldTick), player);
        PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_NETWORK, totalNetwork), player);

        // PacketManager.validateAndSend(NetDataValue_OLD.create(Message.VALUE_AMOUNT_HANDLERS, new
        // SerialInt(timingHandlers.size())), player);

        PacketManager.validateAndSend(
                new NetDataValue(Message.STATUS_TIME_LAST_RUN, new SerialLong(ProfilerSection.timeStampLastRun)),
                player);

        PacketManager.validateAndSend(
                new NetDataValue(
                        Message.STATUS_ACCESS_LEVEL,
                        new SerialInt(PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal())),
                player);

        // This portion is to get the proper filtered amounts depending on the player preferences.
        String name = player.getGameProfile().getName();
        boolean filtered = false;
        if (PlayerTracker.INSTANCE.filteredAmount.containsKey(name))
            filtered = PlayerTracker.INSTANCE.filteredAmount.get(name);
        ArrayList<AmountHolder> amountEntities = EntityManager.INSTANCE.getCumulativeEntities(filtered);

        // Here we send a full update to the player
        // OpisPacketHandler.validateAndSend(Packet_DataList.create(DataReq.LIST_AMOUNT_ENTITIES, amountEntities),
        // player);
        PacketManager.validateAndSend(new NetDataList(Message.LIST_AMOUNT_ENTITIES, amountEntities), player);
    }
}
