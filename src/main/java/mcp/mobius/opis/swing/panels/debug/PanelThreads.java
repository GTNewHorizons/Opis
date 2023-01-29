package mcp.mobius.opis.swing.panels.debug;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataThread;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

import net.miginfocom.swing.MigLayout;

public class PanelThreads extends JPanelMsgHandler implements ITabPanel {

    public PanelThreads() {
        setLayout(new MigLayout("", "[grow]", "[grow]"));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        table = new JTableStats(
                new String[] { "Name", "Class" },
                new Class[] { CachedString.class, CachedString.class },
                new int[] { SwingConstants.LEFT, SwingConstants.LEFT });
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_THREADS: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(DataThread.class);

                    for (Object o : rawdata.array) {
                        DataThread data = (DataThread) o;
                        model.addRow(new Object[] { data.name, data.clzz });
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
        return SelectedTab.THREADS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
