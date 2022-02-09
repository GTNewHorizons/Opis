package mcp.mobius.mobiuscore.asm;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.IOException;

public enum ObfTable {
	WORLD_UPDATEENTITIES      ("ahb", "h", "()V",      "net/minecraft/world/World",           "updateEntities", "()V"),
	WORLD_UPDATEENTITY        ("ahb", "g", "(Lsa;)V",  "net/minecraft/world/World",           "updateEntity",   "(Lnet/minecraft/entity/Entity;)V"),
	TILEENTITY_UPDATEENTITY   ("aor", "h", "()V",      "net/minecraft/tileentity/TileEntity", "updateEntity",   "()V"),

	WORLDSERVER_TICK          ("mt",  "b", "()V",   "net/minecraft/world/WorldServer",   "tick",        "()V"),
	WORLD_PROVIDER            ("mt",  "t", "Laqo;", "net/minecraft/world/WorldServer",   "provider",    "Lnet/minecraft/world/WorldProvider;"),
	WORLDPROVIDER_DIMID       ("aqo", "i", "I",     "net/minecraft/world/WorldProvider", "dimensionId", "I"),	
	
	SERIALIZER_ENCODE         ("fa",  "a", "(Lio/netty/channel/ChannelHandlerContext;Lft;Lio/netty/buffer/ByteBuf;)V", "net/minecraft/util/MessageSerializer", "encode", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;Lio/netty/buffer/ByteBuf;)V"),
	DESERIALIZER_DECODE       ("ez",  "decode", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V", "net/minecraft/util/MessageDeserializer", "decode", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V"),

	PACKET_WRITEPACKETDATA    ("ft", "b", "(Let;)V", "net/minecraft/network/Packet","writePacketData","(Lnet/minecraft/network/PacketBuffer;)V"),	
	PACKET_READPACKETDATA     ("ft", "a", "(Let;)V", "net/minecraft/network/Packet","readPacketData", "(Lnet/minecraft/network/PacketBuffer;)V"),
	PACKET_GENERATEPACKET     ("ft", "a", "(Lcom/google/common/collect/BiMap;I)Lpk;", "net/minecraft/network/Packet", "generatePacket", "(Lcom/google/common/collect/BiMap;I)Lnet/minecraft/network/Packet;"),
	PACKETBUFFER_READABLE     ("et", "readableBytes", "()I", "net/minecraft/network/PacketBuffer", "readableBytes", "()I"),
	PACKETBUFFER_CAPACITY     ("et", "capacity",      "()I", "net/minecraft/network/PacketBuffer", "capacity",      "()I"),
	
	RENDERMANAGER_RENDERENTITY("bnn", "a", "(Lsa;FZ)Z", "net/minecraft/client/renderer/entity/RenderManager", "renderEntityStatic",  "(Lnet/minecraft/entity/Entity;FZ)Z"),
	RENDERMANAGER_RENDERPOSYAW("bnn", "a", "(Lsa;DDDFF)Z", "net/minecraft/client/renderer/entity/RenderManager", "renderEntityWithPosYaw",  "(Lnet/minecraft/entity/Entity;DDDFF)Z"),
	RENDERMANAGER_RENDER      ("bnn", "a", "(Lsa;DDDFFZ)Z", "net/minecraft/client/renderer/entity/RenderManager", "func_147939_a",  "(Lnet/minecraft/entity/Entity;DDDFFZ)Z"),
	
	TERENDER_RENDERAT         ("bml", "a", "(Laor;DDDF)V", "net/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V"),	
	
	FMLCH_PREWORLDTICK        ("cpw/mods/fml/common/FMLCommonHandler","onPreWorldTick","(Lahb;)V", "cpw/mods/fml/common/FMLCommonHandler","onPreWorldTick","(Lnet/minecraft/world/World;)V"),
	FMLCH_POSTWORLDTICK       ("cpw/mods/fml/common/FMLCommonHandler","onPostWorldTick","(Lahb;)V", "cpw/mods/fml/common/FMLCommonHandler","onPostWorldTick","(Lnet/minecraft/world/World;)V"),
	

	FMLPP_PROCESSPACKET       ("cpw/mods/fml/common/network/internal/FMLProxyPacket", "a", "(Lfb;)V", "cpw/mods/fml/common/network/internal/FMLProxyPacket", "processPacket", "(Lnet/minecraft/network/INetHandler;)V"),

