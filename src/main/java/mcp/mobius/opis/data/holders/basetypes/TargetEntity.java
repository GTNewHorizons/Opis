package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class TargetEntity implements ISerializable {

    public int entityID = 0;
    public int dim = 0;

    public TargetEntity(int id, int dim) {
        this.entityID = id;
        this.dim = dim;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.entityID);
        stream.writeInt(this.dim);
    }

    public static TargetEntity readFromStream(ByteArrayDataInput stream) {
        return new TargetEntity(stream.readInt(), stream.readInt());
    }
}
