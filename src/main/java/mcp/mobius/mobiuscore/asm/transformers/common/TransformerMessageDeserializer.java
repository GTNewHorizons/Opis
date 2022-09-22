package mcp.mobius.mobiuscore.asm.transformers.common;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformerMessageDeserializer extends TransformerBase {

    private static String DESERIALIZER_DECODE;

    private static AbstractInsnNode[] DESERIALIZER_PATTERN;
    private static AbstractInsnNode[] DESERIALIZER_PAYLOAD;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        DESERIALIZER_DECODE = ObfTable.DESERIALIZER_DECODE.getFullDescriptor();

        DESERIALIZER_PATTERN = new AbstractInsnNode[] {
            new VarInsnNode(Opcodes.ALOAD, -1),
            new VarInsnNode(Opcodes.ALOAD, -1),
            new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    ObfTable.PACKET_READPACKETDATA.getClazz(),
                    ObfTable.PACKET_READPACKETDATA.getName(),
                    ObfTable.PACKET_READPACKETDATA.getDescriptor(),
                    false),
        };

        DESERIALIZER_PAYLOAD = new AbstractInsnNode[] {
            new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.PACKET_INBOUND.name(), profilerType),
            new VarInsnNode(Opcodes.ALOAD, 7),
            new VarInsnNode(Opcodes.ALOAD, 5),
            new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    ObfTable.PACKETBUFFER_CAPACITY.getClazz(),
                    ObfTable.PACKETBUFFER_CAPACITY.getName(),
                    ObfTable.PACKETBUFFER_CAPACITY.getDescriptor(),
                    false),
            new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false),
            new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;Ljava/lang/Object;)V", false),
        };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode decode = this.getMethod(classNode, DESERIALIZER_DECODE);
        this.applyPayloadBefore(decode, DESERIALIZER_PATTERN, DESERIALIZER_PAYLOAD);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
