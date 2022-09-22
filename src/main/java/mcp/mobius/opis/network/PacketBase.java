package mcp.mobius.opis.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Method;
import java.util.ArrayList;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.newtypes.DataError;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * @author abrarsyed
 * This is the base packet type for tall the SecretRoomsMod network stuff.
 * Any class that extends this should have an empty constructor, otherwise the network system will fail.
 */
public abstract class PacketBase {
    public byte header;
    public Message msg;
    public String clazzStr;
    public Class clazz;
    public ArrayList<ISerializable> array = null;
    public ISerializable value = null;

    public abstract void encode(ByteArrayDataOutput output);

    public abstract void decode(ByteArrayDataInput input);

    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        throw new RuntimeException("Packet is not going the right way ! Server side packet seen client side.");
    }

    public void actionServer(World world, EntityPlayerMP player) {
        throw new RuntimeException("Packet is not going the right way ! Client side packet seen server side.");
    }

    protected ISerializable dataRead(Class<?> datatype, ByteArrayDataInput istream) {
        if (datatype == null) return new DataError();

        try {
            Method readFromStream = datatype.getMethod("readFromStream", ByteArrayDataInput.class);

            return (ISerializable) readFromStream.invoke(null, istream);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
