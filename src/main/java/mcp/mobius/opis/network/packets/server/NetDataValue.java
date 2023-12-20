package mcp.mobius.opis.network.packets.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.DataType;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public class NetDataValue extends PacketBase {

    public NetDataValue() {}

    public NetDataValue(Message msg, ISerializable data) {
        this.msg = msg;
        this.value = data;
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(this.msg.ordinal());
        output.writeInt(DataType.getForClass(this.value.getClass()).ordinal());
        this.value.writeToStream(output);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        this.msg = Message.values()[input.readInt()];
        this.clazz = DataType.getForOrdinal(input.readInt());
        this.value = dataRead(this.clazz, input);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        MessageHandlerRegistrar.INSTANCE.routeMessage(this.msg, this);
    }
}
