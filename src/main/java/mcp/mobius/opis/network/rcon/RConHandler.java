package mcp.mobius.opis.network.rcon;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;

import net.minecraftforge.common.util.FakePlayer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.nettyopis.buffer.ByteBuf;
import io.nettyopis.channel.ChannelFuture;
import io.nettyopis.channel.ChannelFutureListener;
import io.nettyopis.channel.ChannelHandlerContext;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.packets.client.PacketReqChunks;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.nexus.NexusClient;
import mcp.mobius.opis.network.rcon.nexus.NexusInboundHandler;

public class RConHandler {

    public static BiMap<Byte, Class<? extends PacketBase>> packetTypes = HashBiMap.create();
    public static BiMap<FakePlayer, ChannelHandlerContext> fakePlayersRcon = HashBiMap.create();
    public static BiMap<FakePlayer, ChannelHandlerContext> fakePlayersNexus = HashBiMap.create();

    public static String lastError = null;
    public static WeakReference<ChannelHandlerContext> lastContext = null;

    static {
        packetTypes.put((byte) 0, PacketReqChunks.class);
        packetTypes.put((byte) 1, PacketReqData.class);
        packetTypes.put((byte) 2, NetDataCommand.class);
        packetTypes.put((byte) 3, NetDataList.class);
        packetTypes.put((byte) 4, NetDataValue.class);
    }

    public static void sendToAllNexus(PacketBase packet) {
        for (FakePlayer player : fakePlayersNexus.keySet()) sendToPlayerNexus(packet, player);
    }

    public static void sendToAllRCon(PacketBase packet) {
        for (FakePlayer player : fakePlayersRcon.keySet()) sendToPlayerRCon(packet, player);
    }

    public static void sendToPlayerNexus(PacketBase packet, FakePlayer player) {
        ChannelHandlerContext ctx = RConHandler.fakePlayersNexus.get(player);
        NexusInboundHandler hnd = ctx.pipeline().get(NexusInboundHandler.class);

        if (hnd != null && hnd.timers.isDone(packet.msg)) sendToContext(packet, ctx);
    }

    public static void sendToPlayerRCon(PacketBase packet, FakePlayer player) {
        ChannelHandlerContext ctx = RConHandler.fakePlayersRcon.get(player);
        sendToContext(packet, ctx);
    }

    public static void sendToContext(PacketBase packet, final ChannelHandlerContext ctx) {
        // modOpis.log.info(String.format("%s", packet.msg));

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        packet.encode(output);
        byte[] data = output.toByteArray();

        ByteBuf buf = ctx.alloc().buffer(data.length + 4 + 1);
        buf.writeInt(data.length);
        buf.writeByte(RConHandler.packetTypes.inverse().get(packet.getClass()));
        buf.writeBytes(data);
        ChannelFuture f = ctx.writeAndFlush(buf);
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (arg0.cause() != null) {
                    RConHandler.exceptionCaught(ctx, arg0.cause());
                }
            }
        });

        // modOpis.log.info(String.format("%s", packet.msg));
    }

    public static boolean isPriviledge(FakePlayer player) {
        return fakePlayersRcon.containsKey(player) || fakePlayersNexus.containsKey(player);
    }

    public static void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equals(RConHandler.lastError) && ctx.equals(RConHandler.lastContext.get())) return;

        RConHandler.lastError = cause.getMessage();
        RConHandler.lastContext = new WeakReference<ChannelHandlerContext>(ctx);

        if (cause instanceof IOException && cause.getMessage().contains("Connection reset by peer")) {
            modOpis.log.warn("HydraOpis : Connection reset by peer");
        } else if (cause instanceof ConnectException && cause.getMessage().contains("Connection refused")) {
            modOpis.log.warn("HydraOpis : Connection refused by remote server. Server is down ?");
        } else {
            modOpis.log.info(String.format("%s | %s", cause.getClass().getCanonicalName(), cause.getMessage()));
            cause.printStackTrace();
        }

        FakePlayer fakePlayer = null;

        if (RConHandler.fakePlayersNexus.inverse().containsKey(ctx)) {
            fakePlayer = RConHandler.fakePlayersNexus.inverse().get(ctx);
            RConHandler.fakePlayersNexus.remove(fakePlayer);
        }

        if (RConHandler.fakePlayersRcon.inverse().containsKey(ctx)) {
            fakePlayer = RConHandler.fakePlayersRcon.inverse().get(ctx);
            RConHandler.fakePlayersRcon.remove(fakePlayer);
        }

        if (fakePlayer != null) {
            PlayerTracker.INSTANCE.playersSwing.remove(fakePlayer);
            modOpis.log.info(String.format("Lost connection from %s", fakePlayer.getDisplayName()));
        } else {
            modOpis.log.info(String.format("Lost connection"));
        }

        if (NexusClient.instance.ctx.get().equals(ctx)) {
            NexusClient.instance.reconnect = true;
        }

        ctx.close();
    }
}
