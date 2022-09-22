package mcp.mobius.opis.network.rcon.server;

import io.nettyopis.buffer.ByteBuf;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class RConHandshakeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        } // Size of the password char array
        int nChars = in.getInt(in.readerIndex());

        if (in.readableBytes() < 4 + 2 * nChars) {
            return;
        } // The total size of the packet is 4 (int) + 2 (char) * nChars
        in.readInt();

        char[] passchars = new char[nChars];
        for (int i = 0; i < nChars; i++) {
            passchars[i] = in.readChar();
        }

        out.add(new String(passchars));
    }
}
