package mcp.mobius.mobiuscore.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.io.File;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@IFMLLoadingPlugin.TransformerExclusions("mcp.mobius.mobiuscore.asm")
public class CoreDescription implements IFMLLoadingPlugin {

    public static final Logger log = LogManager.getLogger("MobiusCore");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"mcp.mobius.mobiuscore.asm.CoreTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "mcp.mobius.mobiuscore.asm.CoreContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("coremodLocation")) {
            location = (File) data.get("coremodLocation");
        }
    }

    public static File location;

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
