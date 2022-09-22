package mcp.mobius.opis.swing.widgets;

import javax.swing.JPanel;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public abstract class JPanelMsgHandler extends JPanel implements IMessageHandler {

    public JTableStats table;
    public Message cachedmsg;
    public PacketBase cachedrawdata;

    public JTableStats getTable() {
        return this.table;
    }

    public synchronized boolean cacheData(Message msg, PacketBase rawdata) {
        this.cachedmsg = msg;
        this.cachedrawdata = rawdata;
        return true;
    }

    public boolean refresh() {
        // Atomically read both fields without holding the monitor lock for too long
        Message cachedmsg;
        PacketBase cachedrawdata;
        synchronized (this) {
            cachedmsg = this.cachedmsg;
            cachedrawdata = this.cachedrawdata;
        }
        if (cachedmsg != null && cachedrawdata != null) return this.handleMessage(this.cachedmsg, this.cachedrawdata);
        else return false;
    }
}
