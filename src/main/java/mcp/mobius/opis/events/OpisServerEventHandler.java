package mcp.mobius.opis.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.opis.data.holders.newtypes.ChatMsg;
import mcp.mobius.opis.data.holders.newtypes.PlayerStatus;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.enums.PlayerEv;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.network.rcon.RConHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;

public class OpisServerEventHandler {

    public static boolean printEntityTrace = false;
    public static boolean printEntityFull = false;

    public static boolean firstLoad = false;

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onWorldLoad(WorldEvent.Load event) {

        /*
        if (!firstLoad && event.world.provider.dimensionId == 0){
        	firstLoad = true;

        	Thread rconserver = new Thread(RConServer.instance);
        	rconserver.setName("Opis Server");
        	rconserver.start();

        	Thread nexusclient = new Thread(NexusClient.instance);
        	nexusclient.setName("Nexus Client");
        	nexusclient.start();

        }
        */

    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) event.entityLiving;
            PacketManager.sendPacketToAllSwing(new NetDataValue(
                    Message.PLAYER_STATUS_UPDATE,
                    new PlayerStatus(
                            p.getGameProfile().getName(),
                            PlayerEv.DEATH,
                            p.worldObj.provider.dimensionId,
                            (int) p.posX,
                            (int) p.posY,
                            (int) p.posZ)));
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        RConHandler.sendToAllNexus(new NetDataValue(Message.CHAT_MSG, new ChatMsg(event.username, event.message)));
    }

    /*
    @SubscribeEvent
       @SideOnly(Side.SERVER)
       public void onEntityConstructed(EntityEvent.EntityConstructing event) {
    	if (printEntityTrace){

    		if (event.entity instanceof EntityItem){
    			System.out.printf("Entity %s of type %s [ %s ] created\n", event.entity.getEntityId(), event.entity.getClass().getName(), ((EntityItem)(event.entity)).getEntityItem().getDisplayName());


    		} else {
    			System.out.printf("Entity %s of type %s created\n", event.entity.getEntityId(), event.entity.getClass().getName());
    		}


    		if (printEntityFull){
    			try{
    				throw new RuntimeException();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    */
}
