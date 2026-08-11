package mcp.mobius.opis.gui.overlay;

import net.minecraft.util.ResourceLocation;

import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.buttons.ButtonManager;

public class LoadedChunkButtonManager extends ButtonManager {

    public static final LoadedChunkButtonManager INSTANCE = new LoadedChunkButtonManager();

    private LoadedChunkButtonManager() {}

    @Override
    public ResourceLocation getIcon(SupportedMods mod, String theme) {
        return new ResourceLocation("opis", "textures/icon/chunk_loaded.png");
    }

    @Override
    public String getButtonText() {
        return "Opis loaded chunks";
    }
}
