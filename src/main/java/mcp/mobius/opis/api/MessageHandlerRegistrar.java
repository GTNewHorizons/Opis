package mcp.mobius.opis.api;

import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public enum MessageHandlerRegistrar {

    INSTANCE;

    private HashMap<Message, HashSet<IMessageHandler>> msgHandlers = new HashMap<Message, HashSet<IMessageHandler>>();
    public boolean suppressUnhandledMsgLogs = false;

    public void registerHandler(Message msg, IMessageHandler handler) {
        if (!msgHandlers.containsKey(msg)) msgHandlers.put(msg, new HashSet<IMessageHandler>());

        msgHandlers.get(msg).add(handler);
    }

    public void routeMessage(Message msg, PacketBase rawdata) {

        if (msgHandlers.containsKey(msg)) {
            for (IMessageHandler handler : msgHandlers.get(msg)) {
                if (!handler.handleMessage(msg, rawdata)) {
                    modOpis.log.warn(String.format("Unhandled msg %s in handler %s", msg, handler));
                }
            }
        } else if (!suppressUnhandledMsgLogs) {
            modOpis.log.warn(String.format("Unhandled msg : %s", msg));
        }
    }
}
