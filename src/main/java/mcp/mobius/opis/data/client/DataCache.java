package mcp.mobius.opis.data.client;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;

public class DataCache implements IMessageHandler {

    private static DataCache _instance = new DataCache();

    public static DataCache instance() {
        return _instance;
    }
    ;

    private long clockScrew = 0;
    private AccessLevel clientAccess = AccessLevel.NONE;

    public AccessLevel getAccessLevel() {
        return this.clientAccess;
    }

    public long getClockScrew() {
        return this.clockScrew;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case STATUS_ACCESS_LEVEL: {
                this.clientAccess = AccessLevel.values()[((SerialInt) rawdata.value).value];
                break;
            }

            case STATUS_CURRENT_TIME: {
                this.clockScrew = System.currentTimeMillis() - ((SerialLong) rawdata.value).value;
                System.out.printf("Adjusting clock screw. Server differential is %d ms.\n", clockScrew);
                break;
            }

            default:
                return false;
        }
        return true;
    }
}
