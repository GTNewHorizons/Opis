package mcp.mobius.opis.helpers;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.FMLCommonHandler;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;

public class Teleport {

    private static Teleport _instance = new Teleport();

    public static Teleport instance() {
        return _instance;
    };

    // Originally in EntityPlayerMP
    public boolean movePlayerToDimension(EntityPlayerMP player, int dim) {
        return this.transferPlayerToDimension(player.mcServer.getConfigurationManager(), player, dim);
    }

    // Originally in ServerConfigurationManager
    public boolean transferPlayerToDimension(ServerConfigurationManager manager, EntityPlayerMP player, int targetID) {

        int sourceID = player.dimension;
        WorldServer sourceWorld = DimensionManager.getWorld(player.dimension);
        WorldServer targetWorld = DimensionManager.getWorld(targetID);

        if (sourceID == targetID) return true;
        if (sourceWorld == null || targetWorld == null) return false;

        player.dimension = targetID;

        // player.playerNetServerHandler.sendPacket(new Packet9Respawn(player.dimension,
        // (byte)player.worldObj.difficultySetting, targetWorld.getWorldInfo().getTerrainType(),
        // targetWorld.getHeight(), player.theItemInWorldManager.getGameType()));
        player.playerNetServerHandler.sendPacket(
                new S07PacketRespawn(
                        player.dimension,
                        player.worldObj.difficultySetting,
                        player.worldObj.getWorldInfo().getTerrainType(),
                        player.theItemInWorldManager.getGameType()));
        sourceWorld.removePlayerEntityDangerously(player);
        player.isDead = false;
        this.transferEntityToWorld(player, sourceID, sourceWorld, targetWorld);
        this.preloadChunk(player, sourceWorld);

        player.playerNetServerHandler
                .setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        player.theItemInWorldManager.setWorld(targetWorld);

        manager.updateTimeAndWeatherForPlayer(player, targetWorld);
        manager.syncPlayerInventory(player);
        Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();

        while (iterator.hasNext()) {
            PotionEffect potioneffect = iterator.next();
            // player.playerNetServerHandler.sendPacket(new Packet41EntityEffect(player.entityId, potioneffect));
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }

        // GameRegistry.onPlayerChangedDimension(player);
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceID, targetID);

