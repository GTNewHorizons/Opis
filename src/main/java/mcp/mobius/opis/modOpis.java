package mcp.mobius.opis;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mcp.mobius.Tags;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.commands.client.CommandOpis;
import mcp.mobius.opis.commands.server.CommandAddPrivileged;
import mcp.mobius.opis.commands.server.CommandAmountEntities;
import mcp.mobius.opis.commands.server.CommandChunkList;
import mcp.mobius.opis.commands.server.CommandEntityCreate;
import mcp.mobius.opis.commands.server.CommandFrequency;
import mcp.mobius.opis.commands.server.CommandHelp;
import mcp.mobius.opis.commands.server.CommandKill;
import mcp.mobius.opis.commands.server.CommandKillAll;
import mcp.mobius.opis.commands.server.CommandReset;
import mcp.mobius.opis.commands.server.CommandRmPrivileged;
import mcp.mobius.opis.commands.server.CommandStart;
import mcp.mobius.opis.commands.server.CommandStop;
import mcp.mobius.opis.commands.server.CommandTicks;
import mcp.mobius.opis.commands.server.CommandTimingEntities;
import mcp.mobius.opis.commands.server.CommandTimingTileEntities;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.ProfilerDimBlockTick;
import mcp.mobius.opis.data.profilers.ProfilerDimTick;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.data.profilers.ProfilerEvent;
import mcp.mobius.opis.data.profilers.ProfilerNetworkTick;
import mcp.mobius.opis.data.profilers.ProfilerPacket;
import mcp.mobius.opis.data.profilers.ProfilerRenderBlock;
import mcp.mobius.opis.data.profilers.ProfilerRenderEntity;
import mcp.mobius.opis.data.profilers.ProfilerRenderTileEntity;
import mcp.mobius.opis.data.profilers.ProfilerTick;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;
import mcp.mobius.opis.events.OpisClientEventHandler;
import mcp.mobius.opis.events.OpisClientTickHandler;
import mcp.mobius.opis.events.OpisServerEventHandler;
import mcp.mobius.opis.events.OpisServerTickHandler;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.proxy.ProxyServer;
import mcp.mobius.opis.tools.BlockDebug;
import mcp.mobius.opis.tools.BlockLag;
import mcp.mobius.opis.tools.TileDebug;
import mcp.mobius.opis.tools.TileLag;

@Mod(modid = "Opis", name = "Opis", version = Tags.VERSION, acceptableRemoteVersions = "*")
public class modOpis {

    @Instance("Opis")
    public static modOpis instance;

    public static Logger log = LogManager.getLogger("Opis");

    @SidedProxy(clientSide = "mcp.mobius.opis.proxy.ProxyClient", serverSide = "mcp.mobius.opis.proxy.ProxyServer")
    public static ProxyServer proxy;

    public static int profilerDelay = 1;
    public static boolean profilerRun = false;
    public static boolean profilerRunClient = false;
    public static int profilerMaxTicks = 250;
    public static int rconport = 25566;
    public static boolean rconactive = false;
    public static String rconpass = "";
    public static boolean microseconds = true;
    private static int lagGenID = -1;
    public static volatile CoordinatesBlock selectedBlock = null;
    public static boolean swingOpen = false;

    public Configuration config = null;

    public static String commentTables = "Minimum access level to be able to view tables in /opis command. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentOpis = "Minimum access level to be open Opis interface. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentPrivileged = "List of players with PRIVILEGED access level.";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        config = new Configuration(event.getSuggestedConfigurationFile());

        profilerDelay = config.get(Configuration.CATEGORY_GENERAL, "profiler.delay", 1).getInt();
        lagGenID = config.get(Configuration.CATEGORY_GENERAL, "laggenerator_id", -1).getInt();
        profilerMaxTicks = config.get(Configuration.CATEGORY_GENERAL, "profiler.maxpts", 250).getInt();
        microseconds = config.get(Configuration.CATEGORY_GENERAL, "display.microseconds", true).getBoolean(true);
        rconport = config.get("REMOTE_CONSOLE", "opisrcon.port", 25566).getInt();
        rconactive = config.get("REMOTE_CONSOLE", "opisrcon.active", false).getBoolean(false);
        rconpass = config.get("REMOTE_CONSOLE", "opisrcon.password", "").getString();

