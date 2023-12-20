package mcp.mobius.mobiuscore.asm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import mcp.mobius.mobiuscore.asm.transformers.common.TransformerASMEventHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLCommonHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLOutboundHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerMessageDeserializer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerMessageSerializer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerRenderManager;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerTERenderer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerWorldServer;
import mcp.mobius.mobiuscore.asm.transformers.crucible.TransformerWorldCrucible;
import mcp.mobius.mobiuscore.asm.transformers.forge.TransformerWorld;
import mcp.mobius.mobiuscore.asm.transformers.kcauldron.TransformerWorldKCauldron;
import mcp.mobius.mobiuscore.asm.transformers.mcpc.TransformerWorldCauldron;
import mcp.mobius.mobiuscore.asm.transformers.thermos.TransformerWorldThermos;

public class CoreTransformer implements IClassTransformer {

    public CoreTransformer() {
        super();
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        try {

            // TransformerBase.dumpChecksum(bytes, name, srgname);

            if (srgname.equals("net.minecraft.world.World")) {
                ObfTable.ServerType serverType = ObfTable.getServerType();

                if (serverType == ObfTable.ServerType.Forge) {
                    bytes = new TransformerWorld().transform(name, srgname, bytes);
                } else if (serverType == ObfTable.ServerType.Cauldron) {
                    bytes = new TransformerWorldCauldron().transform(name, srgname, bytes);
                } else if (serverType == ObfTable.ServerType.KCauldron) {
                    bytes = new TransformerWorldKCauldron().transform(name, srgname, bytes);
                } else if (serverType == ObfTable.ServerType.Thermos) {
                    bytes = new TransformerWorldThermos().transform(name, srgname, bytes);
                } else if (serverType == ObfTable.ServerType.Crucible) {
                    bytes = new TransformerWorldCrucible().transform(name, srgname, bytes);
                }
            }

            if (srgname.equals("net.minecraft.world.WorldServer")) {
                bytes = new TransformerWorldServer().transform(name, srgname, bytes);
            }

            if (srgname.equals("net.minecraft.util.MessageSerializer")) {
                bytes = new TransformerMessageSerializer().transform(name, srgname, bytes);
            }

            if (srgname.equals("net.minecraft.util.MessageDeserializer")) {
                bytes = new TransformerMessageDeserializer().transform(name, srgname, bytes);
            }

            if (srgname.equals("net.minecraft.client.renderer.entity.RenderManager")) {
                bytes = new TransformerRenderManager().transform(name, srgname, bytes);
            }

            if (srgname.equals("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher")) {
                bytes = new TransformerTERenderer().transform(name, srgname, bytes);
            }

            if (srgname.equals("cpw.mods.fml.common.FMLCommonHandler")) {
                bytes = new TransformerFMLCommonHandler().transform(name, srgname, bytes);
            }

            if (srgname.equals("cpw.mods.fml.common.network.FMLOutboundHandler")) {
                bytes = new TransformerFMLOutboundHandler().transform(name, srgname, bytes);
            }

            // if (srgname.equals("cpw.mods.fml.common.network.internal.FMLProxyPacket")){
            // bytes = new TransformerFMLProxyPacket().transform(name, srgname, bytes);
            // }

            if (srgname.equals("cpw.mods.fml.common.eventhandler.ASMEventHandler")) {
                bytes = new TransformerASMEventHandler().transform(name, srgname, bytes);
            }

        } catch (Exception e) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            try {
                PrintWriter pw = new PrintWriter(String.format("%s.asm", srgname.replaceAll("/", ".")));
                TraceClassVisitor cv = new TraceClassVisitor(pw);
                classReader.accept(cv, 0);
                pw.flush();
                throw new RuntimeException(e);
            } catch (FileNotFoundException f) {
                throw new RuntimeException("DERP");
            }
        }
        return bytes;
    }
}
