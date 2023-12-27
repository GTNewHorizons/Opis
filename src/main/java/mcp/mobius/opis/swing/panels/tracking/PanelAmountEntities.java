package mcp.mobius.opis.swing.panels.tracking;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionAmountEntities;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class PanelAmountEntities extends JPanelMsgHandler implements ITabPanel {

    private static final long serialVersionUID = 3176339193502108176L;
    private JCheckBox chckbxFilter;
    private JButtonAccess btnKillAll;
    private JButtonAccess btnRefresh;
    private JLabel lblSummary;

    /**
     * Create the panel.
     */
    public PanelAmountEntities() {
        setLayout(new MigLayout("", "[][][grow][]", "[][grow][]"));

        chckbxFilter = new JCheckBox("Filter Entities");
        add(chckbxFilter, "cell 0 0");
        chckbxFilter.addItemListener(new ActionAmountEntities());

        btnKillAll = new JButtonAccess("Kill All", AccessLevel.PRIVILEGED);
        add(btnKillAll, "cell 1 0");
        btnKillAll.addActionListener(new ActionAmountEntities());

        btnRefresh = new JButtonAccess("Refresh", AccessLevel.NONE);
        add(btnRefresh, "cell 3 0");
        btnRefresh.addActionListener(new ActionAmountEntities());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 4 1,grow");

        table = new JTableStats(new String[] { "Type", "Amount" }, new Class[] { String.class, Integer.class });
        scrollPane.setViewportView(table);

        lblSummary = new JLabel("New label");
        add(lblSummary, "cell 0 2 4 1,alignx center");
    }

    public JCheckBox getChckbxFilter() {
        return chckbxFilter;
    }

    public JButton getBtnKillAll() {
        return btnKillAll;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JLabel getLblSummary() {
        return lblSummary;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_AMOUNT_ENTITIES: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(AmountHolder.class);
                    int totalEntities = 0;

                    for (Object o : rawdata.array) {
                        AmountHolder entity = (AmountHolder) o;
                        model.addRow(new Object[] { entity.key, entity.value });
                        totalEntities += entity.value;
                    }

                    this.getLblSummary().setText("Total : " + String.valueOf(totalEntities));
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
        return SelectedTab.AMOUNTENTS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
