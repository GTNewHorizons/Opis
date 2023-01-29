package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.newtypes.CachedString;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class AmountHolder implements ISerializable {

    public CachedString key = null;
    public Integer value = 0;
    public CachedString data = null;

    public AmountHolder(String key, Integer value, String data) {
        this.key = new CachedString(key);
        this.value = value;
        this.data = new CachedString(data);
    }

    public AmountHolder(CachedString key, Integer value, CachedString data) {
        this.key = key;
        this.value = value;
        this.data = data;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.key.writeToStream(stream);
        stream.writeInt(this.value);
        this.data.writeToStream(stream);
    }

    public static AmountHolder readFromStream(ByteArrayDataInput istream) {
        return new AmountHolder(
                CachedString.readFromStream(istream),
                istream.readInt(),
                CachedString.readFromStream(istream));
    }
}
