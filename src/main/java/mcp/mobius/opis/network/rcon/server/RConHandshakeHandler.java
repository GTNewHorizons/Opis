package mcp.mobius.opis.network.rcon.server;

import io.nettyopis.buffer.ByteBuf;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.channel.ChannelInboundHandlerAdapter;
import io.nettyopis.handler.codec.compression.JdkZlibDecoder;
import io.nettyopis.handler.codec.compression.JdkZlibEncoder;
import io.nettyopis.util.ReferenceCountUtil;
import mcp.mobius.opis.modOpis;

public class RConHandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Here we should check the password against what the client is sending and do all the user registration part
        // if the password is valid. We should close the socket immediately otherwise.

        try {
            modOpis.log.info(
                    String.format("Connection to rcon from %s detected", ctx.channel().remoteAddress().toString()));

            if (((String) msg).equals(modOpis.rconpass)) {
                ByteBuf buf = ctx.alloc().buffer();
                buf.writeBoolean(true);
                ctx.writeAndFlush(buf);

                ctx.pipeline().remove(RConHandshakeDecoder.class);
                ctx.pipeline().remove(RConHandshakeHandler.class);
                ctx.pipeline().addLast(new JdkZlibDecoder());
                ctx.pipeline().addLast(new JdkZlibEncoder());
                ctx.pipeline().addLast(new RConMsgDecoder());
                ctx.pipeline().addLast(new RConInboundHandler());

            } else {
                modOpis.log.info(String.format("Password error. Closing socket"));
                ByteBuf buf = ctx.alloc().buffer();
                buf.writeBoolean(false);
                ctx.writeAndFlush(buf);
                ctx.close();
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
