package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.newtypes.DataStringUpdate;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;

import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

public enum StringCache implements IMessageHandler {

    INSTANCE;

    private int currentIndex = -1;
    // private HashBiMap<Integer, String> cache = HashBiMap.create();
    // private ArrayList<DataStringUpdate> toSend = new ArrayList<DataStringUpdate>();
    private final BiMap<Integer, String> cache = Maps.synchronizedBiMap(HashBiMap.<Integer, String>create());
    private ConcurrentLinkedQueue<DataStringUpdate> fullsync = new ConcurrentLinkedQueue<DataStringUpdate>();
    private ConcurrentLinkedQueue<DataStringUpdate> unsynced = new ConcurrentLinkedQueue<DataStringUpdate>(); // This is
                                                                                                              // the
                                                                                                              // current
                                                                                                              // list of
                                                                                                              // unsynced

    private StringCache() {
        this.getIndex("Server");
    }

    public String getString(int index) {
        synchronized (cache) {
            String retVal = this.cache.get(index);
            if (retVal == null) {
                return "<ERROR>";
            } else return retVal;
        }
    }

    public int getIndex(String str) {
        synchronized (cache) {
            if (cache.inverse().containsKey(str)) {
                return cache.inverse().get(str);
            } else {
                currentIndex += 1;
                cache.put(currentIndex, str);

                DataStringUpdate upd = new DataStringUpdate(str, currentIndex);
                this.fullsync.add(upd);
                this.unsynced.add(upd);

                // PacketManager.sendToAll(new NetDataValue(Message.STATUS_STRINGUPD, upd));
                return currentIndex;
            }
        }
    }

    public void syncCache(EntityPlayerMP player) {

        int i = 0;

        ArrayList<DataStringUpdate> toSendCopy = new ArrayList<>(fullsync);

        while (i < toSendCopy.size()) {
            PacketManager.sendToPlayer(
                    new NetDataList(
                            Message.STATUS_STRINGUPD_FULL,
                            toSendCopy.subList(i, Math.min(i + 50, toSendCopy.size()))),
                    player);
            i += 50;
        }
    }

    public void syncNewCache() {
        while (!unsynced.isEmpty()) {
            DataStringUpdate update = this.unsynced.poll();
            PacketManager.sendToAll(new NetDataValue(Message.STATUS_STRINGUPD, update));
        }
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case STATUS_STRINGUPD: {
                // modOpis.log.info(String.format("Received partial String Cache update : [ %d ] [ %s ]",
                // ((DataStringUpdate)rawdata.value).index, ((DataStringUpdate)rawdata.value).str));

                DataStringUpdate data = (DataStringUpdate) rawdata.value;
                // modOpis.log.info(String.format("++++ Received String Cache update : [%d] %s", data.index, data.str));
                synchronized (cache) {
                    try {
                        this.cache.put(data.index, data.str);
                    } catch (IllegalArgumentException e) {
                        this.cache.remove(data.index);
                        this.cache.inverse().remove(data.str);
                        this.cache.put(data.index, data.str);
                    }
                }

                TabPanelRegistrar.INSTANCE.refreshAll();

                break;
            }
            case STATUS_STRINGUPD_FULL: {
                // modOpis.log.info(String.format("Received full String Cache update containing %d entries",
                // rawdata.array.size()));

                for (ISerializable idata : rawdata.array) {
                    DataStringUpdate data = (DataStringUpdate) idata;
                    // modOpis.log.info(String.format("++++ Received String Cache update : [%d] %s", data.index,
                    // data.str));
                    synchronized (cache) {
                        try {
                            this.cache.put(data.index, data.str);
                        } catch (IllegalArgumentException e) {
                            this.cache.remove(data.index);
                            this.cache.inverse().remove(data.str);
                            this.cache.put(data.index, data.str);
                        }
                    }
                }

                TabPanelRegistrar.INSTANCE.refreshAll();

                break;
            }
            default:
                return false;
        }
        return true;
    }
}
