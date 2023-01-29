package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class DataPacket250 implements ISerializable {

    public CachedString channel;
    public DataByteSize size;
    public DataByteRate rate;
    public DataAmountRate amount;

    public DataPacket250() {}

    public DataPacket250(String channel) {
        this.channel = new CachedString(channel);
        this.size = new DataByteSize(0);
        this.rate = new DataByteRate(0, 5);
        this.amount = new DataAmountRate(0, 5);
    }

    public DataPacket250 fill(FMLProxyPacket packet, int pktsize) {
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
        this.channel.writeToStream(stream);
        this.size.writeToStream(stream);
        this.rate.writeToStream(stream);
        this.amount.writeToStream(stream);
    }

    public static DataPacket250 readFromStream(ByteArrayDataInput stream) {
        DataPacket250 retVal = new DataPacket250();
        retVal.channel = CachedString.readFromStream(stream);
        retVal.size = DataByteSize.readFromStream(stream);
        retVal.rate = DataByteRate.readFromStream(stream);
        retVal.amount = DataAmountRate.readFromStream(stream);

        return retVal;
    }
}
