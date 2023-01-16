package mcp.mobius.mobiuscore.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import java.util.Arrays;

public class CoreContainer extends DummyModContainer {

    public CoreContainer() {
        super(new ModMetadata());

        ModMetadata md = getMetadata();
        md.modId = "MobiusCore";
        md.name = "MobiusCore";
        md.version = "GRADLETOKEN_VERSION";
        md.credits = "ProfMobius";
        md.authorList = Arrays.asList("ProfMobius");
        md.description = "";
        md.url = "profmobius.blogspot.com";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        // bus.register(this);
        // this needs to return true, otherwise the mod will be deactivated by FML
        return true;
    }
}
