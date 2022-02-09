package mcp.mobius.mobiuscore.asm;

import java.util.HashMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Opcode {

	public static InsnNode DUP(){
		return new InsnNode(Opcodes.DUP);
	}	
	
	public static VarInsnNode ALOAD(int val){
		return new VarInsnNode(Opcodes.ALOAD, val);
	}
	
	public static TypeInsnNode NEW(String name){
		return new TypeInsnNode(Opcodes.NEW, name);
	}
	
	public static FieldInsnNode GETFIELD(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.GETFIELD, clazz, field, sig);
	}
	
	public static FieldInsnNode GETFIELD(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.GETFIELD, clazz, field, sig);
	}	
	
	public static FieldInsnNode PUTFIELD(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.PUTFIELD, clazz, field, sig);
	}
	
	public static FieldInsnNode PUTFIELD(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.PUTFIELD, clazz, field, sig);
	}		
	
	public static FieldInsnNode GETSTATIC(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.GETSTATIC, clazz, field, sig);
	}	
	
	public static FieldInsnNode GETSTATIC(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.GETSTATIC, clazz, field, sig);
	}	
	
	public static MethodInsnNode INVOKEINTERFACE(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz, field, sig, true);
	}
	
	public static MethodInsnNode INVOKEINTERFACE(String clazz, String field, String sig){
		return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz, field, sig, true);
	}
	
	public static MethodInsnNode INVOKEVIRTUAL(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz, field, sig, false);
	}
	
	public static MethodInsnNode INVOKEVIRTUAL(String clazz, String field, String sig){
		return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz, field, sig, false);
	}
	
	public static MethodInsnNode INVOKESPECIAL(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new MethodInsnNode(Opcodes.INVOKESPECIAL, clazz, field, sig, false);
	}	
	
	public static MethodInsnNode INVOKESPECIAL(String clazz, String field, String sig){
		return new MethodInsnNode(Opcodes.INVOKESPECIAL, clazz, field, sig, false);
	}
	
	public static BiMap<String, Integer> Instructions;
		
	static{
		Instructions = HashBiMap.create();
		
	    Instructions.put("NOP",0); // visitInsn
	    Instructions.put("ACONST_NULL",1);
	    Instructions.put("ICONST_M1",2);
	    Instructions.put("ICONST_0",3);
	    Instructions.put("ICONST_1",4);
	    Instructions.put("ICONST_2",5);
	    Instructions.put("ICONST_3",6);
	    Instructions.put("ICONST_4",7);
	    Instructions.put("ICONST_5",8);
	    Instructions.put("LCONST_0",9);
	    Instructions.put("LCONST_1",10);
	    Instructions.put("FCONST_0",11);
	    Instructions.put("FCONST_1",12);
	    Instructions.put("FCONST_2",13);
	    Instructions.put("DCONST_0",14);
	    Instructions.put("DCONST_1",15);
	    Instructions.put("BIPUSH",16);
	    Instructions.put("SIPUSH",17);
	    Instructions.put("LDC",18);
	    Instructions.put("LDC_W",19);
	    Instructions.put("LDC2_W",20);
	    Instructions.put("ILOAD",21);
	    Instructions.put("LLOAD",22);
	    Instructions.put("FLOAD",23);
	    Instructions.put("DLOAD",24);
	    Instructions.put("ALOAD",25);
	    Instructions.put("ILOAD_0",26);
	    Instructions.put("ILOAD_1",27);
	    Instructions.put("ILOAD_2",28);
	    Instructions.put("ILOAD_3",29);
	    Instructions.put("LLOAD_0",30);
	    Instructions.put("LLOAD_1",31);
	    Instructions.put("LLOAD_2",32);
	    Instructions.put("LLOAD_3",33);
	    Instructions.put("FLOAD_0",34);
	    Instructions.put("FLOAD_1",35);
	    Instructions.put("FLOAD_2",36);
	    Instructions.put("FLOAD_3",37);
	    Instructions.put("DLOAD_0",38);
	    Instructions.put("DLOAD_1",39);
	    Instructions.put("DLOAD_2",40);
	    Instructions.put("DLOAD_3",41);
	    Instructions.put("ALOAD_0",42);
	    Instructions.put("ALOAD_1",43);
	    Instructions.put("ALOAD_2",44);
	    Instructions.put("ALOAD_3",45);
	    Instructions.put("IALOAD",46);
	    Instructions.put("LALOAD",47);
	    Instructions.put("FALOAD",48);
	    Instructions.put("DALOAD",49);
	    Instructions.put("AALOAD",50);
	    Instructions.put("BALOAD",51);
	    Instructions.put("CALOAD",52);
	    Instructions.put("SALOAD",53);
	    Instructions.put("ISTORE",54);
	    Instructions.put("LSTORE",55);
	    Instructions.put("FSTORE",56);
	    Instructions.put("DSTORE",57);
	    Instructions.put("ASTORE",58);
	    Instructions.put("ISTORE_0",59);
	    Instructions.put("ISTORE_1",60);
	    Instructions.put("ISTORE_2",61);
	    Instructions.put("ISTORE_3",62);
	    Instructions.put("LSTORE_0",63);
	    Instructions.put("LSTORE_1",64);
	    Instructions.put("LSTORE_2",65);
	    Instructions.put("LSTORE_3",66);
	    Instructions.put("FSTORE_0",67);
	    Instructions.put("FSTORE_1",68);
	    Instructions.put("FSTORE_2",69);
	    Instructions.put("FSTORE_3",70);
	    Instructions.put("DSTORE_0",71);
	    Instructions.put("DSTORE_1",72);
	    Instructions.put("DSTORE_2",73);
	    Instructions.put("DSTORE_3",74);
	    Instructions.put("ASTORE_0",75);
	    Instructions.put("ASTORE_1",76);
	    Instructions.put("ASTORE_2",77);
	    Instructions.put("ASTORE_3",78);
	    Instructions.put("IASTORE",79);
	    Instructions.put("LASTORE",80);
	    Instructions.put("FASTORE",81);
	    Instructions.put("DASTORE",82);
	    Instructions.put("AASTORE",83);
	    Instructions.put("BASTORE",84);
	    Instructions.put("CASTORE",85);
	    Instructions.put("SASTORE",86);
	    Instructions.put("POP",87);
	    Instructions.put("POP2",88);
	    Instructions.put("DUP",89);
	    Instructions.put("DUP_X1",90);
	    Instructions.put("DUP_X2",91);
	    Instructions.put("DUP2",92);
	    Instructions.put("DUP2_X1",93);
	    Instructions.put("DUP2_X2",94);
	    Instructions.put("SWAP",95);
	    Instructions.put("IADD",96);
	    Instructions.put("LADD",97);
	    Instructions.put("FADD",98);
	    Instructions.put("DADD",99);
	    Instructions.put("ISUB",100);
	    Instructions.put("LSUB",101);
	    Instructions.put("FSUB",102);
	    Instructions.put("DSUB",103);
	    Instructions.put("IMUL",104);
	    Instructions.put("LMUL",105);
	    Instructions.put("FMUL",106);
	    Instructions.put("DMUL",107);
	    Instructions.put("IDIV",108);
	    Instructions.put("LDIV",109);
	    Instructions.put("FDIV",110);
	    Instructions.put("DDIV",111);
	    Instructions.put("IREM",112);
	    Instructions.put("LREM",113);
	    Instructions.put("FREM",114);
	    Instructions.put("DREM",115);
	    Instructions.put("INEG",116);
	    Instructions.put("LNEG",117);
	    Instructions.put("FNEG",118);
	    Instructions.put("DNEG",119);
	    Instructions.put("ISHL",120);
	    Instructions.put("LSHL",121);
	    Instructions.put("ISHR",122);
	    Instructions.put("LSHR",123);
	    Instructions.put("IUSHR",124);
	    Instructions.put("LUSHR",125);
	    Instructions.put("IAND",126);
	    Instructions.put("LAND",127);
	    Instructions.put("IOR",128);
	    Instructions.put("LOR",129);
	    Instructions.put("IXOR",130);
	    Instructions.put("LXOR",131);
	    Instructions.put("IINC",132);
	    Instructions.put("I2L",133);
	    Instructions.put("I2F",134);
	    Instructions.put("I2D",135);
	    Instructions.put("L2I",136);
	    Instructions.put("L2F",137);
	    Instructions.put("L2D",138);
	    Instructions.put("F2I",139);
	    Instructions.put("F2L",140);
	    Instructions.put("F2D",141);
	    Instructions.put("D2I",142);
	    Instructions.put("D2L",143);
	    Instructions.put("D2F",144);
	    Instructions.put("I2B",145);
	    Instructions.put("I2C",146);
	    Instructions.put("I2S",147);
	    Instructions.put("LCMP",148);
	    Instructions.put("FCMPL",149);
	    Instructions.put("FCMPG",150);
	    Instructions.put("DCMPL",151);
	    Instructions.put("DCMPG",152);
	    Instructions.put("IFEQ",153);
	    Instructions.put("IFNE",154);
	    Instructions.put("IFLT",155);
	    Instructions.put("IFGE",156);
	    Instructions.put("IFGT",157);
	    Instructions.put("IFLE",158);
	    Instructions.put("IF_ICMPEQ",159);
	    Instructions.put("IF_ICMPNE",160);
	    Instructions.put("IF_ICMPLT",161);
	    Instructions.put("IF_ICMPGE",162);
	    Instructions.put("IF_ICMPGT",163);
	    Instructions.put("IF_ICMPLE",164);
	    Instructions.put("IF_ACMPEQ",165);
	    Instructions.put("IF_ACMPNE",166);
	    Instructions.put("GOTO",167);
	    Instructions.put("JSR",168);
	    Instructions.put("RET",169);
	    Instructions.put("TABLESWITCH",170);
	    Instructions.put("LOOKUPSWITCH",171);
	    Instructions.put("IRETURN",172);
	    Instructions.put("LRETURN",173);
	    Instructions.put("FRETURN",174);
	    Instructions.put("DRETURN",175);
	    Instructions.put("ARETURN",176);
	    Instructions.put("RETURN",177);
	    Instructions.put("GETSTATIC",178);
	    Instructions.put("PUTSTATIC",179);
	    Instructions.put("GETFIELD",180);
	    Instructions.put("PUTFIELD",181);
	    Instructions.put("INVOKEVIRTUAL",182);
	    Instructions.put("INVOKESPECIAL",183);
	    Instructions.put("INVOKESTATIC",184);
	    Instructions.put("INVOKEINTERFACE",185);
	    Instructions.put("INVOKEDYNAMIC",186);
	    Instructions.put("NEW",187);
	    Instructions.put("NEWARRAY",188);
	    Instructions.put("ANEWARRAY",189);
	    Instructions.put("ARRAYLENGTH",190);
	    Instructions.put("ATHROW",191);
	    Instructions.put("CHECKCAST",192);
	    Instructions.put("INSTANCEOF",193);
	    Instructions.put("MONITORENTER",194);
	    Instructions.put("MONITOREXIT",195);
	    Instructions.put("WIDE",196);
	    Instructions.put("MULTIANEWARRAY",197);
	    Instructions.put("IFNULL",198);
	    Instructions.put("IFNONNULL",199);
	    Instructions.put("GOTO_W",200);
	    Instructions.put("JSR_W",201);		
	}
	
}
