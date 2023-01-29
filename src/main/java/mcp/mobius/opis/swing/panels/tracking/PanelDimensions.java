package mcp.mobius.opis.swing.panels.tracking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.DataDimension;
import mcp.mobius.opis.data.holders.newtypes.DataTimingMillisecond;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionDimensions;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableButton;
import mcp.mobius.opis.swing.widgets.JTableStats;

import net.miginfocom.swing.MigLayout;

public class PanelDimensions extends JPanelMsgHandler implements IMessageHandler, ITabPanel {

    private JButtonAccess btnPurgeAll;

    public PanelDimensions() {
        setLayout(new MigLayout("", "[grow]", "[][grow]"));

        btnPurgeAll = new JButtonAccess("Purge All", AccessLevel.PRIVILEGED);
        add(btnPurgeAll, "cell 0 0");
        btnPurgeAll.addActionListener(new ActionDimensions());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1,grow");

        table = new JTableStats(
                new String[] { "Dim", "Name", "Players", "Forced chunks", "Loaded chunks", "Monsters", "Animals",
                        "Stacks", "Update time", "Purge" },
                new Class[] { Integer.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class,
                        Integer.class, Integer.class, DataTimingMillisecond.class, JTableButton.class },
                new boolean[] { false, false, false, false, false, false, false, false, false, true });
        table.setBackground(this.getBackground());
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        scrollPane.setViewportView(table);

        JTableButton buttonColumn = new JTableButton(table, new ActionDimensions(), 9, AccessLevel.PRIVILEGED);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_DIMENSION_DATA: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(DataDimension.class);

                    for (Object o : rawdata.array) {
                        DataDimension data = (DataDimension) o;
                        model.addRow(
                                new Object[] { data.dim, data.name, data.players, data.forced, data.loaded, data.mobs,
                                        data.neutral, data.itemstacks, data.update.asMillisecond(), "Purge" });
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

    public JButton getBtnPurgeAll() {
        return btnPurgeAll;
    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.DIMENSIONS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
