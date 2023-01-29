package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class SerialInt implements ISerializable {

    public int value = 0;

    public SerialInt(int value) {
        this.value = value;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.value);
    }

    public static SerialInt readFromStream(ByteArrayDataInput stream) {
        return new SerialInt(stream.readInt());
    }
}