        return true;
    }

    // Originally in ServerConfigurationManager
    public void preloadChunk(EntityPlayerMP player, WorldServer sourceWorld) {
        WorldServer otherWorld = player.getServerForPlayer();

        if (sourceWorld != null) {
            sourceWorld.getPlayerManager().removePlayer(player);
        }

        otherWorld.getPlayerManager().addPlayer(player);
        otherWorld.theChunkProviderServer.loadChunk((int) player.posX >> 4, (int) player.posZ >> 4);
    }

    public void transferEntityToWorld(Entity ent, int dim, WorldServer srcWorld, WorldServer trgWorld) {
        WorldProvider pOld = srcWorld.provider;
        WorldProvider pNew = trgWorld.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = ent.posX * moveFactor;
        double d1 = ent.posZ * moveFactor;
        double d3 = ent.posX;
        double d4 = ent.posY;
        double d5 = ent.posZ;
        float f = ent.rotationYaw;

        srcWorld.theProfiler.startSection("placing");
        d0 = MathHelper.clamp_int((int) d0, -29999872, 29999872);
        d1 = MathHelper.clamp_int((int) d1, -29999872, 29999872);

        if (ent.isEntityAlive()) {
            trgWorld.spawnEntityInWorld(ent);
            ent.setLocationAndAngles(d0, ent.posY, d1, ent.rotationYaw, ent.rotationPitch);
            trgWorld.updateEntityWithOptionalForce(ent, false);
        }

        ent.setWorld(trgWorld);
    }

    public boolean moveEntityToDimension(Entity target, int targetID) {
        if (!target.worldObj.isRemote && !target.isDead) {
            MinecraftServer server = MinecraftServer.getServer();
            int sourceID = target.dimension;
            WorldServer sourceWorld = DimensionManager.getWorld(sourceID);
            WorldServer targetWorld = DimensionManager.getWorld(targetID);

            if (sourceID == targetID) return true;
            if (sourceWorld == null || targetWorld == null) return false;

            target.dimension = targetID;

            target.worldObj.removeEntity(target);
            target.isDead = false;
            this.transferEntityToWorld(target, sourceID, sourceWorld, targetWorld);
            Entity entity = EntityList.createEntityByName(EntityList.getEntityString(target), targetWorld);

            if (entity != null) {
                entity.copyDataFrom(target, true);
                targetWorld.spawnEntityInWorld(entity);
            }

            target.isDead = true;
            sourceWorld.resetUpdateEntityTick();
            targetWorld.resetUpdateEntityTick();
        }

        return true;
    }

    public CoordinatesBlock getTeleportTarget(CoordinatesBlock coord) {
        World world = DimensionManager.getWorld(coord.dim);
        if (world == null) {
            return null;
        }

        int maxOffset = 16;
        boolean targetFound = false;

        if (coord.y > 0) {
            for (int xoffset = 0; xoffset <= maxOffset; xoffset++) {
                for (int zoffset = 0; zoffset <= maxOffset; zoffset++) {
                    if (world.isAirBlock(coord.x + xoffset, coord.y, coord.z + zoffset)
                            && world.isAirBlock(coord.x + xoffset, coord.y + 1, coord.z + zoffset)
                            && !world.isAirBlock(coord.x + xoffset, coord.y - 1, coord.z + zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x + xoffset, coord.y, coord.z + zoffset);

                    if (world.isAirBlock(coord.x - xoffset, coord.y, coord.z + zoffset)
                            && world.isAirBlock(coord.x - xoffset, coord.y + 1, coord.z + zoffset)
                            && !world.isAirBlock(coord.x - xoffset, coord.y - 1, coord.z + zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x - xoffset, coord.y, coord.z + zoffset);

                    if (world.isAirBlock(coord.x + xoffset, coord.y, coord.z - zoffset)
                            && world.isAirBlock(coord.x + xoffset, coord.y + 1, coord.z - zoffset)
                            && !world.isAirBlock(coord.x + xoffset, coord.y - 1, coord.z - zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x + xoffset, coord.y, coord.z - zoffset);

                    if (world.isAirBlock(coord.x - xoffset, coord.y, coord.z - zoffset)
                            && world.isAirBlock(coord.x - xoffset, coord.y + 1, coord.z - zoffset)
                            && !world.isAirBlock(coord.x - xoffset, coord.y - 1, coord.z - zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x - xoffset, coord.y, coord.z - zoffset);
                }
            }
        } else {
            int y = 256;
            while (world.isAirBlock(coord.x, y, coord.z) || world.getBlock(coord.x, y, coord.z) == Blocks.vine) y--;

            for (int xoffset = 0; xoffset <= maxOffset; xoffset++) {
                for (int zoffset = 0; zoffset <= maxOffset; zoffset++) {
                    if (world.isAirBlock(coord.x + xoffset, y, coord.z + zoffset)
                            && world.isAirBlock(coord.x + xoffset, y + 1, coord.z + zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x + xoffset, y, coord.z + zoffset);

                    if (world.isAirBlock(coord.x - xoffset, y, coord.z + zoffset)
                            && world.isAirBlock(coord.x - xoffset, y + 1, coord.z + zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x - xoffset, y, coord.z + zoffset);

                    if (world.isAirBlock(coord.x + xoffset, y, coord.z - zoffset)
                            && world.isAirBlock(coord.x + xoffset, y + 1, coord.z - zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x + xoffset, y, coord.z - zoffset);

                    if (world.isAirBlock(coord.x - xoffset, y, coord.z - zoffset)
                            && world.isAirBlock(coord.x - xoffset, y + 1, coord.z - zoffset))
                        return new CoordinatesBlock(coord.dim, coord.x - xoffset, y, coord.z - zoffset);
                }
            }
        }

        return null;
    }

    public CoordinatesBlock fixNetherTP(CoordinatesBlock target) {
        World targetWorld = DimensionManager.getWorld(target.dim);
        boolean isBedrock = targetWorld.getBlock(target.x, target.y - 1, target.z) == Blocks.bedrock;
        boolean canTeleport = false;
        CoordinatesBlock finalTarget;

        // Fix for the nether and all cavern worlds.
        if ((target.y > 64) && isBedrock) {
            int tempY = target.y - 1;
            while (tempY > 1) {
                if (targetWorld.isAirBlock(target.x, tempY, target.z)
                        && targetWorld.isAirBlock(target.x, tempY - 1, target.z)) {
                    canTeleport = true;

                    while (targetWorld.isAirBlock(target.x, tempY, target.z)) {
                        tempY -= 1;
                    }

                    break;
                }
                tempY -= 1;
            }

            finalTarget = new CoordinatesBlock(target.dim, target.x, tempY + 1, target.z);

        } else {
            canTeleport = true;
            finalTarget = target;
        }

        if (canTeleport) return finalTarget;
        else return null;
    }
}
