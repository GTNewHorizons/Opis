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

public class TransformerTERenderer extends TransformerBase {

    private static String TER_RENDER;

    private static AbstractInsnNode[] TER_RENDER_PAYLOAD_TOP;
    private static AbstractInsnNode[] TER_RENDER_PAYLOAD_BOTTOM;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        // TER_RENDER = "a (Lasp;F)V";
        TER_RENDER = ObfTable.TERENDER_RENDERAT.getFullDescriptor();

        TER_RENDER_PAYLOAD_TOP = new AbstractInsnNode[] {
                new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        profilerClass,
                        ProfilerSection.RENDER_TILEENTITY.name(),
                        profilerType),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V", false) };

        TER_RENDER_PAYLOAD_BOTTOM = new AbstractInsnNode[] {
                new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        profilerClass,
                        ProfilerSection.RENDER_TILEENTITY.name(),
                        profilerType),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V", false) };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode renderEntNode = this.getMethod(classNode, TER_RENDER);
        this.applyPayloadFirst(renderEntNode, TER_RENDER_PAYLOAD_TOP);
        this.applyPayloadLast(renderEntNode, TER_RENDER_PAYLOAD_BOTTOM);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
