package mcp.mobius.opis.network.rcon.server;

import java.util.UUID;

import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.RConHandler;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.mojang.authlib.GameProfile;

import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.channel.ChannelInboundHandlerAdapter;
import io.nettyopis.util.ReferenceCountUtil;

class RConInboundHandler extends ChannelInboundHandlerAdapter {
    // One object of this type is created by connection
    // There is one permanent context per connection !

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        UUID fakeUUID = UUID.randomUUID();
        FakePlayer fakePlayer = FakePlayerFactory
                .get(DimensionManager.getWorld(0), new GameProfile(fakeUUID, ctx.name()));
        RConHandler.fakePlayersRcon.put(fakePlayer, ctx);

        PlayerTracker.INSTANCE.playersSwing.add(fakePlayer);
        PacketManager.validateAndSend(
                new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())),
                fakePlayer);
        StringCache.INSTANCE.syncCache(fakePlayer);

        modOpis.log.info(String.format("FakePlayer %s with uuid %s created.", ctx.name(), fakeUUID));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FakePlayer fakePlayer = RConHandler.fakePlayersRcon.inverse().get(ctx);
            PacketBase packet = (PacketBase) msg;

            // if (packet instanceof PacketReqData)
            // modOpis.log.info(String.format("Received request %s from player %s", ((PacketReqData)packet).dataReq,
            // fakePlayer.getDisplayName()));

            packet.actionServer(null, fakePlayer);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RConHandler.exceptionCaught(ctx, cause);
    }
}
