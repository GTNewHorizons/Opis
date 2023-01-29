package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.profilers.ProfilerNetworkTick;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class DataNetworkTick implements ISerializable, Comparable {

    public DataTiming update;

    public DataNetworkTick fill() {

        DescriptiveStatistics data = ((ProfilerNetworkTick) (ProfilerSection.NETWORK_TICK.getProfiler())).data;
        this.update = new DataTiming(data.getN() != 0 ? data.getGeometricMean() : 0.0D);

        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.update.writeToStream(stream);
    }

    public static DataNetworkTick readFromStream(ByteArrayDataInput stream) {
        DataNetworkTick retVal = new DataNetworkTick();
        retVal.update = DataTiming.readFromStream(stream);
        return retVal;
    }

    @Override
    public int compareTo(Object o) {
        return this.update.compareTo(((DataNetworkTick) o).update);
    }
}
