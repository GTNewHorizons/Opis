package mcp.mobius.opis.swing.panels.network;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataAmountRate;
import mcp.mobius.opis.data.holders.newtypes.DataByteRate;
import mcp.mobius.opis.data.holders.newtypes.DataByteSize;
import mcp.mobius.opis.data.holders.newtypes.DataPacket250;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

import net.miginfocom.swing.MigLayout;

public class PanelOutbound250 extends JPanelMsgHandler implements ITabPanel {

    public PanelOutbound250() {
        setLayout(new MigLayout("", "[grow]", "[grow]"));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        table = new JTableStats(
                new String[] { "Channel", "Amount", "Rate", "Total Size" },
                new Class[] { CachedString.class, DataAmountRate.class, DataByteRate.class, DataByteSize.class },
                new int[] { SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT });
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_PACKETS_OUTBOUND_250: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(DataPacket250.class);

                    for (Object o : rawdata.array) {
                        DataPacket250 packet = (DataPacket250) o;
                        model.addRow(new Object[] { packet.channel, packet.amount, packet.rate, packet.size });
                    }

                    this.getTable().dataUpdated(row);
                });
                break;
            }

            default:
                return false;
        }
        return true;
    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.PACKETOUTBOUND250;
    }

    @Override
    public boolean refreshOnString() {
        return false;
    }
}
