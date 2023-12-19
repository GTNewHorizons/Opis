package mcp.mobius.opis.data.holders;

import com.google.common.collect.HashBiMap;

import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialDouble;
import mcp.mobius.opis.data.holders.basetypes.SerialFloat;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.basetypes.SerialString;
import mcp.mobius.opis.data.holders.basetypes.TargetEntity;
import mcp.mobius.opis.data.holders.basetypes.TicketData;
import mcp.mobius.opis.data.holders.clienttypes.DataBlockRender;
import mcp.mobius.opis.data.holders.clienttypes.DataEntityRender;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.ChatMsg;
import mcp.mobius.opis.data.holders.newtypes.ConnectionProperties;
import mcp.mobius.opis.data.holders.newtypes.DataAmountRate;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTick;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataByteRate;
import mcp.mobius.opis.data.holders.newtypes.DataByteSize;
import mcp.mobius.opis.data.holders.newtypes.DataChunk;
import mcp.mobius.opis.data.holders.newtypes.DataChunkEntities;
import mcp.mobius.opis.data.holders.newtypes.DataDimension;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataEvent;
import mcp.mobius.opis.data.holders.newtypes.DataNetworkTick;
import mcp.mobius.opis.data.holders.newtypes.DataPacket;
import mcp.mobius.opis.data.holders.newtypes.DataPacket250;
import mcp.mobius.opis.data.holders.newtypes.DataStringUpdate;
import mcp.mobius.opis.data.holders.newtypes.DataThread;
import mcp.mobius.opis.data.holders.newtypes.DataTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataTileEntityRender;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.holders.newtypes.DataTimingMillisecond;
import mcp.mobius.opis.data.holders.newtypes.NexusAuth;
import mcp.mobius.opis.data.holders.newtypes.NexusData;
import mcp.mobius.opis.data.holders.newtypes.PlayerStatus;
import mcp.mobius.opis.data.holders.stats.StatAbstract;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.modOpis;

public enum DataType {

    AMOUNTHOLDER(AmountHolder.class),
    COORDINATESBLOCK(CoordinatesBlock.class),
    COORDINATESCHUNK(CoordinatesChunk.class),
    SERIALDOUBLE(SerialDouble.class),
    SERIALFLOAT(SerialFloat.class),
    SERIALINT(SerialInt.class),
    SERIALLONG(SerialLong.class),
    SERIALSTRING(SerialString.class),
    TARGETENTITY(TargetEntity.class),
    TICKETDATA(TicketData.class),

    CACHEDSTRING(CachedString.class),
    DATAAMOUNTRATE(DataAmountRate.class),
    DATABITRATE(DataByteRate.class),
    DATABITSIZE(DataByteSize.class),
    DATABLOCKRENDER(DataBlockRender.class),
    DATABLOCKTICK(DataBlockTick.class),
    DATABLOCKTILEENTITY(DataBlockTileEntity.class),
    DATABLOCKTILEENTITYPERCLASS(DataBlockTileEntityPerClass.class),
    DATACHUNK(DataChunk.class),
    DATACHUNKENTITIES(DataChunkEntities.class),
    DATADIMENSION(DataDimension.class),
    DATAENTITY(DataEntity.class),
    DATAENTITYPERCLASS(DataEntityPerClass.class),
    DATAENTITYRENDER(DataEntityRender.class),
    DATAEVENT(DataEvent.class),
    DATANETWORKTICK(DataNetworkTick.class),
    DATAPACKET(DataPacket.class),
    DATAPACKET250(DataPacket250.class),
    DATASTRINGUPD(DataStringUpdate.class),
    DATATHREAD(DataThread.class),
    DATATILEENTITY(DataTileEntity.class),
    DATATILEENTITYRENDER(DataTileEntityRender.class),
    DATATIMING(DataTiming.class),
    DATATIMINGMILLISECOND(DataTimingMillisecond.class),
    STATABSTRACT(StatAbstract.class),
    STATSCHUNK(StatsChunk.class),
    NEXUSDATA(NexusData.class),
    NEXUSAUTH(NexusAuth.class),
    PLAYERLOGGING(PlayerStatus.class),
    CONNECTIONPROPERTIES(ConnectionProperties.class),
    CHATMSG(ChatMsg.class);

    private Class clazz;
    private static HashBiMap<DataType, Class> bimap = HashBiMap.create(50);

    static {
        for (DataType type : DataType.values()) bimap.put(type, type.clazz);
    }

    private DataType(Class clazz) {
        this.clazz = clazz;
    }

    public static DataType getForClass(Class clazz) {
        DataType type = bimap.inverse().get(clazz);
        if (type == null) {
            modOpis.log.warn(String.format("Class %s was not registered with the DataType enum", clazz));
        }
        return type;
    }

    public static Class<?> getForOrdinal(int ordinal) {
        Class<?> retVal;
        try {
            DataType type = DataType.values()[ordinal];
            retVal = bimap.get(type);
            if (retVal == null) {
                modOpis.log.warn(String.format("DataType doesn't have a class registered for %s", type));
            }
        } catch (Exception e) {
            retVal = null;
            modOpis.log.warn(String.format("Index out of bound for ordinal %d in DataType", ordinal));
        }
        return retVal;
    }
}
