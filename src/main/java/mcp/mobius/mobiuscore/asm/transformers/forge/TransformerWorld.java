package mcp.mobius.mobiuscore.asm.transformers.forge;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import mcp.mobius.mobiuscore.asm.CoreDescription;
import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.Opcode;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;

public class TransformerWorld extends TransformerBase {

    private static String WORLD_UPDATEENTITIES;
    private static String WORLD_INIT;

    // tileentity.updateEntity(); World:2215
    private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_TEUPDATE;

    // ProfilerRegistrar.profilerTileEntity.Start(tileentity);
    private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_TEUPDATE;

    // ProfilerRegistrar.profilerTileEntity.Stop(tileentity);
    private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE;

    // this.updateEntity(entity);
    private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_ENTUPDATE;

    // ProfilerRegistrar.profilerEntity.Start(entity);
    private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE;

    // ProfilerRegistrar.profilerEntity.Stop(entity);
    private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE;

    // World.loadedEntityList
    private static AbstractInsnNode[] WORLD_PATTERN_LOADEDENTS;

    // World.loadedTileEntityList
    private static AbstractInsnNode[] WORLD_PATTERN_LOADEDTILES;

    // World.loadedEntityList
    private static AbstractInsnNode[] WORLD_PAYLOAD_LOADEDENTS;

    // World.loadedTileEntityList
    private static AbstractInsnNode[] WORLD_PAYLOAD_LOADEDTILES;

    static {
        String profilerClass = ProfilerSection.getClassName();
        String profilerType = ProfilerSection.getTypeName();

        WORLD_UPDATEENTITIES = ObfTable.WORLD_UPDATEENTITIES.getFullDescriptor(); // updateEntities.getFullDescriptor();
        WORLD_INIT = ObfTable.WORLD_INIT.getFullDescriptor();

        WORLD_UPDATE_PATTERN_TEUPDATE = new AbstractInsnNode[] { new LineNumberNode(-1, new LabelNode()),
                Opcode.ALOAD(-1),
                Opcode.INVOKEVIRTUAL(
                        ObfTable.TILEENTITY_UPDATEENTITY.getClazz(),
                        ObfTable.TILEENTITY_UPDATEENTITY.getName(),
                        ObfTable.TILEENTITY_UPDATEENTITY.getDescriptor()) };

        WORLD_UPDATE_PAYLOAD_START_TEUPDATE = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.TILEENT_UPDATETIME.name(), profilerType),
                Opcode.ALOAD(8), Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V") };

        WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.TILEENT_UPDATETIME.name(), profilerType),
                Opcode.ALOAD(8), Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V") };

        WORLD_UPDATE_PATTERN_ENTUPDATE = new AbstractInsnNode[] { new LineNumberNode(-1, new LabelNode()),
                Opcode.ALOAD(-1), Opcode.ALOAD(-1),
                Opcode.INVOKEVIRTUAL(
                        ObfTable.WORLD_UPDATEENTITY.getClazz(),
                        ObfTable.WORLD_UPDATEENTITY.getName(),
                        ObfTable.WORLD_UPDATEENTITY.getDescriptor()) };

        WORLD_UPDATE_PAYLOAD_START_ENTUPDATE = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.ENTITY_UPDATETIME.name(), profilerType),
                Opcode.ALOAD(2), Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V") };

        WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE = new AbstractInsnNode[] {
                Opcode.GETSTATIC(profilerClass, ProfilerSection.ENTITY_UPDATETIME.name(), profilerType),
                Opcode.ALOAD(2), Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V") };

        WORLD_PATTERN_LOADEDENTS = new AbstractInsnNode[] { Opcode.ALOAD(0), Opcode.NEW("java/util/ArrayList"),
                Opcode.DUP(), Opcode.INVOKESPECIAL("java/util/ArrayList", "<init>", "()V"),
                Opcode.PUTFIELD(
                        ObfTable.WORLD_LOADEDENTS.getClazz(),
                        ObfTable.WORLD_LOADEDENTS.getName(),
                        ObfTable.WORLD_LOADEDENTS.getDescriptor()) };

        WORLD_PATTERN_LOADEDTILES = new AbstractInsnNode[] { Opcode.ALOAD(0), Opcode.NEW("java/util/ArrayList"),
                Opcode.DUP(), Opcode.INVOKESPECIAL("java/util/ArrayList", "<init>", "()V"),
                Opcode.PUTFIELD(
                        ObfTable.WORLD_LOADEDTILES.getClazz(),
                        ObfTable.WORLD_LOADEDTILES.getName(),
                        ObfTable.WORLD_LOADEDTILES.getDescriptor()) };

        WORLD_PAYLOAD_LOADEDENTS = new AbstractInsnNode[] { Opcode.ALOAD(0),
                Opcode.NEW("mcp/mobius/mobiuscore/monitors/MonitoredEntityList"), Opcode.DUP(),
                Opcode.INVOKESPECIAL("mcp/mobius/mobiuscore/monitors/MonitoredEntityList", "<init>", "()V"),
                Opcode.PUTFIELD(
                        ObfTable.WORLD_LOADEDENTS.getClazz(),
                        ObfTable.WORLD_LOADEDENTS.getName(),
                        ObfTable.WORLD_LOADEDENTS.getDescriptor()) };

        WORLD_PAYLOAD_LOADEDTILES = new AbstractInsnNode[] { Opcode.ALOAD(0),
                Opcode.NEW("mcp/mobius/mobiuscore/monitors/MonitoredTileList"), Opcode.DUP(),
                Opcode.INVOKESPECIAL("mcp/mobius/mobiuscore/monitors/MonitoredTileList", "<init>", "()V"),
                Opcode.PUTFIELD(
                        ObfTable.WORLD_LOADEDTILES.getClazz(),
                        ObfTable.WORLD_LOADEDTILES.getName(),
                        ObfTable.WORLD_LOADEDTILES.getDescriptor()) };
    }

    @Override
    public byte[] transform(String name, String srgname, byte[] bytes) {
        dumpChecksum(bytes, name, srgname);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        MethodNode updateEntitiesNode = this.getMethod(classNode, WORLD_UPDATEENTITIES);
        CoreDescription.log.info("Found World.updateEntities()...");

        this.applyPayloadBefore(updateEntitiesNode, WORLD_UPDATE_PATTERN_TEUPDATE, WORLD_UPDATE_PAYLOAD_START_TEUPDATE);
        this.applyPayloadAfter(updateEntitiesNode, WORLD_UPDATE_PATTERN_TEUPDATE, WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE);

        this.applyPayloadBefore(
                updateEntitiesNode,
                WORLD_UPDATE_PATTERN_ENTUPDATE,
                WORLD_UPDATE_PAYLOAD_START_ENTUPDATE);
        this.applyPayloadAfter(updateEntitiesNode, WORLD_UPDATE_PATTERN_ENTUPDATE, WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE);

        MethodNode initNode = this.getMethod(classNode, WORLD_INIT);
        CoreDescription.log.info(String.format("Found World.%s %s", initNode.name, initNode.desc));

        this.applyPayloadAfter(initNode, WORLD_PATTERN_LOADEDENTS, WORLD_PAYLOAD_LOADEDENTS);
        this.applyPayloadAfter(initNode, WORLD_PATTERN_LOADEDTILES, WORLD_PAYLOAD_LOADEDTILES);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
