package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;

import net.minecraft.network.Packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class DataPacket implements ISerializable {

    public int id;
    public DataByteSize size;
    public DataByteRate rate;
    public DataAmountRate amount;
    public CachedString type;

    public DataPacket() {}

    public DataPacket(Packet packet) {
        this.type = new CachedString(packet.getClass().getSimpleName());
        this.size = new DataByteSize(0);
        this.rate = new DataByteRate(0, 5);
        this.amount = new DataAmountRate(0, 5);
    }

    public DataPacket fill(Packet packet, int pktsize) {
        /*
         * PacketBuffer buff = new PacketBuffer(Unpooled.buffer()); int pktsize = 0; try{ packet.writePacketData(buff);
         * pktsize = buff.readableBytes(); } catch (Exception e){ }
         */
        this.size.size += pktsize;
        this.rate.size += pktsize;
        this.amount.size += 1;
        return this;
    }

    public void startInterval() {
        this.rate.reset();
        this.amount.reset();
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.size.writeToStream(stream);
        this.rate.writeToStream(stream);
        this.amount.writeToStream(stream);
        this.type.writeToStream(stream);
    }

    public static DataPacket readFromStream(ByteArrayDataInput stream) {
        DataPacket retVal = new DataPacket();
        retVal.size = DataByteSize.readFromStream(stream);
        retVal.rate = DataByteRate.readFromStream(stream);
        retVal.amount = DataAmountRate.readFromStream(stream);
        retVal.type = CachedString.readFromStream(stream);

        return retVal;
    }
}
