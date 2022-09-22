package mcp.mobius.opis.data.holders.stats;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public class StatsChunk extends StatAbstract {
    public int tileEntities = 0;
    public int entities = 0;

    public StatsChunk() {}

    public StatsChunk(CoordinatesChunk chunk) {
        this.chunk = chunk;
        this.coord = chunk.asCoordinatesBlock();
    }

    public StatsChunk(CoordinatesChunk chunk, int tileEntities, int entities) {
        this.chunk = chunk;
        this.coord = chunk.asCoordinatesBlock();
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public StatsChunk(CoordinatesChunk chunk, int tileEntities, int entities, double time) {
        this.chunk = chunk;
        this.coord = chunk.asCoordinatesBlock();
        this.tileEntities = tileEntities;
        this.entities = entities;
        this.setDataSum(time);
    }

    public void addTileEntity() {
        this.tileEntities += 1;
    }

    public void addEntity() {
        this.entities += 1;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.chunk.writeToStream(stream);
        stream.writeInt(this.tileEntities);
        stream.writeInt(this.entities);
        stream.writeDouble(this.getDataSum());
    }

    public static StatsChunk readFromStream(ByteArrayDataInput stream) {
        CoordinatesChunk chunk = CoordinatesChunk.readFromStream(stream);
        StatsChunk chunkStats = new StatsChunk(chunk, stream.readInt(), stream.readInt());
        chunkStats.setDataSum(stream.readDouble());
        return chunkStats;
    }

    @Override
    public int compareTo(Object o) {
        double value = ((StatAbstract) o).getDataSum() - this.getDataSum();
        if (value > 0) return 1;
        if (value < 0) return -1;
        return 0;
    }

    public String toString() {
        return String.format("%.3f µs", this.getDataSum() / 1000.0);
    }
}
