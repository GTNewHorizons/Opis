package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class DataEntityPerClass implements Comparable, ISerializable {

    public int nents;
    public CachedString name;
    public DataTiming update;

    public DataEntityPerClass() {}

    public DataEntityPerClass(String name) {
        this.name = new CachedString(name);
        this.nents = 0;
        this.update = new DataTiming();
    }

    public DataEntityPerClass add(Double timing) {
        this.nents += 1;
        this.update.timing += timing;

        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.nents);
        this.name.writeToStream(stream);
        this.update.writeToStream(stream);
    }

    public static DataEntityPerClass readFromStream(ByteArrayDataInput stream) {
        DataEntityPerClass retVal = new DataEntityPerClass();
        retVal.nents = stream.readInt();
        retVal.name = CachedString.readFromStream(stream);
        retVal.update = DataTiming.readFromStream(stream);
        return retVal;
    }

    @Override
    public int compareTo(Object o) {
        return this.update.compareTo(((DataEntityPerClass) o).update);
    }
}
