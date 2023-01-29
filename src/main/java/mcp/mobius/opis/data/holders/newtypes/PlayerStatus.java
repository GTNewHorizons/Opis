package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.network.enums.PlayerEv;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class PlayerStatus implements ISerializable {

    public CachedString name;
    public PlayerEv status;
    public int x, y, z, dim;
    public long timestamp;

    public PlayerStatus(String name_, PlayerEv status, int dim, int x, int y, int z) {
        this.name = new CachedString(name_);
        this.status = status;
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = System.currentTimeMillis();
    }

    public PlayerStatus(CachedString name, PlayerEv status, int dim, int x, int y, int z, long timestamp) {
        this.name = name;
        this.status = status;
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.name.writeToStream(stream);
        stream.writeInt(this.status.ordinal());
        stream.writeInt(this.dim);
        stream.writeInt(this.x);
        stream.writeInt(this.y);
        stream.writeInt(this.z);
        stream.writeLong(this.timestamp);
    }

    public static PlayerStatus readFromStream(ByteArrayDataInput stream) {
        return new PlayerStatus(
                CachedString.readFromStream(stream),
                PlayerEv.values()[stream.readInt()],
                stream.readInt(),
                stream.readInt(),
                stream.readInt(),
                stream.readInt(),
                stream.readLong());
    }
}
