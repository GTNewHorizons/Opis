package mcp.mobius.mobiuscore.asm.transformers.common;

import mcp.mobius.mobiuscore.asm.Opcode;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformerFMLOutboundHandler extends TransformerBase {

    private static String FMLOH_WRITE;

    private static AbstractInsnNode[] FMLOH_WRITE_PATTERN;
    private static AbstractInsnNode[] FMLOH_WRITE_PAYLOAD;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        FMLOH_WRITE =
                "write (Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Object;Lio/netty/channel/ChannelPromise;)V";

        FMLOH_WRITE_PATTERN = new AbstractInsnNode[] {
            Opcode.INVOKEVIRTUAL(
                    "cpw/mods/fml/common/network/handshake/NetworkDispatcher.sendProxy (Lcpw/mods/fml/common/network/internal/FMLProxyPacket;)V"),
        };

        FMLOH_WRITE_PAYLOAD = new AbstractInsnNode[] {
            Opcode.GETSTATIC(profilerClass, ProfilerSection.PACKET_OUTBOUND.name(), profilerType),
            Opcode.ALOAD(4),
            Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V"),
        };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode write = this.getMethod(classNode, FMLOH_WRITE);
        this.applyPayloadAfter(write, FMLOH_WRITE_PATTERN, FMLOH_WRITE_PAYLOAD);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
