package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.holders.ISerializable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class DataAmountRate implements Comparable, ISerializable {

    public Long size; // Size is stored in byte
    public int interval;

    // public DataByteRate(int interval){ this.size = 0L; this.interval = interval;}
    public DataAmountRate(long size, int interval) {
        this.size = size;
        this.interval = interval;
    }

    @Override
    public int compareTo(Object o) {
        return ((DataAmountRate) o).size.compareTo(this.size); // Reverse order ! Put higher values FIRST
    }

    public void reset() {
        this.size = 0L;
    }

    public String toString() {
        return String.format("%.2f packet/s", (double) this.size / (double) this.interval);
    }

    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeLong(this.size);
        stream.writeInt(this.interval);
    }

    public static DataAmountRate readFromStream(ByteArrayDataInput stream) {
        return new DataAmountRate(stream.readLong(), stream.readInt());
    }
}
