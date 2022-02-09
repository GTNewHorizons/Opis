package mcp.mobius.mobiuscore.asm.transformers.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.Opcode;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;

public class TransformerFMLProxyPacket extends TransformerBase {

	private static String FMLPP_PROCESS;
	
	private static AbstractInsnNode[] FMLPP_PROCESS_PAYLOAD;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		FMLPP_PROCESS = ObfTable.FMLPP_PROCESSPACKET.getFullDescriptor();
		
		FMLPP_PROCESS_PAYLOAD = new AbstractInsnNode[]
				{
				Opcode.GETSTATIC(profilerClass, ProfilerSection.PACKET_INBOUND.name(), profilerType),
				Opcode.ALOAD(0),
				Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V"),
				 };			
		
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        MethodNode process  = this.getMethod(classNode, FMLPP_PROCESS);
        this.applyPayloadFirst(process, FMLPP_PROCESS_PAYLOAD);
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        
        return writer.toByteArray();
	}

}
