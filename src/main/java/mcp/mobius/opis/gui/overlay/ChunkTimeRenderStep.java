package mcp.mobius.opis.gui.overlay;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.navigator.api.NavigatorApi;
import com.gtnewhorizons.navigator.api.model.steps.UniversalLocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;

public class ChunkTimeRenderStep extends UniversalLocationInteractableStep<ChunkTimeLocation> {

    public ChunkTimeRenderStep(ChunkTimeLocation location) {
        super(location);
    }

    @Override
    public void draw(double x, double y, float drawScale, double zoom) {
        DrawUtils.drawRect(
                x,
                y,
                getAdjustedWidth(),
                getAdjustedHeight(),
                location.getColor(),
                modOpis.overlayAlphaTiming);
        DrawUtils.drawHollowRect(x, y, getAdjustedWidth(), getAdjustedHeight(), 0x000000, 255);
    }

    @Override
    public void getTooltip(List<String> list) {
        StatsChunk stats = location.getStats();
        CoordinatesChunk chunk = stats.getChunk();

        list.add(String.format("Chunk [%d, %d]", chunk.chunkX, chunk.chunkZ));
        list.add(
                modOpis.microseconds ? String.format("%.3f µs", stats.getDataSum() / 1000.0)
                        : String.format("%.5f ms", stats.getDataSum() / 1000.0 / 1000.0));
        list.add(String.format("%d tile entities, %d entities", stats.tileEntities, stats.entities));
        list.add(EnumChatFormatting.GRAY + teleportHint());
    }

    /** Server side this is PRIVILEGED, so it is silently ignored for players without access. */
    @Override
    public void onActionKeyPressed() {
        PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_CHUNK, location.getStats().getChunk()));
        Minecraft.getMinecraft().setIngameFocus();
    }

    /** The action key is rebindable, so read the current binding rather than hardcoding a name. */
    static String teleportHint() {
        return "[" + Keyboard.getKeyName(NavigatorApi.ACTION_KEY.getKeyCode()) + "] Teleport here";
    }
}