	WORLD_INIT                ("ahb", "<init>", "(Lazc;Ljava/lang/String;Lahj;Laqo;Lqi;)V", "net/minecraft/world/World", "<init>", "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;)V"),
	WORLD_LOADEDENTS          ("ahb", "e", "Ljava/util/List;", "net/minecraft/world/World", "loadedEntityList", "Ljava/util/List;"),
	WORLD_LOADEDTILES         ("ahb", "g", "Ljava/util/List;", "net/minecraft/world/World", "loadedTileEntityList", "Ljava/util/List;"),
	/*
	NETWORKLISTEN_NETWORKTICK ("nc",  "c", "()V",      "net/minecraft/network/NetworkSystem",          "networkTick",   "()V"),
	TCPCONN_READPACKET		  ("co",  "i", "()Z",	   "net/minecraft/network/TcpConnection",                "readPacket",    "()Z"),
	TCPCONN_SENDPACKET        ("co",  "a", "(Z)Ley;",  "net/minecraft/network/TcpConnection",                "func_74460_a",  "(Z)Lnet/minecraft/network/packet/Packet;"),
	TCPCONN_NETWORKSOCKET     ("co",  "j", "Ljava/net/Socket;", "net/minecraft/network/TcpConnection",       "networkSocket", "Ljava/net/Socket;"),
	MEMCONN_ADDSENDQUEUE      ("cn",  "a", "(Ley;)V", "net/minecraft/network/MemoryConnection", "addToSendQueue", "(Lnet/minecraft/network/packet/Packet;)V"),
	MEMCONN_PROCESSREAD       ("cn",  "b", "()V", "net/minecraft/network/MemoryConnection", "processReadPackets", "()V"),
	MEMCONN_PAIREDCONN        ("cn",  "d", "Lcn;",    "net/minecraft/network/MemoryConnection", "pairedConnection",     "Lnet/minecraft/network/MemoryConnection;"),
    MEMCONN_PROCESSORCACHE    ("cn",  "b", "(Ley;)V", "net/minecraft/network/MemoryConnection", "processOrCachePacket", "(Lnet/minecraft/network/packet/Packet;)V"),	
    MEMCONN_MYNETHANDLER      ("cn", "e", "Lez;", "net/minecraft/network/MemoryConnection", "myNetHandler", "Lnet/minecraft/network/packet/NetHandler;");
	*/

    //PACKET_PROCESSPACKET      ("ey", "a", "(Lez;)V", "net/minecraft/network/packet/Packet", "processPacket", "(Lnet/minecraft/network/packet/NetHandler;)V"),    
	//PACKET_WRITEPACKET        ("ey",  "a", "(Llp;Ljava/io/DataInput;ZLjava/net/Socket;)Ley;", "net/minecraft/network/packet/Packet", "writePacket", "(Lnet/minecraft/network/packet/Packet;Ljava/io/DataOutput;)V"),
	//PACKET_READPACKET         ("ey",  "a", "(Llp;Ljava/io/DataInput;ZLjava/net/Socket;)Ley;", "net/minecraft/network/packet/Packet", "readPacket", "(Lnet/minecraft/logging/ILogAgent;Ljava/io/DataInput;ZLjava/net/Socket;)Lnet/minecraft/network/packet/Packet;"),
	;


	private static Boolean isObfuscated = null;
	private static ServerType serverType;
	private String clazzNameN;
	private String methodNameN;
	private String descriptorN;
	private String clazzNameS;
	private String methodNameS;
	private String descriptorS;	
	
	private ObfTable(String clazzn, String namen, String descn, String clazzs, String names, String descs){
		this.clazzNameN = clazzn;
		this.clazzNameS = clazzs;
		this.methodNameN = namen;
		this.methodNameS = names;
		this.descriptorN = descn;
		this.descriptorS = descs;
	}
	
	public String getClazz(){
		if (deobfuscatedEnvironment())
			return this.clazzNameS;
		else
			return this.clazzNameN;
	}

	public String getName(){
		if (deobfuscatedEnvironment())
			return this.methodNameS;
		else
			return this.methodNameN;
	}	

	public String getDescriptor(){
		if (deobfuscatedEnvironment())
			return this.descriptorS;
		else
			return this.descriptorN;
	}		
	
	public String getFullDescriptor(){
		if (deobfuscatedEnvironment())
			return this.methodNameS + " " + this.descriptorS;
		else
			return this.methodNameN + " " + this.descriptorN;
	}
	
	public static Boolean deobfuscatedEnvironment(){
		if (ObfTable.isObfuscated != null)
			return ObfTable.isObfuscated;
		
		try{
		// Are we in a 'decompiled' environment?
			byte[] bs = ((LaunchClassLoader)CoreContainer.class.getClassLoader()).getClassBytes("net.minecraft.world.World");
			if (bs != null){
				CoreDescription.log.info("Current code is UNOBFUSCATED");
				ObfTable.isObfuscated = true;
			} else {
				CoreDescription.log.info("Current code is OBFUSCATED");
				ObfTable.isObfuscated = false;
			}

		}catch (IOException e1){}
		
		return ObfTable.isObfuscated;
	}	
	
	public static ServerType getServerType() {
		if (serverType != null) return serverType;

		if (FMLCommonHandler.instance().getModName().contains("thermos")) {
			serverType = ServerType.Thermos;
			CoreDescription.log.info("Switching injection mode to Thermos");
		} else if (FMLCommonHandler.instance().getModName().contains("kcauldron")) {
			serverType = ServerType.KCauldron;
			CoreDescription.log.info("Switching injection mode to KCAULDRON");
		} else if (FMLCommonHandler.instance().getModName().contains("cauldron") || FMLCommonHandler.instance().getModName().contains("mcpc")) {
			serverType = ServerType.Cauldron;
			CoreDescription.log.info("Switching injection mode to CAULDRON");
		} else {
			serverType = ServerType.Forge;
			CoreDescription.log.info("Switching injection mode to FORGE");
		}
		return serverType;
	}
		
	public enum ServerType {
		Forge, Cauldron, KCauldron, Thermos
	}
	
	/*
	public FieldInsnNode getFieldNode(){
		return new FieldInsnNode(Opcodes.GETFIELD, this.getClazz(), this.getName(), this.getDescriptor());
	}
	*/
}
