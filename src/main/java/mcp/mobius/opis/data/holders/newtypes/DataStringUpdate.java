package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import mcp.mobius.opis.data.holders.ISerializable;

public class DataStringUpdate implements ISerializable {

    public String str;
    public int index;

    public DataStringUpdate() {}

    public DataStringUpdate(String str, int index) {
        this.str = str;
        this.index = index;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeUTF(this.str);
        stream.writeInt(this.index);
    }

    public static DataStringUpdate readFromStream(ByteArrayDataInput stream) {
        return new DataStringUpdate(stream.readUTF(), stream.readInt());
    }
}
