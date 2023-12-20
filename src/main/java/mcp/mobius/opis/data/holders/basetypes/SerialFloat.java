package mcp.mobius.opis.data.holders.basetypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class SerialFloat implements ISerializable {

    public float value = 0;

    public SerialFloat(float value) {
        this.value = value;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeFloat(this.value);
    }

    public static SerialFloat readFromStream(ByteArrayDataInput stream) {
        return new SerialFloat(stream.readFloat());
    }
}
