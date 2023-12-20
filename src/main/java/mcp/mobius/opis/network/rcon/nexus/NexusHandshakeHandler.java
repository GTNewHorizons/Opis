package mcp.mobius.opis.network.rcon.nexus;

import io.nettyopis.channel.Channel;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.channel.ChannelInboundHandlerAdapter;
import io.nettyopis.handler.codec.compression.JdkZlibDecoder;
import io.nettyopis.handler.codec.compression.JdkZlibEncoder;
import io.nettyopis.handler.ssl.SslHandler;
import io.nettyopis.util.ReferenceCountUtil;
import io.nettyopis.util.concurrent.Future;
import io.nettyopis.util.concurrent.GenericFutureListener;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.rcon.RConHandler;

public class NexusHandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture()
                .addListener(new GenericFutureListener<Future<Channel>>() {

                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        modOpis.log.info("SSL encryption activated.");
                        ctx.pipeline().remove(NexusHandshakeDecoder.class);
                        ctx.pipeline().remove(NexusHandshakeHandler.class);
                        ctx.pipeline().addLast(new JdkZlibDecoder());
                        ctx.pipeline().addLast(new JdkZlibEncoder());
                        ctx.pipeline().addLast(new NexusMsgDecoder());
                        ctx.pipeline().addLast(new NexusInboundHandler());
                    }
                });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            modOpis.log.warn("You shouldn't see this. This handler is never supposed to be active !");
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RConHandler.exceptionCaught(ctx, cause);
    }
}
