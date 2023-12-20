package mcp.mobius.opis.data.holders.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public abstract class StatAbstract implements Comparable<StatAbstract>, ISerializable {

    protected DescriptiveStatistics dstat = new DescriptiveStatistics();
    public Long dataPoints = 0L;
    protected Double geomMean = null;
    protected Double dataSum = null;
    protected CoordinatesBlock coord = CoordinatesBlock.INVALID;
    protected CoordinatesChunk chunk = CoordinatesChunk.INVALID;
    protected String name;

    /*
     * public StatAbstract(){ dstat = new DescriptiveStatistics(); } public StatAbstract(int points){ dstat = new
     * DescriptiveStatistics(points); }
     */

    public void addMeasure(long timing) {
        // this.dstat.addValue((double)timing/1000.0);
        this.dstat.addValue((double) timing / 1000.);
        dataPoints += 1;
    }

    public void addMeasure(double timing) {
        this.dstat.addValue(timing);
        dataPoints += 1;
    }

    public double getGeometricMean() {
        if (geomMean != null) return geomMean;
        else return dstat.getGeometricMean();
    }

    public void setGeometricMean(double value) {
        this.geomMean = value;
    }

    public double getDataSum() {
        if (dataSum != null) return dataSum;
        else return dstat.getSum();
    }

    public void setDataSum(double value) {
        this.dataSum = value;
    }

    public long getDataPoints() {
        return this.dataPoints;
    }

    public void setDataPoints(long ndata) {
        this.dataPoints = ndata;
    }

    @Override
    public int compareTo(StatAbstract o) {
        double value = o.getGeometricMean() - this.getGeometricMean();
        if (value > 0) return 1;
        if (value < 0) return -1;
        return 0;
    }

    public String getName() {
        return this.name;
    };

    public CoordinatesBlock getCoordinates() {
        return this.coord;
    }

    public CoordinatesChunk getChunk() {
        return this.chunk;
    }

    public CoordinatesBlock getTeleportTarget() {
        return this.coord;
    }

    public abstract void writeToStream(ByteArrayDataOutput stream);
}