        String[] users = config.get("ACCESS_RIGHTS", "privileged", new String[] {}, commentPrivileged).getStringList();
        AccessLevel minTables = AccessLevel.PRIVILEGED;
        AccessLevel openOpis = AccessLevel.PRIVILEGED;
        try {
            openOpis = AccessLevel.valueOf(config.get("ACCESS_RIGHTS", "opis", "NONE", commentTables).getString());
        } catch (IllegalArgumentException e) {}
        try {
            minTables = AccessLevel.valueOf(config.get("ACCESS_RIGHTS", "tables", "NONE", commentTables).getString());
        } catch (IllegalArgumentException e) {}

        Message.setTablesMinimumLevel(minTables);
        Message.setOpisMinimumLevel(openOpis);

        for (String s : users) PlayerTracker.INSTANCE.addPrivilegedPlayer(s, false);

        config.save();

        MinecraftForge.EVENT_BUS.register(new OpisClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new OpisServerEventHandler());
        FMLCommonHandler.instance().bus().register(OpisClientTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(OpisServerTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(PlayerTracker.INSTANCE);

        PacketManager.init();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {

        if (lagGenID != -1) {
            Block blockDemo = new BlockLag(Material.wood);
            GameRegistry.registerBlock(blockDemo, "opis.laggen");
            GameRegistry.registerTileEntity(TileLag.class, "opis.laggen");

            Block blockDebug = new BlockDebug(Material.wood);
            GameRegistry.registerBlock(blockDebug, "opis.debug");
            GameRegistry.registerTileEntity(TileDebug.class, "opis.debug");
        }

        ProfilerSection.RENDER_TILEENTITY.setProfiler(new ProfilerRenderTileEntity());
        ProfilerSection.RENDER_ENTITY.setProfiler(new ProfilerRenderEntity());
        ProfilerSection.RENDER_BLOCK.setProfiler(new ProfilerRenderBlock());
        ProfilerSection.EVENT_INVOKE.setProfiler(new ProfilerEvent());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModIdentification.init();
        proxy.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ProfilerSection.DIMENSION_TICK.setProfiler(new ProfilerDimTick());
        ProfilerSection.DIMENSION_BLOCKTICK.setProfiler(new ProfilerDimBlockTick());
        ProfilerSection.ENTITY_UPDATETIME.setProfiler(new ProfilerEntityUpdate());
        ProfilerSection.TICK.setProfiler(new ProfilerTick());
        ProfilerSection.TILEENT_UPDATETIME.setProfiler(new ProfilerTileEntityUpdate());
        ProfilerSection.PACKET_INBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.PACKET_OUTBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.NETWORK_TICK.setProfiler(new ProfilerNetworkTick());

        event.registerServerCommand(new CommandChunkList());
        event.registerServerCommand(new CommandFrequency());
        event.registerServerCommand(new CommandStart());
        event.registerServerCommand(new CommandStop());
        event.registerServerCommand(new CommandTimingTileEntities());
        event.registerServerCommand(new CommandTicks());
        event.registerServerCommand(new CommandTimingEntities());
        event.registerServerCommand(new CommandAmountEntities());
        event.registerServerCommand(new CommandKill());
        event.registerServerCommand(new CommandKillAll());
        event.registerServerCommand(new CommandReset());
        event.registerServerCommand(new CommandEntityCreate());
        event.registerServerCommand(new CommandOpis());
        event.registerServerCommand(new CommandAddPrivileged());
        event.registerServerCommand(new CommandRmPrivileged());

        // event.registerServerCommand(new CommandClientTest());
        // event.registerServerCommand(new CommandClientStart());
        // event.registerServerCommand(new CommandClientShowRenderTick());

        event.registerServerCommand(new CommandHelp());

        // GameRegistry.registerPlayerTracker(PlayerTracker.instance());

        // DeadManSwitch.startDeadManSwitch(MinecraftServer.getServer());
        /*
         * for (ProfilerSection sec : ProfilerSection.values()){ System.out.printf("%s : %s\n", sec,
         * sec.getProfiler().getClass().getSimpleName()); }
         */
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        OpisServerTickHandler.INSTANCE.purgeScheduledCallQueue();
    }
}
