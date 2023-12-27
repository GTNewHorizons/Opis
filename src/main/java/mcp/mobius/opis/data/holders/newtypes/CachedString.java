package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.helpers.Helpers;

public class CachedString implements Comparable<CachedString>, ISerializable {

    public String str;
    public int index;
    private boolean valid = false;

    public CachedString(int index) {
        this.index = index;
        this.str = StringCache.INSTANCE.getString(index);
        this.valid = !this.str.equals("<ERROR>");
    }

    public CachedString(String str) {

        // if(FMLCommonHandler.instance().getSide() == Side.SERVER){
        if (Helpers.getEffectiveSide() == Side.SERVER) {
            this.index = StringCache.INSTANCE.getIndex(str);
            this.str = str;
        } else {
            this.index = -1;
            this.str = str;
        }
    }

    @Override
    public int compareTo(CachedString o) {
        return o.str.compareTo(this.str); // Reverse order ! Put higher values FIRST
    }

    public String toString() {
        // return String.format("[%d] %s",this.index, this.str);
        if (!this.valid) {
            this.str = StringCache.INSTANCE.getString(index);
            this.valid = !this.str.equals("<ERROR>");
        }

        return this.str;
    }

    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.index);
    }

    public static CachedString readFromStream(ByteArrayDataInput stream) {
        return new CachedString(stream.readInt());
    }
}
