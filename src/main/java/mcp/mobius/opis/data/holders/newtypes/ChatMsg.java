package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.SerialString;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class ChatMsg implements ISerializable {

    public long timestamp;
    public CachedString username;
    public SerialString message;

    public ChatMsg(String username, String message) {
        this.username = new CachedString(username);
        this.message = new SerialString(message);
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMsg(CachedString username, SerialString message, long timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.username.writeToStream(stream);
        this.message.writeToStream(stream);
        stream.writeLong(this.timestamp);
    }

    public static ChatMsg readFromStream(ByteArrayDataInput stream) {
        return new ChatMsg(CachedString.readFromStream(stream), SerialString.readFromStream(stream), stream.readLong());
    }
}
