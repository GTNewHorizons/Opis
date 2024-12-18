package mcp.mobius.opis.data.holders.newtypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.SetMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class DataForcedChunks implements ISerializable {

    private static final CachedString NONE_CACHED = new CachedString("N/A");

    public int dim;
    public CachedString dimName;
    public CachedString modId;

    public CachedString playerOrEntityName;

    public CachedString rawData;
    public CachedString chunks;

    // Optimistic data we may be able to collect
    public CachedString position;
    public CachedString type;

    public static List<DataForcedChunks> getAll(int dim) {
        WorldServer world = DimensionManager.getWorld(dim);
        List<DataForcedChunks> dataList = new ArrayList<>();

        CachedString dimName = new CachedString(world.provider.getDimensionName());

        SetMultimap<ChunkCoordIntPair, Ticket> ticketMap = world.getPersistentChunks();
        Set<Ticket> tickets = new HashSet<>(ticketMap.values());
        for (Ticket ticket : tickets) {
            DataForcedChunks data = new DataForcedChunks();
            dataList.add(data);

            // Basic Information
            data.dim = dim;
            data.dimName = dimName;
            data.modId = new CachedString(ticket.getModId());

            // Report the player name, if available. Otherwise, report the associated entity.
            // If neither are available, show "N/A."
            if (ticket.isPlayerTicket()) {
                data.playerOrEntityName = new CachedString(ticket.getPlayerName());
            } else if (ticket.getType() == ForgeChunkManager.Type.ENTITY && ticket.getEntity() != null) {
                Entity entity = ticket.getEntity();
                data.playerOrEntityName = new CachedString(entity.getClass().getSimpleName());
            } else {
                data.playerOrEntityName = NONE_CACHED;
            }

            // Raw ticket NBT data
            NBTTagCompound rawData = ticket.getModData();
            data.rawData = new CachedString(rawData.toString());
            // Try to parse out optimistic information from the NBT data
            data.tryParseLocation(rawData);
            data.tryParseType(rawData);

            // Chunk positions
            StringBuilder sb = new StringBuilder();
            Iterator<ChunkCoordIntPair> itr = ticket.getChunkList().iterator();
            while (itr.hasNext()) {
                ChunkCoordIntPair coord = itr.next();
                sb.append("(");
                sb.append(coord.chunkXPos);
                sb.append(",");
                sb.append(coord.chunkZPos);
                sb.append(")");
                if (itr.hasNext()) {
                    sb.append(", ");
                }
            }
            data.chunks = new CachedString(sb.toString());
        }
        return dataList;
    }

    private void tryParseLocation(NBTTagCompound raw) {
        int xCoord = 0, yCoord = 0, zCoord = 0;

        if (raw.hasKey("xCoord", Constants.NBT.TAG_INT)) { // Railcraft
            xCoord = raw.getInteger("xCoord");
        } else if (raw.hasKey("OwnerX", Constants.NBT.TAG_INT)) { // GregTech
            xCoord = raw.getInteger("OwnerX");
        } else if (raw.hasKey("x", Constants.NBT.TAG_INT)) { // Extra Utilities
            xCoord = raw.getInteger("x");
        } else if (raw.hasKey("poppetX", Constants.NBT.TAG_INT)) { // Witchery
            xCoord = raw.getInteger("poppetX");
        } else if (raw.hasKey("ChunkLoaderTileX", Constants.NBT.TAG_INT)) { // Galacticraft
            xCoord = raw.getInteger("ChunkLoaderTileX");
        }

        if (raw.hasKey("yCoord", Constants.NBT.TAG_INT)) { // Railcraft
            yCoord = raw.getInteger("yCoord");
        } else if (raw.hasKey("OwnerY", Constants.NBT.TAG_INT)) { // GregTech
            yCoord = raw.getInteger("OwnerY");
        } else if (raw.hasKey("y", Constants.NBT.TAG_INT)) { // Extra Utilities
            yCoord = raw.getInteger("y");
        } else if (raw.hasKey("poppetY", Constants.NBT.TAG_INT)) { // Witchery
            yCoord = raw.getInteger("poppetY");
        } else if (raw.hasKey("ChunkLoaderTileY", Constants.NBT.TAG_INT)) { // Galacticraft
            yCoord = raw.getInteger("ChunkLoaderTileY");
        }

        if (raw.hasKey("zCoord", Constants.NBT.TAG_INT)) { // Railcraft
            zCoord = raw.getInteger("zCoord");
        } else if (raw.hasKey("OwnerZ", Constants.NBT.TAG_INT)) { // GregTech
            zCoord = raw.getInteger("OwnerZ");
        } else if (raw.hasKey("z", Constants.NBT.TAG_INT)) { // Extra Utilities
            zCoord = raw.getInteger("z");
        } else if (raw.hasKey("poppetZ", Constants.NBT.TAG_INT)) { // Witchery
            zCoord = raw.getInteger("poppetZ");
        } else if (raw.hasKey("ChunkLoaderTileZ", Constants.NBT.TAG_INT)) { // Galacticraft
            zCoord = raw.getInteger("ChunkLoaderTileZ");
        }

        if (xCoord != 0 && yCoord != 0 && zCoord != 0) {
            position = new CachedString(String.format("[ %4d %4d %4d ]", xCoord, yCoord, zCoord));
        } else {
            position = NONE_CACHED;
        }
    }

    private void tryParseType(NBTTagCompound raw) {
        String type = null;
        if (raw.hasKey("type", Constants.NBT.TAG_STRING)) { // Railcraft
            type = raw.getString("type");
        } else if (raw.hasKey("id", Constants.NBT.TAG_STRING)) { // Extra Utilities
            type = raw.getString("id");
        } else if (raw.hasKey("townName", Constants.NBT.TAG_STRING)) { // MyTown2
            type = "Town: " + raw.getString("townName");
        }

        if (type != null) {
            this.type = new CachedString(type);
        } else {
            this.type = NONE_CACHED;
        }
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(dim);
        dimName.writeToStream(stream);
        modId.writeToStream(stream);
        playerOrEntityName.writeToStream(stream);
        position.writeToStream(stream);
        type.writeToStream(stream);
        rawData.writeToStream(stream);
        chunks.writeToStream(stream);
    }

    public static DataForcedChunks readFromStream(ByteArrayDataInput stream) {
        DataForcedChunks retVal = new DataForcedChunks();

        retVal.dim = stream.readInt();
        retVal.dimName = CachedString.readFromStream(stream);
        retVal.modId = CachedString.readFromStream(stream);
        retVal.playerOrEntityName = CachedString.readFromStream(stream);
        retVal.position = CachedString.readFromStream(stream);
        retVal.type = CachedString.readFromStream(stream);
        retVal.rawData = CachedString.readFromStream(stream);
        retVal.chunks = CachedString.readFromStream(stream);

        return retVal;
    }
}
