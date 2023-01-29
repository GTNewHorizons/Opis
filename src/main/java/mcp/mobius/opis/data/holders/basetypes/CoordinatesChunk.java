package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class CoordinatesChunk implements ISerializable {

    public final int dim, x, y, z;
    public final int chunkX, chunkZ;
    public final byte metadata;
    // public boolean isChunk;

    public static final CoordinatesChunk INVALID = new CoordinatesChunk(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);

    public CoordinatesChunk(CoordinatesBlock coord) {
        this.dim = coord.dim;
        this.chunkX = coord.chunkX;
        this.chunkZ = coord.chunkZ;

        this.x = chunkX << 4;
        this.y = 0;
        this.z = chunkZ << 4;

        this.metadata = 0;
    }

    public CoordinatesChunk(int dim, int chunkX, int chunkZ) {
        this.dim = dim;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.x = chunkX << 4;
        this.y = 0;
        this.z = chunkZ << 4;

        this.metadata = 0;
    }

    public CoordinatesChunk(int dim, int chunkX, int chunkZ, byte metadata) {
        this.dim = dim;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.x = chunkX << 4;
        this.y = 0;
        this.z = chunkZ << 4;

        this.metadata = metadata;
    }

    public CoordinatesChunk(int dim, ChunkCoordIntPair coord) {
        this.dim = dim;
        this.chunkX = coord.chunkXPos;
        this.chunkZ = coord.chunkZPos;

        this.x = chunkX << 4;
        this.y = 0;
        this.z = chunkZ << 4;

        this.metadata = 0;
    }

    public CoordinatesChunk(int dim, ChunkCoordIntPair coord, byte metadata) {
        this.dim = dim;
        this.chunkX = coord.chunkXPos;
        this.chunkZ = coord.chunkZPos;

        this.x = chunkX << 4;
        this.y = 0;
        this.z = chunkZ << 4;

        this.metadata = metadata;
    }

    public CoordinatesChunk(TileEntity te) {
        this.dim = te.getWorldObj().provider.dimensionId;
        this.chunkX = te.xCoord >> 4;
        this.chunkZ = te.zCoord >> 4;

        this.x = this.chunkX << 4;
        this.y = 0;
        this.z = this.chunkZ << 4;

        this.metadata = 0;
    }

    public boolean isInvalid() {
        return this.equals(CoordinatesChunk.INVALID);
    }

    public String toString() {
        return String.format("[%6d %6d %6d]", this.dim, this.chunkX, this.chunkZ);
    }

    public boolean equals(Object o) {
        CoordinatesChunk c = (CoordinatesChunk) o;
        return (this.dim == c.dim) && (this.chunkX == c.chunkX) && (this.chunkZ == c.chunkZ);
    }

    // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
    // 11111111 11111111 11111111 11122222 22222222 22222222 22222200 00000000

    public int hashCode() {
        // return String.format("%s %s %s", this.dim, this.chunkX, this.chunkZ).hashCode();
        return this.dim + 31 * this.chunkX + 877 * this.chunkZ;
    }

    public ChunkCoordIntPair toChunkCoordIntPair() {
        return new ChunkCoordIntPair(this.chunkX, this.chunkZ);
    }

    public CoordinatesBlock asCoordinatesBlock() {
        return new CoordinatesBlock(this);
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.dim);
        stream.writeInt(this.chunkX);
        stream.writeInt(this.chunkZ);
        stream.writeByte(this.metadata);
    }

    public static CoordinatesChunk readFromStream(ByteArrayDataInput stream) {
        return new CoordinatesChunk(stream.readInt(), stream.readInt(), stream.readInt(), stream.readByte());
    }
}
