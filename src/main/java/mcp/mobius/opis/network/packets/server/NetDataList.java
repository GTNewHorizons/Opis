package mcp.mobius.opis.network.packets.server;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.DataType;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetDataList extends PacketBase {

    public NetDataList() {}

    public NetDataList(Message msg, List<? extends ISerializable> data) {
        this.msg = msg;
        this.array = new ArrayList<ISerializable>(data);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(this.msg.ordinal());
        output.writeInt(this.array.size());

        if (this.array.size() > 0)
            // Packet.writeString(data.get(0).getClass().getCanonicalName(), ostream);
            output.writeInt(DataType.getForClass(this.array.get(0).getClass()).ordinal());

        for (ISerializable odata : this.array) odata.writeToStream(output);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        this.msg = Message.values()[input.readInt()];
        int ndata = input.readInt();
        this.clazzStr = "";
        if (ndata > 0) {
            this.clazz = DataType.getForOrdinal(input.readInt());
        }

        this.array = new ArrayList<ISerializable>();

        for (int i = 0; i < ndata; i++) this.array.add(dataRead(this.clazz, input));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        MessageHandlerRegistrar.INSTANCE.routeMessage(this.msg, this);
    }
}
