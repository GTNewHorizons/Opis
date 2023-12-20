package mcp.mobius.opis.data.holders.basetypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class SerialLong implements ISerializable {

    public long value = 0;

    public SerialLong(long value) {
        this.value = value;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeLong(this.value);
    }

    public static SerialLong readFromStream(ByteArrayDataInput stream) {
        return new SerialLong(stream.readLong());
    }
}
