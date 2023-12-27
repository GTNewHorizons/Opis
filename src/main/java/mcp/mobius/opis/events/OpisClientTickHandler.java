package mcp.mobius.opis.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table.Cell;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.clienttypes.DataBlockRender;
import mcp.mobius.opis.data.holders.clienttypes.DataEntityRender;
import mcp.mobius.opis.data.holders.clienttypes.DataRenderEvent;
import mcp.mobius.opis.data.holders.newtypes.DataTileEntityRender;
import mcp.mobius.opis.data.profilers.ProfilerEvent;
import mcp.mobius.opis.data.profilers.ProfilerRenderBlock;
import mcp.mobius.opis.data.profilers.ProfilerRenderEntity;
import mcp.mobius.opis.data.profilers.ProfilerRenderTileEntity;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.timingclient.PanelEventClient;
import mcp.mobius.opis.swing.panels.timingclient.PanelRenderEntities;
import mcp.mobius.opis.swing.panels.timingclient.PanelRenderHandlers;
import mcp.mobius.opis.swing.panels.timingclient.PanelRenderTileEnts;

public enum OpisClientTickHandler {

    INSTANCE;

    public long profilerUpdateTickCounter = 0;
    public long profilerRunningTicks = 0;
    public EventTimer timer500 = new EventTimer(500);
    public EventTimer timer1000 = new EventTimer(1000);
    public EventTimer timer2000 = new EventTimer(2000);
    public EventTimer timer5000 = new EventTimer(5000);
    public EventTimer timer10000 = new EventTimer(10000);

    private final ConcurrentLinkedQueue<Runnable> scheduledCalls = new ConcurrentLinkedQueue<>();

    /** Schedules the provided function to execute on the MC client thread on the next tick */
    public void scheduleOnClientThread(Runnable func) {
        // Don't do anything on dedicated servers
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            scheduledCalls.add(func);
        }
    }

    public void purgeScheduledCallQueue() {
        scheduledCalls.clear();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickEnd(TickEvent.ClientTickEvent event) {

        for (Runnable r = scheduledCalls.poll(); r != null; r = scheduledCalls.poll()) {
            r.run();
        }

        // One second timer
        if (timer1000.isDone()) {
            if (modOpis.swingOpen)
                PacketManager.sendToServer(new PacketReqData(Message.STATUS_PING, new SerialLong(System.nanoTime())));
        }

        if (modOpis.profilerRunClient) {
            ((PanelRenderTileEnts) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS))).getBtnRunRender()
                    .setText("Running...");
            ((PanelRenderEntities) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES))).getBtnRunRender()
                    .setText("Running...");
            ((PanelRenderHandlers) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERHANDLERS))).getBtnRunRender()
                    .setText("Running...");
            ((PanelEventClient) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS))).getBtnRunRender()
                    .setText("Running...");
        } else {
            ((PanelRenderTileEnts) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS))).getBtnRunRender()
                    .setText("Run Render");
            ((PanelRenderEntities) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES))).getBtnRunRender()
                    .setText("Run Render");
            ((PanelRenderHandlers) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERHANDLERS))).getBtnRunRender()
                    .setText("Run Render");
            ((PanelEventClient) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS))).getBtnRunRender()
                    .setText("Run Render");
        }

        profilerUpdateTickCounter++;

        if (profilerRunningTicks < modOpis.profilerMaxTicks && modOpis.profilerRunClient) {
            profilerRunningTicks++;
        } else if (profilerRunningTicks >= modOpis.profilerMaxTicks && modOpis.profilerRunClient) {
            profilerRunningTicks = 0;
            modOpis.profilerRunClient = false;
            ProfilerSection.desactivateAll(Side.CLIENT);

            System.out.printf("Profiling done\n");

            this.updateTabs();
        }
    }

    private void updateTabs() {
        // ====================================================================================

        ArrayList<DataTileEntityRender> tileEntData = new ArrayList<DataTileEntityRender>();
        double tileEntTotal = 0.0D;
        for (TileEntity te : ((ProfilerRenderTileEntity) ProfilerSection.RENDER_TILEENTITY.getProfiler()).data
                .keySet()) {
            try {
                DataTileEntityRender dataTe = new DataTileEntityRender().fill(te);
                tileEntData.add(dataTe);
                tileEntTotal += dataTe.update.timing;
            } catch (Exception e) {
                modOpis.log.warn(String.format("Error while adding entity %s to the list", te));
            }
        }

        System.out.printf("Rendered %d TileEntities\n", tileEntData.size());

        Collections.sort(tileEntData);
        ((PanelRenderTileEnts) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS))).setTable(tileEntData);
        ((PanelRenderTileEnts) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS))).getLblTotal()
                .setText(String.format("Total : %.3f µs", tileEntTotal / 1000.0));

        // ====================================================================================

        ArrayList<DataEntityRender> entData = new ArrayList<DataEntityRender>();
        double entTotal = 0.0D;
        for (Entity ent : ((ProfilerRenderEntity) ProfilerSection.RENDER_ENTITY.getProfiler()).data.keySet()) {
            try {
                DataEntityRender dataEnt = new DataEntityRender().fill(ent);
                entData.add(dataEnt);
                entTotal += dataEnt.update.timing;
            } catch (Exception e) {
                modOpis.log.warn(String.format("Error while adding entity %s to the list", ent));
            }
        }

        System.out.printf("Rendered %d Entities\n", entData.size());

        Collections.sort(entData);
        ((PanelRenderEntities) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES))).setTable(entData);
        ((PanelRenderEntities) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES))).getLblTotal()
                .setText(String.format("Total : %.3f µs", entTotal / 1000.0));

        // ====================================================================================

        // ((PanelRenderHandlers)(TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERHANDLERS))).setTable(TickHandlerManager.getCumulatedStatsRender());

        // ====================================================================================

        ArrayList<DataRenderEvent> timingEvents = new ArrayList<DataRenderEvent>();
        HashBasedTable<Class<?>, String, DescriptiveStatistics> eventData = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).data;
        HashBasedTable<Class<?>, String, String> eventMod = ((ProfilerEvent) ProfilerSection.EVENT_INVOKE
                .getProfiler()).dataMod;
        for (Cell<Class<?>, String, DescriptiveStatistics> cell : eventData.cellSet()) {
            timingEvents.add(new DataRenderEvent().fill(cell, eventMod.get(cell.getRowKey(), cell.getColumnKey())));
        }
        ((PanelEventClient) (TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS))).setTable(timingEvents);

        // ====================================================================================

        ArrayList<DataBlockRender> blockData = new ArrayList<DataBlockRender>();
        for (CoordinatesBlock coord : ((ProfilerRenderBlock) ProfilerSection.RENDER_BLOCK.getProfiler()).data
                .keySet()) {
            try {
                DataBlockRender dataBlock = new DataBlockRender().fill(coord);
                blockData.add(dataBlock);
            } catch (Exception e) {
                modOpis.log.warn(String.format("Error while adding block %s to the list", coord));
            }
        }

        /*
         * Collections.sort(blockData); for (DataBlockRender data : blockData){ ItemStack stack = new ItemStack(data.id,
         * 0, data.meta); String name = stack.getDisplayName(); System.out.printf("%s %s : %s\n", data.pos, name,
         * data.update); }
         */
    }
}
