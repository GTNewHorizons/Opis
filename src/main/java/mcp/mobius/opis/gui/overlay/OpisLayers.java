package mcp.mobius.opis.gui.overlay;

import com.gtnewhorizons.navigator.api.NavigatorApi;

/** Entry point for the Navigator layers. Only reachable once Navigator is confirmed present and new enough. */
public final class OpisLayers {

    private static boolean shown = false;

    private OpisLayers() {}

    /** Listens for data without showing any button yet. */
    public static void init() {
        ChunkTimeLayerManager.init();
        LoadedChunkLayerManager.init();
    }

    /**
     * Deferred until first use of Opis so the buttons do not clutter every map. Must run on the client thread, as
     * Navigator's layer list is iterated while rendering.
     */
    public static void show() {
        if (shown) return;
        shown = true;

        NavigatorApi.registerLayerManager(ChunkTimeLayerManager.INSTANCE);
        NavigatorApi.registerLayerManager(LoadedChunkLayerManager.INSTANCE);
    }
}
