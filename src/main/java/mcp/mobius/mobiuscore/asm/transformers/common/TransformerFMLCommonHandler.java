package mcp.mobius.mobiuscore.asm.transformers.common;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.Opcode;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformerFMLCommonHandler extends TransformerBase {

    // (Ljava/util/EnumSet<Lcpw/mods/fml/common/TickType;>;Lcpw/mods/fml/relauncher/Side;[Ljava/lang/Object;)V
    private static String FMLCH_ONPRESERVERTICK;
    private static String FMLCH_ONPOSTSERVERTICK;
    private static String FMLCH_ONPREWORLDTICK;
    private static String FMLCH_ONPOSTWORLDTICK;

    private static AbstractInsnNode[] FMLCH_PAYLOAD_PRESERVERTICK;
    private static AbstractInsnNode[] FMLCH_PAYLOAD_POSTSERVERTICK;

    private static AbstractInsnNode[] FMLCH_PAYLOAD_PREWORLDTICK;
    private static AbstractInsnNode[] FMLCH_PAYLOAD_POSTWORLDTICK;

    private static boolean isEclipse;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        FMLCH_ONPRESERVERTICK = "onPreServerTick ()V";
        FMLCH_ONPOSTSERVERTICK = "onPostServerTick ()V";

        FMLCH_ONPREWORLDTICK = ObfTable.FMLCH_PREWORLDTICK.getFullDescriptor();
        FMLCH_ONPOSTWORLDTICK = ObfTable.FMLCH_POSTWORLDTICK.getFullDescriptor();

        FMLCH_PAYLOAD_PRESERVERTICK = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.TICK.name(), profilerType),
                Opcode.INVOKEVIRTUAL(profilerClass, "start", "()V"), };

        FMLCH_PAYLOAD_POSTSERVERTICK = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.TICK.name(), profilerType),
                Opcode.INVOKEVIRTUAL(profilerClass, "stop", "()V"), };

        FMLCH_PAYLOAD_PREWORLDTICK = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.DIMENSION_TICK.name(), profilerType), Opcode.ALOAD(1),
                Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V") };

        FMLCH_PAYLOAD_POSTWORLDTICK = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.DIMENSION_TICK.name(), profilerType), Opcode.ALOAD(1),
                Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V") };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode preServerTickNode = this.getMethod(classNode, FMLCH_ONPRESERVERTICK);
        this.applyPayloadFirst(preServerTickNode, FMLCH_PAYLOAD_PRESERVERTICK);

        MethodNode postServerTickNode = this.getMethod(classNode, FMLCH_ONPOSTSERVERTICK);
        this.applyPayloadLast(postServerTickNode, FMLCH_PAYLOAD_POSTSERVERTICK);

        MethodNode preworldTick = this.getMethod(classNode, FMLCH_ONPREWORLDTICK);
        this.applyPayloadFirst(preworldTick, FMLCH_PAYLOAD_PREWORLDTICK);

        MethodNode postWorldTick = this.getMethod(classNode, FMLCH_ONPOSTWORLDTICK);
        this.applyPayloadLast(postWorldTick, FMLCH_PAYLOAD_POSTWORLDTICK);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
