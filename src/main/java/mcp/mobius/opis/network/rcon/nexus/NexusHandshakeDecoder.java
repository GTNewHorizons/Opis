package mcp.mobius.opis.network.rcon.nexus;

import io.nettyopis.buffer.ByteBuf;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class NexusHandshakeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) {
            return;
        }
        out.add(in.readBoolean());
    }
}
