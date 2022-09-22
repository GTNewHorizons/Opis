package mcp.mobius.opis.network.rcon.nexus;

import com.mojang.authlib.GameProfile;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.channel.ChannelInboundHandlerAdapter;
import io.nettyopis.util.ReferenceCountUtil;
import java.lang.ref.WeakReference;
import java.util.UUID;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.newtypes.ConnectionProperties;
import mcp.mobius.opis.data.holders.newtypes.NexusAuth;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.RConHandler;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class NexusInboundHandler extends ChannelInboundHandlerAdapter {

    public EventTimerRing timers = new EventTimerRing();

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        NexusClient.instance.ctx = new WeakReference<ChannelHandlerContext>(ctx);
        RConHandler.sendToContext(
                new NetDataValue(
                        Message.NEXUS_UUID,
                        new NexusAuth(
                                NexusClient.instance.uuid, NexusClient.instance.pass, NexusClient.instance.reconnect)),
                ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FakePlayer fakePlayer = RConHandler.fakePlayersNexus.inverse().get(ctx);
            PacketBase packet = (PacketBase) msg;
            if (packet instanceof PacketReqData && ((PacketReqData) packet).dataReq == Message.CONNECTION_STATUS) {
                PacketReqData pck = (PacketReqData) packet;
                int status = ((SerialInt) pck.param1).value;
                if (status == 0) {
                    modOpis.log.error(String.format("Connection refused. Wrong uuid or pass."));
                    NexusClient.instance.shouldRetry = false;
                } else if (status == 1) {
                    this.registerFakePlayer(ctx);
                    this.timers = new EventTimerRing(((ConnectionProperties) pck.param2).prop);
                }

            } else {
                packet.actionServer(null, fakePlayer);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RConHandler.exceptionCaught(ctx, cause);
    }

    private void registerFakePlayer(ChannelHandlerContext ctx) {
        UUID fakeUUID = UUID.randomUUID();

        FakePlayer fakePlayer =
                FakePlayerFactory.get(DimensionManager.getWorld(0), new GameProfile(fakeUUID, ctx.name()));
        RConHandler.fakePlayersNexus.put(fakePlayer, ctx);

        PlayerTracker.INSTANCE.playersSwing.add(fakePlayer);
        PlayerTracker.INSTANCE.playerTab.put(fakePlayer, SelectedTab.ALL);
        RConHandler.sendToContext(
                new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())), ctx);
        StringCache.INSTANCE.syncCache(fakePlayer);

        modOpis.log.info(String.format("FakePlayer %s with uuid %s registered.", ctx.name(), fakeUUID));
    }

    /*
      private void unregisterFakePlayer(ChannelHandlerContext ctx){
      	FakePlayer fakePlayer = RConHandler.fakePlayersNexus.inverse().get(ctx);
      	RConHandler.fakePlayersNexus.remove(fakePlayer);
    PlayerTracker.INSTANCE.playersSwing.remove(fakePlayer);
    PlayerTracker.INSTANCE.playerTab.remove(fakePlayer);
    modOpis.log.info(String.format("FakePlayer %s unregistered.", fakePlayer));
      }
      */

}
