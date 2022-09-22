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

    public boolean cacheData(Message msg, PacketBase rawdata) {
        this.cachedmsg = msg;
        this.cachedrawdata = rawdata;
        return true;
    }

    public boolean refresh() {
        if (this.cachedmsg != null && this.cachedrawdata != null)
            return this.handleMessage(this.cachedmsg, this.cachedrawdata);
        else return false;
    }
}
