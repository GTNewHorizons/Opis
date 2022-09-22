package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import mcp.mobius.opis.data.holders.ISerializable;

public class DataByteRate implements Comparable, ISerializable {
    public Long size; // Size is stored in byte
    public int interval;

    // public DataByteRate(int interval){ this.size = 0L; this.interval = interval;}
    public DataByteRate(long size, int interval) {
        this.size = size;
        this.interval = interval;
    }

    @Override
    public int compareTo(Object o) {
        return ((DataByteRate) o).size.compareTo(this.size); // Reverse order ! Put higher values FIRST
    }

    public void reset() {
        this.size = 0L;
    }

    public String toString() {

        if (size >= 1024 && size < (1024 * 1024)) {
            return String.format("%.3f KiB/s", this.size / 1024.0 / this.interval);
        }

        if (size >= (1024 * 1024)) {
            return String.format("%.3f MiB/s", this.size / 1024.0 / 1024.0 / this.interval);
        }

        return String.format("%4d   B/s", this.size / this.interval);
    }

    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeLong(this.size);
        stream.writeInt(this.interval);
    }

    public static DataByteRate readFromStream(ByteArrayDataInput stream) {
        return new DataByteRate(stream.readLong(), stream.readInt());
    }
}
