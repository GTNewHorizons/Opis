package mcp.mobius.opis.swing.panels.debug;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataTileEntity;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionOrphanTileEntities;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;
import net.miginfocom.swing.MigLayout;

public class PanelOrphanTileEntities extends JPanelMsgHandler implements ITabPanel {

    private JButtonAccess btnRefresh;

    public PanelOrphanTileEntities() {
        setLayout(new MigLayout("", "[grow][]", "[][grow]"));

        btnRefresh = new JButtonAccess("Refresh", AccessLevel.PRIVILEGED);
        add(btnRefresh, "cell 1 0");
        btnRefresh.addActionListener(new ActionOrphanTileEntities());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 2 1,grow");

        table = new JTableStats(
                new String[] {"Class", "Hash", "Type", "Dimension", "Coordinates"},
                new Class[] {CachedString.class, String.class, CachedString.class, Integer.class, String.class},
                new int[] {
                    SwingConstants.LEFT,
                    SwingConstants.LEFT,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER
                });
        scrollPane.setViewportView(table);
    }

    int row;

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_ORPHAN_TILEENTS: {
                this.cacheData(msg, rawdata);

                SwingUtilities.invokeLater(() -> {
                    this.getTable().addTableData(rawdata.array);
                    DefaultTableModel model = this.getTable().getModel();
                    // int               row   = this.updateData(table, model, DataTileEntity.class);

                    for (Object o : rawdata.array) {
                        DataTileEntity data = (DataTileEntity) o;
                        model.addRow(new Object[] {
                            data.clazz,
                            String.format("0x%x", data.hashCode),
                            data.cause,
                            data.pos.dim,
                            String.format("[ %4d %4d %4d ]", data.pos.x, data.pos.y, data.pos.z),
                        });
                    }

                    this.getTable().dataUpdated(row);
                });

                break;
            }

            case LIST_ORPHAN_TILEENTS_CLEAR: {
                SwingUtilities.invokeLater(() -> {
                    this.getTable().clearTableData();
                    DefaultTableModel model = this.getTable().getModel();
                    row = this.getTable().clearTable(DataTileEntity.class);
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
        return SelectedTab.ORPHANTES;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
