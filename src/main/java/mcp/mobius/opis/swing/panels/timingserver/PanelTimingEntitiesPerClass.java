package mcp.mobius.opis.swing.panels.timingserver;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionRunOpis;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;
import net.miginfocom.swing.MigLayout;

public class PanelTimingEntitiesPerClass extends JPanelMsgHandler implements ITabPanel {
    private JButtonAccess btnRun;

    public PanelTimingEntitiesPerClass() {
        setLayout(new MigLayout("", "[grow][]", "[][grow]"));

        btnRun = new JButtonAccess("Run Opis", AccessLevel.PRIVILEGED);
        add(btnRun, "cell 1 0");
        btnRun.addActionListener(new ActionRunOpis());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 2 1,grow");

        table = new JTableStats(
                new String[] {"Name", "Amount", "Timing", "Mean value"},
                new Class[] {CachedString.class, Integer.class, DataTiming.class, DataTiming.class},
                new int[] {SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER});
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_TIMING_ENTITIES_PER_CLASS: {
                this.cacheData(msg, rawdata);

                this.getTable().setTableData(rawdata.array);

                DefaultTableModel model = table.getModel();
                int row = this.getTable().clearTable(DataEntityPerClass.class);

                for (Object o : rawdata.array) {
                    DataEntityPerClass data = (DataEntityPerClass) o;
                    model.addRow(new Object[] {
                        data.name, data.nents, data.update, new DataTiming(data.update.timing / data.nents)
                    });
                }

                this.getTable().dataUpdated(row);

                break;
            }

            case STATUS_START: {
                this.getBtnRun().setText("Running...");
                break;
            }
            case STATUS_STOP: {
                this.getBtnRun().setText("Run Opis");
                break;
            }
            case STATUS_RUNNING: {
                this.getBtnRun().setText("Running...");
                break;
            }
            default:
                return false;
        }
        return true;
    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.TIMINGENTITESPERCLASS;
    }

    public JButton getBtnRun() {
        return btnRun;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
