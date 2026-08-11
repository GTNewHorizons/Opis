package mcp.mobius.opis.gui.overlay;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizons.navigator.api.model.steps.UniversalLocationInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;

public class LoadedChunkRenderStep extends UniversalLocationInteractableStep<LoadedChunkLocation> {

    public LoadedChunkRenderStep(LoadedChunkLocation location) {
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
                modOpis.overlayAlphaLoaded);
        DrawUtils.drawHollowRect(x, y, getAdjustedWidth(), getAdjustedHeight(), 0x000000, 255);
    }

    @Override
    public void getTooltip(List<String> list) {
        CoordinatesChunk chunk = location.getChunk();

        list.add(String.format("Chunk [%d, %d]", chunk.chunkX, chunk.chunkZ));
        list.add(location.isForced() ? "Force loaded" : "Game loaded");
        list.add(EnumChatFormatting.GRAY + ChunkTimeRenderStep.teleportHint());
    }

    @Override
    public void onActionKeyPressed() {
        PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_CHUNK, location.getChunk()));
        Minecraft.getMinecraft().setIngameFocus();
    }
}
