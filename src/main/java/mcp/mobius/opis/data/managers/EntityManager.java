package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcp.mobius.mobiuscore.monitors.MonitoredEntityList;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.holders.newtypes.PlayerStatus;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.helpers.Teleport;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.PlayerEv;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public enum EntityManager {

    INSTANCE;

    public ArrayList<DataEntity> getWorses(int amount) {
        ArrayList<DataEntity> sorted = new ArrayList<DataEntity>();
        ArrayList<DataEntity> topEntities = new ArrayList<DataEntity>();

        for (Entity entity : ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet())
            sorted.add(new DataEntity().fill(entity));

        Collections.sort(sorted);

        int i = 0;
        while (topEntities.size() < Math.min(amount, sorted.size()) && (i < sorted.size())) {
            DataEntity testats = sorted.get(i);

            if (testats.npoints < 40) {
                i++;
                continue;
            }
            if (DimensionManager.getWorld(testats.pos.dim) == null) {
                i++;
                continue;
            }

            topEntities.add(testats);
            i++;
        }
        return topEntities;
    }

    /* Returns all the entities in all dimensions (without timing data) */
    public ArrayList<DataEntity> getAllEntities() {
        ArrayList<DataEntity> entities = new ArrayList<DataEntity>();
        for (int i : DimensionManager.getIDs()) {
            entities.addAll(this.getEntitiesInDim(i));
        }
        return entities;
    }

    /* Returns all the entities in the given dimension (without timing data) */
    public ArrayList<DataEntity> getEntitiesInDim(int dim) {
        ArrayList<DataEntity> entities = new ArrayList<DataEntity>();

        World world = DimensionManager.getWorld(dim);
        if (world == null) return entities;

        for (Object o : world.loadedEntityList.toArray()) {
            Entity ent = (Entity) o;
            entities.add(new DataEntity().fill(ent));
        }

        return entities;
    }

    /* Returns a hashmap of all entities per chunk (not timing) */
    public HashMap<CoordinatesChunk, ArrayList<DataEntity>> getAllEntitiesPerChunk() {
        HashMap<CoordinatesChunk, ArrayList<DataEntity>> entities = new HashMap<CoordinatesChunk, ArrayList<DataEntity>>();
        for (int i : DimensionManager.getIDs()) {
            entities.putAll(this.getEntitiesPerChunkInDim(i));
        }
        return entities;
    }

    /* Returns a hashmap of entities in the given dimension (not timing) */
    public HashMap<CoordinatesChunk, ArrayList<DataEntity>> getEntitiesPerChunkInDim(int dim) {
        HashMap<CoordinatesChunk, ArrayList<DataEntity>> entities = new HashMap<CoordinatesChunk, ArrayList<DataEntity>>();
        World world = DimensionManager.getWorld(dim);
        if (world == null) return entities;

        for (Object o : world.loadedEntityList.toArray()) {
            Entity ent = (Entity) o;
            CoordinatesChunk chunk = new CoordinatesBlock(ent.dimension, (int) ent.posX, (int) ent.posY, (int) ent.posZ)
                    .asCoordinatesChunk();

            if (!entities.containsKey(chunk)) entities.put(chunk, new ArrayList<DataEntity>());

            // entities.get(chunk).add(new EntityStats(ent.entityId, ent.getClass().getName(), ent.dimension, ent.posX,
            // ent.posY, ent.posZ));
            entities.get(chunk).add(new DataEntity().fill(ent));
        }

        return entities;
    }

    /* Returns an array of all entities in a given chunk */
    public ArrayList<DataEntity> getEntitiesInChunk(CoordinatesChunk coord) {
        ArrayList<DataEntity> entities = new ArrayList<DataEntity>();

        World world = DimensionManager.getWorld(coord.dim);
        if (world == null) return entities;

        for (Object o : world.loadedEntityList.toArray()) {
            Entity ent = (Entity) o;
            CoordinatesChunk chunk = new CoordinatesBlock(ent.dimension, (int) ent.posX, (int) ent.posY, (int) ent.posZ)
                    .asCoordinatesChunk();
            if (chunk.equals(coord))
                // entities.add(new EntityStats(ent.entityId, ent.getClass().getName(), ent.dimension, ent.posX,
                // ent.posY, ent.posZ));
                entities.add(new DataEntity().fill(ent));
        }

        return entities;
    }

    /* Returns a hashmap with the entity name and amount of it on the server */
    public ArrayList<AmountHolder> getCumulativeEntities(boolean filtered) {
        ArrayList<AmountHolder> cumData = new ArrayList<AmountHolder>();

        for (int dim : DimensionManager.getIDs()) {
            World world = DimensionManager.getWorld(dim);
            if (world == null) continue;

            if (!(world.loadedEntityList instanceof MonitoredEntityList)) {
                modOpis.log.info(
                        "Improper type detected for entity list in world " + dim + ". Regenerating tracking list.");
                List<Entity> newList = new MonitoredEntityList<Entity>();
                for (Object o : world.loadedEntityList) newList.add((Entity) o);
                world.loadedEntityList = newList;
            }

            Map<String, Integer> count = ((MonitoredEntityList) world.loadedEntityList).getCount();
            for (String key : count.keySet())
                if (count.get(key) > 0) cumData.add(new AmountHolder(key, count.get(key), "Plop"));
        }
        return cumData;
    }

    public boolean teleportPlayer(CoordinatesBlock coord, EntityPlayerMP player) {
        // System.out.printf("%s %s\n", coord, getTeleportTarget(coord));
        CoordinatesBlock target = Teleport.instance().getTeleportTarget(coord);
        if (target == null) return false;

        target = Teleport.instance().fixNetherTP(target);

        if (target == null) return false;

        if (Teleport.instance().movePlayerToDimension(player, coord.dim))
            player.setPositionAndUpdate(target.x + 0.5, target.y, target.z + 0.5);
        else return false;

        return true;
    }

    public boolean teleportEntity(Entity src, Entity trg, EntityPlayerMP msgtrg) {
        if ((src == null) && (msgtrg != null)) {
            PacketManager.sendChatMsg(String.format("\u00A7oCannot find source entity %s", src), msgtrg);
            return false;
        }

        if ((trg == null) && (msgtrg != null)) {
            PacketManager.sendChatMsg(String.format("\u00A7oCannot find target entity %s", src), msgtrg);
            return false;
        }

        if (src instanceof EntityPlayerMP) {
            if (Teleport.instance().movePlayerToDimension((EntityPlayerMP) src, trg.worldObj.provider.dimensionId))
                src.setLocationAndAngles(trg.posX, trg.posY, trg.posZ, src.rotationYaw, src.rotationPitch);
            else return false;
        } else {
            if (Teleport.instance().moveEntityToDimension(src, trg.worldObj.provider.dimensionId))
                src.setLocationAndAngles(trg.posX, trg.posY, trg.posZ, src.rotationYaw, src.rotationPitch);
            else return false;
        }

        return true;
    }

    public Entity getEntity(int eid, int dim) {
        World world = DimensionManager.getWorld(dim);
        if (world == null) return null;

        Entity entity = world.getEntityByID(eid);
        return entity;
    }

    public String getEntityName(Entity ent) {
        return getEntityName(ent, false);
    }

    public String getEntityName(Entity ent, boolean filtered) {
        if (ent instanceof EntityItem && filtered) {
            return "Dropped Item";
        } else if (ent instanceof EntityItem && !filtered) {
            try {
                return "[Stack] " + ((EntityItem) ent).getEntityItem().getDisplayName();
            } catch (Exception e) {
                return "<Unknown dropped item>";
            }
        }

        if (ent instanceof EntityPlayerMP && filtered) return "Player";
        else if (ent instanceof EntityPlayerMP && !filtered)
            return "[ Player ] " + ((EntityPlayerMP) ent).getGameProfile().getName();

        String name = ent.getCommandSenderName();

        if (name.contains(".")) {
            String[] namelst = ent.getClass().getName().split("\\.");
            return namelst[namelst.length - 1];
        }

        return name;
    }

    public DataTiming getTotalUpdateTime() {
        double updateTime = 0D;
        for (Entity entity : ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet()) {
            updateTime += ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.get(entity)
                    .getGeometricMean();
        }
        return new DataTiming(updateTime);
    }

    public int killAll(String entName) {
        int nkilled = 0;

        if (entName.contains("Player")) {
            return -1; // Error msg for when trying to kill a player
        }

        for (int dim : DimensionManager.getIDs()) {
            World world = DimensionManager.getWorld(dim);
            if (world == null) continue;

            for (Object o : world.loadedEntityList.toArray()) {
                Entity ent = (Entity) o;
                String nameFiltered = this.getEntityName(ent, true).toLowerCase();
                String nameUnfiltered = this.getEntityName(ent, false).toLowerCase();

                if (nameFiltered.equals(entName.toLowerCase()) || nameUnfiltered.equals(entName.toLowerCase())) {
                    ent.setDead();
                    nkilled += 1;
                }
            }
        }

        System.out.printf("Killed %d %s\n", nkilled, entName);

        return nkilled;
    }

    public ArrayList<PlayerStatus> getAllPlayers() {
        // List players = MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).playerEntityList;
        List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        ArrayList<PlayerStatus> outList = new ArrayList<PlayerStatus>();

        for (Object o : players) {
            // outList.add(new DataEntity().fill((EntityPlayer) p));
            EntityPlayer p = (EntityPlayer) o;
            outList.add(
                    new PlayerStatus(
                            p.getGameProfile().getName(),
                            PlayerEv.ONLINE,
                            p.worldObj.provider.dimensionId,
                            (int) p.posX,
                            (int) p.posY,
                            (int) p.posZ));
        }

        return outList;
    }

    public int getAmountEntities() {
        int amountEntities = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            amountEntities += world.loadedEntityList.size();
        }
        return amountEntities;
    }

    public int killAllPerClass(int dim, Class clazz) {
        WorldServer world = DimensionManager.getWorld(dim);
        if (world == null) return -1;

        int killedEnts = 0;

        ArrayList copyList = new ArrayList(world.loadedEntityList);

        for (Entity entity : (ArrayList<Entity>) copyList) {
            if (clazz.isInstance(entity)) {
                entity.setDead();
                killedEnts += 1;
            }
        }
        return killedEnts;
    }

    public ArrayList<DataEntityPerClass> getTotalPerClass() {
        HashMap<String, DataEntityPerClass> data = new HashMap<String, DataEntityPerClass>();

        for (Entity entity : ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet()) {
            String name = this.getEntityName(entity, true);
            if (!data.containsKey(name)) data.put(name, new DataEntityPerClass(name));

            data.get(name).add(
                    ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.get(entity)
                            .getGeometricMean());
        }
        return new ArrayList<DataEntityPerClass>(data.values());
    }
}
