package mcp.mobius.opis.gui.overlay;

import com.gtnewhorizons.navigator.api.NavigatorApi;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import mcp.mobius.opis.events.OpisClientTickHandler;

/** Entry point for the Navigator layers. Only reachable once Navigator is confirmed present and new enough. */
public class OpisLayers {

    private static boolean shown = false;

    /** Listens for data without showing any button yet. */
    public static void init() {
        ChunkTimeLayerManager.init();
        LoadedChunkLayerManager.init();
        FMLCommonHandler.instance().bus().register(new OpisLayers());
    }

    /** Cached data belongs to one server; keeping it would show its chunks in the next session. */
    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Fired on the netty thread; clearState touches Navigator caches the render thread is using.
        OpisClientTickHandler.INSTANCE.scheduleOnClientThread(() -> {
            ChunkTimeLayerManager.INSTANCE.clearState();
            LoadedChunkLayerManager.INSTANCE.clearState();
        });
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
