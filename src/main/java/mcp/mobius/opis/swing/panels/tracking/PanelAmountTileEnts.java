package mcp.mobius.opis.swing.panels.tracking;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionAmountTileEnts;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class PanelAmountTileEnts extends JPanelMsgHandler implements ITabPanel {

    private JButtonAccess btnRefresh;

    /**
     * Create the panel.
     */
    public PanelAmountTileEnts() {
        setLayout(new MigLayout("", "[][][grow][]", "[][grow][]"));

        btnRefresh = new JButtonAccess("Refresh", AccessLevel.NONE);
        add(btnRefresh, "cell 3 0");
        btnRefresh.addActionListener(new ActionAmountTileEnts());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 4 1,grow");

        table = new JTableStats(
                new String[] { "Name", "Mod", "Amount" },
                new Class[] { String.class, String.class, Integer.class },
                new int[] { SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.CENTER });
        scrollPane.setViewportView(table);
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_AMOUNT_TILEENTS: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(AmountHolder.class);

                    for (Object o : rawdata.array) {
                        AmountHolder entity = (AmountHolder) o;
                        model.addRow(new Object[] { entity.key, entity.data, entity.value });
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
        return SelectedTab.AMOUNTTES;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
