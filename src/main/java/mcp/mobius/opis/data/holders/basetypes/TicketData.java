package mcp.mobius.opis.data.holders.basetypes;

import mcp.mobius.opis.data.holders.ISerializable;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class TicketData implements ISerializable {

    public final CoordinatesChunk coord;
    public final int nchunks;
    public final String modID;
    public final Ticket ticket;

    public TicketData(Ticket ticket) {
        ImmutableSet requestedChunks = ticket.getChunkList();
        int maxChunkX = -999999, minChunkX = 9999999, maxChunkZ = -999999, minChunkZ = 9999999;

        for (Object obj : requestedChunks) {
            ChunkCoordIntPair chunk = (ChunkCoordIntPair) obj;
            maxChunkX = Math.max(maxChunkX, chunk.chunkXPos);
            minChunkX = Math.min(minChunkX, chunk.chunkXPos);
            maxChunkZ = Math.max(maxChunkZ, chunk.chunkZPos);
            minChunkZ = Math.min(minChunkZ, chunk.chunkZPos);
        }

        this.coord = new CoordinatesChunk(
                ticket.world.provider.dimensionId,
                (minChunkX + maxChunkX) / 2,
                (minChunkZ + maxChunkZ) / 2);
        this.nchunks = requestedChunks.size();
        this.modID = ticket.getModId();
        this.ticket = ticket;
    }

    public TicketData(CoordinatesChunk coord, int nchunks, String modid) {
        this.coord = coord;
        this.nchunks = nchunks;
        this.modID = modid;
        this.ticket = null;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        this.coord.writeToStream(stream);
        stream.writeInt(this.nchunks);
        stream.writeUTF(this.modID);
    }

    public static TicketData readFromStream(ByteArrayDataInput stream) {
        return new TicketData(CoordinatesChunk.readFromStream(stream), stream.readInt(), stream.readUTF());
    }

    public boolean equals(Object o) {
        TicketData c = (TicketData) o;

        if (this.ticket != null && c.ticket != null) return this.ticket.equals(c.ticket);

        return (this.coord.dim == c.coord.dim) && (this.coord.chunkX == c.coord.chunkX)
                && (this.coord.chunkZ == c.coord.chunkZ)
                && (this.nchunks == c.nchunks);
    };

    public int hashCode() {
        if (this.ticket != null) return this.ticket.hashCode();
        return String.format("%s %s %s %s", this.coord.dim, this.coord.chunkX, this.coord.chunkZ, this.nchunks)
                .hashCode();
    }

    public String toString() {
        return String.format(
                "Ticket %s [%d %d %d] %d",
                this.modID,
                this.coord.dim,
                this.coord.chunkX,
                this.coord.chunkZ,
                this.nchunks);
    }
}
