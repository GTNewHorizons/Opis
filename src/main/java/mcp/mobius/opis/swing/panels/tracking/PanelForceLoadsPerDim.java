package mcp.mobius.opis.swing.panels.tracking;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.DataForcedChunks;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class PanelForceLoadsPerDim extends JPanelMsgHandler implements ITabPanel {

    // todo serialVersionUID

    public PanelForceLoadsPerDim() {
        setLayout(new MigLayout("", "[grow]", "[grow]"));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        table = new JTableStats(
                new String[] { "Dim", "Name", "Mod Id", "Player or Entity", "Position", "Type", "Chunks", "Raw Data" },
                new Class[] { Integer.class, String.class, String.class, String.class, String.class, String.class,
                        String.class, String.class },
                new boolean[] { false, false, false, false, false, false, false, false });
        table.setBackground(this.getBackground());
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_FORCE_CHUNK_DATA: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = this.getTable().getModel();
                    int row = this.getTable().clearTable(DataForcedChunks.class);

                    for (Object o : rawdata.array) {
                        DataForcedChunks data = (DataForcedChunks) o;
                        model.addRow(
                                new Object[] { data.dim, data.dimName, data.modId, data.playerOrEntityName,
                                        data.position, data.type, data.chunks, data.rawData });
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
        return SelectedTab.FORCELOADS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
