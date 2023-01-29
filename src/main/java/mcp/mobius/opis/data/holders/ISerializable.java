package mcp.mobius.opis.data.holders;

import com.google.common.io.ByteArrayDataOutput;

public interface ISerializable {

    void writeToStream(ByteArrayDataOutput stream);
    // Object readFromStream(DataInputStream stream) throws IOException;
}
