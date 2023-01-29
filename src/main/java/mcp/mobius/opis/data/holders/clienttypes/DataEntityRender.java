package mcp.mobius.opis.data.holders.clienttypes;

import java.util.WeakHashMap;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.profilers.ProfilerRenderEntity;

import net.minecraft.entity.Entity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataEntityRender implements Comparable<DataEntityRender> {

    public int eid;
    public long npoints;
    public String name;
    public CoordinatesBlock pos;
    public DataTiming update;

    public DataEntityRender fill(Entity entity) {

        this.eid = entity.getEntityId();
        this.name = EntityManager.INSTANCE.getEntityName(entity, false);
        this.pos = new CoordinatesBlock(entity);

        WeakHashMap<Entity, DescriptiveStatistics> data = ((ProfilerRenderEntity) (ProfilerSection.RENDER_ENTITY
                .getProfiler())).data;
        this.update = new DataTiming(data.containsKey(entity) ? data.get(entity).getGeometricMean() : 0.0D);
        this.npoints = data.containsKey(entity) ? data.get(entity).getN() : 0;

        return this;
    }

    @Override
    public int compareTo(DataEntityRender o) {
        return this.update.compareTo(o.update);
    }
}
