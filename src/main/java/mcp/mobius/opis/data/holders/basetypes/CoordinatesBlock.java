package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class CoordinatesBlock implements ISerializable {

    public final int dim, x, y, z;
    public final int chunkX, chunkZ;
    // public boolean isChunk;

    public static final CoordinatesBlock INVALID = new CoordinatesBlock(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);

    public CoordinatesBlock(Entity entity) {
        this.dim = entity.worldObj.provider.dimensionId;
        this.x = MathHelper.floor_double(entity.posX);
        this.y = MathHelper.floor_double(entity.posY);
        this.z = MathHelper.floor_double(entity.posZ);
        this.chunkX = x >> 4;
        this.chunkZ = z >> 4;
        // this.isChunk = false;
    }

    public CoordinatesBlock(int dim, int x, int y, int z) {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = x >> 4;
        this.chunkZ = z >> 4;
        // this.isChunk = false;
    }

    public CoordinatesBlock(int dim, double x, double y, double z) {
        this.dim = dim;
        this.x = MathHelper.floor_double(x);
        this.y = MathHelper.floor_double(y);
        this.z = MathHelper.floor_double(z);
        this.chunkX = MathHelper.floor_double(x) >> 4;
        this.chunkZ = MathHelper.floor_double(z) >> 4;
        // this.isChunk = false;
    }

    public CoordinatesBlock(CoordinatesChunk coord) {
        this.dim = coord.dim;
        this.chunkX = coord.chunkX;
        this.chunkZ = coord.chunkZ;

        this.x = coord.x;
        this.y = coord.y;
        this.z = coord.z;
    }

    /*
     * public CoordinatesBlock(int dim, int chunkX, int chunkZ){ this.dim = dim; this.chunkX = chunkX; this.chunkZ =
     * chunkZ; this.x = chunkX << 4; this.y = 0; this.z = chunkZ << 4; this.isChunk = true; }
     */

    public CoordinatesBlock(TileEntity te) {
        this.dim = te.getWorldObj().provider.dimensionId;
        this.x = te.xCoord;
        this.y = te.yCoord;
        this.z = te.zCoord;
        this.chunkX = x >> 4;
        this.chunkZ = z >> 4;
        // this.isChunk = false;
    }

    public CoordinatesChunk asCoordinatesChunk() {
        return new CoordinatesChunk(this);
    }

    public String toString() {
        // if (this.isChunk)
        // return String.format("[%6d %6d %6d]", this.dim, this.chunkX, this.chunkZ);
        // else
        return String.format("[%6d %6d %6d %6d]", this.dim, this.x, this.y, this.z);
    }

    public boolean equals(Object o) {
        CoordinatesBlock c = (CoordinatesBlock) o;
        // if (this.isChunk)
        // return (this.dim == c.dim) && (this.chunkX == c.chunkX) && (this.chunkZ == c.chunkZ);
        // else
        return (this.dim == c.dim) && (this.x == c.x) && (this.y == c.y) && (this.z == c.z);
    };

    public boolean isInvalid() {
        return this.equals(CoordinatesBlock.INVALID);
    }

    public int hashCode() {
        // if (this.isChunk)
        // return String.format("%s %s %s", this.dim, this.chunkX, this.chunkZ).hashCode();
        // else
        // return String.format("%s %s %s %s", this.dim, this.x, this.y, this.z).hashCode();
        return this.dim + 31 * this.x + 877 * this.y + 3187 * this.z;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.dim);
        stream.writeInt(this.x);
        stream.writeInt(this.y);
        stream.writeInt(this.z);
    }

    public static CoordinatesBlock readFromStream(ByteArrayDataInput stream) {
        return new CoordinatesBlock(stream.readInt(), stream.readInt(), stream.readInt(), stream.readInt());
    }
}
