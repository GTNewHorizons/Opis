package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.SerialString;

public class NexusAuth implements ISerializable {

    SerialString uuid;
    SerialString passphrase;
    boolean reconnect;
    long timestamp;

    public NexusAuth(String uuid, String passphrase, boolean reconnect) {
        this.uuid = new SerialString(uuid);
        this.passphrase = new SerialString(passphrase);
        this.reconnect = reconnect;
        this.timestamp = System.currentTimeMillis();
    }

    public NexusAuth(SerialString uuid, SerialString passphrase, boolean reconnect) {
        this.uuid = uuid;
        this.passphrase = passphrase;
        this.reconnect = reconnect;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.uuid.writeToStream(stream);
        this.passphrase.writeToStream(stream);
        stream.writeBoolean(reconnect);
        stream.writeLong(this.timestamp);
    }

    public static NexusAuth readFromStream(ByteArrayDataInput stream) {
        return new NexusAuth(
                SerialString.readFromStream(stream),
                SerialString.readFromStream(stream),
                stream.readBoolean());
    }
}
