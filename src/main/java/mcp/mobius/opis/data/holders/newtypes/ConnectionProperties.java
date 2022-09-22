package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import java.util.Properties;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.SerialString;

public class ConnectionProperties implements ISerializable {

    public Properties prop = new Properties();

    public ConnectionProperties(Properties prop) {
        this.prop = prop;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.prop.size());
        for (String key : this.prop.stringPropertyNames()) {
            new SerialString(key).writeToStream(stream);
            new SerialString(this.prop.getProperty(key)).writeToStream(stream);
        }
    }

    public static ConnectionProperties readFromStream(ByteArrayDataInput stream) {
        Properties prop = new Properties();
        int nkeys = stream.readInt();
        for (int i = 0; i < nkeys; i++) {
            String key = SerialString.readFromStream(stream).value;
            String val = SerialString.readFromStream(stream).value;
            prop.setProperty(key, val);
        }
        return new ConnectionProperties(prop);
    }
}
