package mcp.mobius.opis.data.profilers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.network.Packet;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.opis.data.holders.newtypes.DataPacket;
import mcp.mobius.opis.data.holders.newtypes.DataPacket250;
import mcp.mobius.opis.helpers.Helpers;

public class ProfilerPacket extends ProfilerAbstract {

    public long dataAmount = 0;
    public HashMap<String, DataPacket> data = new HashMap<String, DataPacket>();
    public HashMap<String, DataPacket250> data250 = new HashMap<String, DataPacket250>();

    public ArrayList<DataPacket> jabbaSpec = new ArrayList<DataPacket>();

    public void startInterval() {
        for (DataPacket packet : data.values()) packet.startInterval();

        for (DataPacket250 packet : data250.values()) packet.startInterval();
    }

    @Override
    public void reset() {
        dataAmount = 0;
    }

    @Override
    public void start(Object msg, Object size) {
        if ((msg != null) && (Helpers.getEffectiveSide() == Side.SERVER)) {
            if (!(msg instanceof FMLProxyPacket)) {
                Packet pkt = (Packet) msg;

                dataAmount += (Integer) size;

                try {
                    data.get(msg.getClass().getSimpleName()).fill(pkt, (Integer) size);
                } catch (Exception e) {
                    data.put(msg.getClass().getSimpleName(), new DataPacket((Packet) msg));
                    data.get(msg.getClass().getSimpleName()).fill(pkt, (Integer) size);
                }
            }
        }
    }

    @Override
    public void stop(Object msg) {
        if ((msg != null) && (Helpers.getEffectiveSide() == Side.SERVER)) {
            if (msg instanceof FMLProxyPacket) {
                FMLProxyPacket pkt = (FMLProxyPacket) msg;
                String channel = pkt.channel();
                int pktsize = pkt.payload().capacity();
                dataAmount += pktsize;

                try {
                    data250.get(channel).fill(pkt, pktsize);
                } catch (Exception e) {
                    data250.put(channel, new DataPacket250(channel));
                    data250.get(channel).fill(pkt, pktsize);
                }
            }
        }
    }
}
