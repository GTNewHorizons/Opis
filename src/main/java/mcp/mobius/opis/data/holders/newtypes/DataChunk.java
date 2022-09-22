package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import java.util.ArrayList;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.TileEntityManager;

public class DataChunk implements ISerializable, Comparable {
    int nentities;
    int ntileents;
    DataTiming update;
    CoordinatesChunk chunk;

    public DataChunk fill(CoordinatesChunk chunk) {
        this.chunk = chunk;
        ArrayList<DataBlockTileEntity> tileEnts = TileEntityManager.INSTANCE.getTileEntitiesInChunk(this.chunk);
        ArrayList<DataEntity> entities = EntityManager.INSTANCE.getEntitiesInChunk(this.chunk);

        this.nentities = entities.size();
        this.ntileents = tileEnts.size();
        double totalUpdate = 0.0D;
        for (DataBlockTileEntity tileent : tileEnts) totalUpdate += tileent.update.timing;

        for (DataEntity ent : entities) totalUpdate += ent.update.timing;

        this.update = new DataTiming(totalUpdate);
        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.nentities);
        stream.writeInt(this.ntileents);
        this.update.writeToStream(stream);
        this.chunk.writeToStream(stream);
    }

    public static DataChunk readFromStream(ByteArrayDataInput stream) {
        DataChunk retVal = new DataChunk();
        retVal.nentities = stream.readInt();
        retVal.ntileents = stream.readInt();
        retVal.update = DataTiming.readFromStream(stream);
        retVal.chunk = CoordinatesChunk.readFromStream(stream);
        return retVal;
    }

    @Override
    public int compareTo(Object o) {
        return this.update.compareTo(((DataEntity) o).update);
    }
}
