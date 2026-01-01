package mcp.mobius.opis.proxy;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public class ProxyServer implements IMessageHandler {

    public boolean isClient() {
        return false;
    }

    public void init() {}

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        return false;
    }
}
