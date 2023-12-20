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

public class TransformerRenderManager extends TransformerBase {

    // private static String RM_RENDERENT;
    // private static String RM_RENDERPOSYAW;
    private static String RM_RENDER;

    private static AbstractInsnNode[] RM_RENDER_PAYLOAD_TOP;
    private static AbstractInsnNode[] RM_RENDER_PAYLOAD_BOTTOM;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        // RM_RENDERENT = ObfTable.RENDERMANAGER_RENDERENTITY.getFullDescriptor();
        // RM_RENDERPOSYAW = ObfTable.RENDERMANAGER_RENDERPOSYAW.getFullDescriptor();

        RM_RENDER = ObfTable.RENDERMANAGER_RENDER.getFullDescriptor();

        RM_RENDER_PAYLOAD_TOP = new AbstractInsnNode[] {
                new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_ENTITY.name(), profilerType),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V", false) };

        RM_RENDER_PAYLOAD_BOTTOM = new AbstractInsnNode[] {
                new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_ENTITY.name(), profilerType),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V", false) };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode renderEntNode = this.getMethod(classNode, RM_RENDER);
        this.applyPayloadFirst(renderEntNode, RM_RENDER_PAYLOAD_TOP);

        this.applyPayloadLast(renderEntNode, RM_RENDER_PAYLOAD_BOTTOM);

        /*
         * MethodNode renderPosYawNode = this.getMethod(classNode, RM_RENDERPOSYAW);
         * this.applyPayloadFirst(renderPosYawNode, RM_RENDER_PAYLOAD_TOP); this.applyPayloadLast (renderPosYawNode,
         * RM_RENDER_PAYLOAD_BOTTOM);
         */

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
