package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import java.util.HashMap;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.profilers.ProfilerDimBlockTick;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataBlockTick implements ISerializable {
    public HashMap<Integer, DataTiming> perdim = new HashMap<Integer, DataTiming>();
    public DataTiming total;

    public DataBlockTick fill() {
        this.total = new DataTiming();
        HashMap<Integer, DescriptiveStatistics> data =
                ((ProfilerDimBlockTick) ProfilerSection.DIMENSION_BLOCKTICK.getProfiler()).data;

        for (Integer dim : data.keySet()) {
            this.perdim.put(dim, new DataTiming(data.get(dim).getGeometricMean()));
            this.total.timing += data.get(dim).getGeometricMean();
        }

        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeShort(this.perdim.size());
        for (Integer key : this.perdim.keySet()) {
            stream.writeInt(key);
            this.perdim.get(key).writeToStream(stream);
        }
        this.total.writeToStream(stream);
    }

    public static DataBlockTick readFromStream(ByteArrayDataInput stream) {
        DataBlockTick retVal = new DataBlockTick();
        int nkeys = stream.readShort();
        for (int i = 0; i < nkeys; i++) retVal.perdim.put(stream.readInt(), DataTiming.readFromStream(stream));
        retVal.total = DataTiming.readFromStream(stream);
        return retVal;
    }
}
