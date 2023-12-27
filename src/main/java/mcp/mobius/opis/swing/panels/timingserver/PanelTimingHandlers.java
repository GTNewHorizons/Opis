package mcp.mobius.opis.swing.panels.timingserver;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataEvent;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionRunOpis;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class PanelTimingHandlers extends JPanelMsgHandler implements ITabPanel {

    private static final long serialVersionUID = -5484173224998075653L;
    private JButtonAccess btnRun;
    private JLabel lblSummary;

    /**
     * Create the panel.
     */
    public PanelTimingHandlers() {
        setLayout(new MigLayout("", "[grow][]", "[][grow][]"));

        btnRun = new JButtonAccess("Run Opis", AccessLevel.PRIVILEGED);
        add(btnRun, "cell 1 0");
        btnRun.addActionListener(new ActionRunOpis());

        JScrollPane scrollPane = new JScrollPane();

        add(scrollPane, "cell 0 1 2 1,grow");

        table = new JTableStats(
                new String[] { "Mod", "Tick", "Update Time" },
                new Class[] { CachedString.class, String.class, DataTiming.class });
        scrollPane.setViewportView(table);

        lblSummary = new JLabel("TmpText");
        add(lblSummary, "cell 0 2 2 1,alignx center");
    }

    public JButton getBtnRun() {
        return btnRun;
    }

    public JLabel getLblSummary() {
        return lblSummary;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_TIMING_HANDLERS: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = table.getModel();
                    int row = this.getTable().clearTable(DataEvent.class);

                    for (Object o : rawdata.array) {
                        DataEvent data = (DataEvent) o;
                        try {
                            model.addRow(new Object[] { data.mod, data.event.toString().split("\\$")[1], data.update });
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // System.out.printf("AIOOB : %s %s\n", data.event.index, data.event.toString());
                            model.addRow(new Object[] { data.mod, data.event.toString(), data.update });
                        }
                    }

                    this.getTable().dataUpdated(row);
                });
                break;
            }
            case VALUE_TIMING_HANDLERS: {
                SwingUtilities.invokeLater(
                        () -> {
                            this.getLblSummary()
                                    .setText(String.format("Total update time : %s", (rawdata.value).toString()));
                        });
                break;
            }
            case STATUS_START:
            case STATUS_RUNNING: {
                SwingUtilities.invokeLater(() -> { this.getBtnRun().setText("Running..."); });
                break;
            }
            case STATUS_STOP: {
                SwingUtilities.invokeLater(() -> { this.getBtnRun().setText("Run Opis"); });
                break;
            }
            default:
                return false;
        }
        return true;
    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.TIMINGHANDLERS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
