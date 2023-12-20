package mcp.mobius.opis.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModIdentification {

    public static HashMap<String, String> modSource_Name = new HashMap<String, String>();
    public static HashMap<String, String> modSource_ID = new HashMap<String, String>();
    public static HashMap<Integer, String> itemMap = new HashMap<Integer, String>();
    public static HashMap<String, String> keyhandlerStrings = new HashMap<String, String>();

    public static void init() {

        for (ModContainer mod : Loader.instance().getModList()) {
            modSource_Name.put(mod.getSource().getName(), mod.getName());
            modSource_ID.put(mod.getSource().getName(), mod.getModId());
        }

        // TODO : Update this to match new version (1.7.2)
        modSource_Name.put("1.6.2.jar", "Minecraft");
        modSource_Name.put("1.6.3.jar", "Minecraft");
        modSource_Name.put("1.6.4.jar", "Minecraft");
        modSource_Name.put("1.7.2.jar", "Minecraft");
        modSource_Name.put("Forge", "Minecraft");
        modSource_ID.put("1.6.2.jar", "Minecraft");
        modSource_ID.put("1.6.3.jar", "Minecraft");
        modSource_ID.put("1.6.4.jar", "Minecraft");
        modSource_ID.put("1.7.2.jar", "Minecraft");
        modSource_ID.put("Forge", "Minecraft");
    }

    public static String nameFromObject(Object obj) {
        String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        try {
            objPath = URLDecoder.decode(objPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String modName = "<Unknown>";
        for (String s : modSource_Name.keySet()) if (objPath.contains(s)) {
            modName = modSource_Name.get(s);
            break;
        }

        if (modName.equals("Minecraft Coder Pack")) modName = "Minecraft";

        return modName;
    }

    public static String nameFromStack(ItemStack stack) {
        try {
            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
            GameRegistry.UniqueIdentifier ui = new GameRegistry.UniqueIdentifier(name);
            ModContainer mod = Loader.instance().getIndexedModList().get(ui.modId);
            String modname = mod == null ? "Minecraft" : mod.getName();
            return modname;
        } catch (NullPointerException e) {
            // System.out.printf("NPE : %s\n",itemstack.toString());
            return "";
        }
    }

    public static String getStackName(int id, int meta) {
        return getStackName(Block.getBlockById(id), meta);
    }

    public static String getStackName(Block block, int meta) {
        ItemStack is;
        String name = String.format("te.%s.%d", block, meta);

        try {
            is = new ItemStack(block, 1, meta);
            name = is.getDisplayName();
        } catch (Exception e) {}

        return name;
    }

    public static String getModStackName(int id, int meta) {
        return getModStackName(Block.getBlockById(id), meta);
    }

    public static String getModStackName(Block block, int meta) {
        ItemStack is;
        String modID = "<UNKNOWN>";

        try {
            is = new ItemStack(block, 1, meta);
            modID = nameFromStack(is);
        } catch (Exception e) {}

        return modID;
    }
}
