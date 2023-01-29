package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class SerialDouble implements ISerializable {

    public double value = 0;

    public SerialDouble(double value) {
        this.value = value;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeDouble(this.value);
    }

    public static SerialDouble readFromStream(ByteArrayDataInput stream) {
        return new SerialDouble(stream.readDouble());
    }
}
