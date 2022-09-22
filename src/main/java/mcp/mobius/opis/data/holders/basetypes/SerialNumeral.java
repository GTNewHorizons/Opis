package mcp.mobius.opis.data.holders.basetypes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerialNumeral<U> {
    private enum Type {
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        STRING;
    }

    public static final SerialNumeral<Integer> INTEGER = new SerialNumeral<Integer>(Type.INTEGER);
    public static final SerialNumeral<Float> FLOAT = new SerialNumeral<Float>(Type.FLOAT);
    public static final SerialNumeral<Long> LONG = new SerialNumeral<Long>(Type.LONG);
    public static final SerialNumeral<Double> DOUBLE = new SerialNumeral<Double>(Type.DOUBLE);

    U value;
    Type type;

    public SerialNumeral(Type type) {
        this.type = type;
    }

    public void setValue(U value) {
        this.value = value;
    }

    public U getValue() {
        return this.value;
    }

    public void writeToSteam(DataOutputStream stream) throws IOException {
        stream.writeByte(this.type.ordinal());

        switch (type) {
            case DOUBLE:
                stream.writeDouble((Double) this.value);
                break;
            case FLOAT:
                stream.writeFloat((Float) this.value);
                break;
            case INTEGER:
                stream.writeInt((Integer) this.value);
                break;
            case LONG:
                stream.writeLong((Long) this.value);
                break;
            default:
                break;
        }
    }

    public static SerialNumeral readFromStream(DataInputStream stream) throws IOException {
        Type datatype = Type.values()[stream.readByte()];
        switch (datatype) {
            case DOUBLE: {
                SerialNumeral<Double> retVal = new SerialNumeral<Double>(Type.DOUBLE);
                retVal.setValue(stream.readDouble());
                return retVal;
            }
            case FLOAT: {
                SerialNumeral<Float> retVal = new SerialNumeral<Float>(Type.FLOAT);
                retVal.setValue(stream.readFloat());
                return retVal;
            }
            case INTEGER: {
                SerialNumeral<Integer> retVal = new SerialNumeral<Integer>(Type.INTEGER);
                retVal.setValue(stream.readInt());
                return retVal;
            }
            case LONG: {
                SerialNumeral<Long> retVal = new SerialNumeral<Long>(Type.LONG);
                retVal.setValue(stream.readLong());
                return retVal;
            }
            default:
                break;
        }
        return null;
    }
}
