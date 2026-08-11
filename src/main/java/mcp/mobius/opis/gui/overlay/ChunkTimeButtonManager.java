package mcp.mobius.opis.gui.overlay;

import net.minecraft.util.ResourceLocation;

import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.buttons.ButtonManager;

public class ChunkTimeButtonManager extends ButtonManager {

    public static final ChunkTimeButtonManager INSTANCE = new ChunkTimeButtonManager();

    private ChunkTimeButtonManager() {}

    @Override
    public ResourceLocation getIcon(SupportedMods mod, String theme) {
        return new ResourceLocation("opis", "textures/icon/chunk_timing.png");
    }

    @Override
    public String getButtonText() {
        return "Opis chunk update time";
    }
}
