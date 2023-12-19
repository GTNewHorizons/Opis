package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class DataThread implements ISerializable {

    public CachedString name;
    public CachedString clzz;

    public DataThread fill(Thread thread) {
        this.name = new CachedString(thread.getName());
        this.clzz = new CachedString(thread.getClass().getSimpleName());
        return this;
    }

    public void writeToStream(ByteArrayDataOutput stream) {
        this.name.writeToStream(stream);
        this.clzz.writeToStream(stream);
    }

    public static DataThread readFromStream(ByteArrayDataInput stream) {
        DataThread retVal = new DataThread();
        retVal.name = CachedString.readFromStream(stream);
        retVal.clzz = CachedString.readFromStream(stream);
        return retVal;
    }
}
