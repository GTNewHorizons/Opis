package mcp.mobius.opis.network.rcon.nexus;

import com.google.common.io.ByteStreams;
import io.nettyopis.buffer.ByteBuf;
import io.nettyopis.channel.ChannelHandlerContext;
import io.nettyopis.handler.codec.ByteToMessageDecoder;
import java.util.List;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.rcon.RConHandler;

public class NexusMsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        } // Packet starts with the size (int)
        int packetSize = in.getInt(in.readerIndex()); // We read the packet size (not including the packet type byte)

        if (in.readableBytes() < 4 + 1 + packetSize) {
            return;
        } // We check if there is at least packet type + packetSize available bytes
        in.readInt();
        byte packetType = in.readByte(); // We read the header

        // Here we should get a list of potential packets and decode for the corresponding byte

        PacketBase packet = (PacketBase) RConHandler.packetTypes.get(packetType).newInstance();
        packet.decode(ByteStreams.newDataInput(in.readBytes(packetSize).array()));
        out.add(packet);
    }
}
