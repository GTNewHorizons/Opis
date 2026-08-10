package mcp.mobius.opis.gui.overlay;

import com.gtnewhorizons.navigator.api.NavigatorApi;

/**
 * Entry point for Opis' Navigator layers. Every class here touches Navigator, so it must only be reached once
 * {@code Loader.isModLoaded("navigator")} has been checked.
 */
public final class OpisLayers {

    private static boolean shown = false;

    private OpisLayers() {}

    /** Starts listening for layer data without putting any button on a map. */
    public static void init() {
        ChunkTimeLayerManager.init();
        LoadedChunkLayerManager.init();
    }

    /**
     * Makes the layers visible to the installed map mods. Deferred until the player actually uses Opis so the buttons
     * do not clutter everyone's map. Idempotent, and must run on the client thread because Navigator's layer list is
     * iterated while rendering.
     */
    public static void show() {
        if (shown) return;
        shown = true;

        NavigatorApi.registerLayerManager(ChunkTimeLayerManager.INSTANCE);
        NavigatorApi.registerLayerManager(LoadedChunkLayerManager.INSTANCE);
    }
}
