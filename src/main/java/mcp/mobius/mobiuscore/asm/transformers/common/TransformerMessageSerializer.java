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

public class TransformerMessageSerializer extends TransformerBase {

	private static String SERIALIZER_ENCODE;
	
	private static AbstractInsnNode[] SERIALIZER_PATTERN;
	private static AbstractInsnNode[] SERIALIZER_PAYLOAD;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();		
		
		SERIALIZER_ENCODE = ObfTable.SERIALIZER_ENCODE.getFullDescriptor();

		SERIALIZER_PATTERN =	new AbstractInsnNode[] 
				{
				new VarInsnNode   (Opcodes.ALOAD, -1),
				new VarInsnNode   (Opcodes.ALOAD, -1),
				new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ObfTable.PACKET_WRITEPACKETDATA.getClazz(), ObfTable.PACKET_WRITEPACKETDATA.getName(), ObfTable.PACKET_WRITEPACKETDATA.getDescriptor(), false)
				};

		SERIALIZER_PAYLOAD =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       profilerClass, ProfilerSection.PACKET_OUTBOUND.name(), profilerType),
				 new VarInsnNode   (Opcodes.ALOAD, 2),
				 new VarInsnNode   (Opcodes.ALOAD, 5),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ObfTable.PACKETBUFFER_READABLE.getClazz(), ObfTable.PACKETBUFFER_READABLE.getName(), ObfTable.PACKETBUFFER_READABLE.getDescriptor(), false),
				 new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;Ljava/lang/Object;)V", false),
				};
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
        
        MethodNode encode  = this.getMethod(classNode, SERIALIZER_ENCODE);
        this.applyPayloadAfter(encode, SERIALIZER_PATTERN, SERIALIZER_PAYLOAD);
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES |ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);        
        
		return writer.toByteArray();
	}

}
