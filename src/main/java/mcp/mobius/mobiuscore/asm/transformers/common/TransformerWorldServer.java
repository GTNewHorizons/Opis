package mcp.mobius.mobiuscore.asm.transformers.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;

public class TransformerWorldServer extends TransformerBase {

    private static String WORLDSERVER_TICK;

    private static AbstractInsnNode[] WORLDSERVER_PAYLOAD_TICKSTART;
    private static AbstractInsnNode[] WORLDSERVER_PAYLOAD_TICKEND;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        WORLDSERVER_TICK = ObfTable.WORLDSERVER_TICK.getFullDescriptor();

        /*
         * WORLDSERVER_PAYLOAD_TICKSTART = new AbstractInsnNode[] { new FieldInsnNode (Opcodes.GETSTATIC,
         * "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerWorldTick",
         * "Lmcp/mobius/mobiuscore/profiler/IProfilerWorldTick;"), new VarInsnNode (Opcodes.ALOAD, 0), new FieldInsnNode
         * (Opcodes.GETFIELD, "net/minecraft/world/WorldServer", "provider", "Lnet/minecraft/world/WorldProvider;"), new
         * FieldInsnNode (Opcodes.GETFIELD, "net/minecraft/world/WorldProvider", "dimensionId", "I"), new
         * MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerWorldTick",
         * "WorldTickStart", "(I)V"), };
         */

        WORLDSERVER_PAYLOAD_TICKSTART = new AbstractInsnNode[] {
                new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        profilerClass,
                        ProfilerSection.DIMENSION_BLOCKTICK.name(),
                        profilerType),
                new VarInsnNode(Opcodes.ALOAD, 0),
                new FieldInsnNode(
                        Opcodes.GETFIELD,
                        ObfTable.WORLD_PROVIDER.getClazz(),
                        ObfTable.WORLD_PROVIDER.getName(),
                        ObfTable.WORLD_PROVIDER.getDescriptor()),
                new FieldInsnNode(
                        Opcodes.GETFIELD,
                        ObfTable.WORLDPROVIDER_DIMID.getClazz(),
                        ObfTable.WORLDPROVIDER_DIMID.getName(),
                        ObfTable.WORLDPROVIDER_DIMID.getDescriptor()),
                new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V", false), };

        WORLDSERVER_PAYLOAD_TICKEND = new AbstractInsnNode[] {
                new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        profilerClass,
                        ProfilerSection.DIMENSION_BLOCKTICK.name(),
                        profilerType),
                new VarInsnNode(Opcodes.ALOAD, 0),
                new FieldInsnNode(
                        Opcodes.GETFIELD,
                        ObfTable.WORLD_PROVIDER.getClazz(),
                        ObfTable.WORLD_PROVIDER.getName(),
                        ObfTable.WORLD_PROVIDER.getDescriptor()),
                new FieldInsnNode(
                        Opcodes.GETFIELD,
                        ObfTable.WORLDPROVIDER_DIMID.getClazz(),
                        ObfTable.WORLDPROVIDER_DIMID.getName(),
                        ObfTable.WORLDPROVIDER_DIMID.getDescriptor()),
                new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V", false), };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode tickNode = this.getMethod(classNode, WORLDSERVER_TICK);
        this.applyPayloadFirst(tickNode, WORLDSERVER_PAYLOAD_TICKSTART);
        this.applyPayloadLast(tickNode, WORLDSERVER_PAYLOAD_TICKEND);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
