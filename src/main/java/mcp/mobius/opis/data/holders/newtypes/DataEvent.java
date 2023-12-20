package mcp.mobius.opis.data.holders.newtypes;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Table.Cell;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class DataEvent implements ISerializable, Comparable {

    public CachedString event;
    public CachedString handler;
    public CachedString package_;
    public CachedString mod;
    public long nCalls;
    public DataTiming update;

    public DataEvent fill(Cell<Class, String, DescriptiveStatistics> cellData, String modName) {
        /*
         * String handlerName = cell.getColumnKey().getSimpleName(); try { String[] splitHandler =
         * handlerName.split("_"); handlerName = splitHandler[2] + "." + splitHandler[3]; } catch (Exception e){}
         */
        String[] nameRaw = cellData.getColumnKey().split("\\|");

        String handlerName = nameRaw[1];
        try {
            String[] splitHandler = handlerName.split("_");
            handlerName = splitHandler[2] + "." + splitHandler[3];
        } catch (Exception e) {}

        this.package_ = new CachedString(nameRaw[0]);
        this.handler = new CachedString(handlerName);
        this.event = new CachedString(cellData.getRowKey().getName().replace("net.minecraftforge.event.", ""));
        this.nCalls = cellData.getValue().getN();
        this.mod = new CachedString(modName);

        this.update = new DataTiming(cellData.getValue().getGeometricMean());
        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.event.writeToStream(stream);
        this.package_.writeToStream(stream);
        this.handler.writeToStream(stream);
        this.update.writeToStream(stream);
        this.mod.writeToStream(stream);
        stream.writeLong(this.nCalls);
    }

    public static DataEvent readFromStream(ByteArrayDataInput stream) {
        DataEvent retVal = new DataEvent();
        retVal.event = CachedString.readFromStream(stream);
        retVal.package_ = CachedString.readFromStream(stream);
        retVal.handler = CachedString.readFromStream(stream);
        retVal.update = DataTiming.readFromStream(stream);
        retVal.mod = CachedString.readFromStream(stream);
        retVal.nCalls = stream.readLong();
        return retVal;
    }

    @Override
    public int compareTo(Object o) {
        return this.update.compareTo(((DataEvent) o).update);
    }
}
