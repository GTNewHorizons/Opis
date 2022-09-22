package mcp.mobius.opis.swing.panels.timingserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
import net.miginfocom.swing.MigLayout;

public class PanelTimingEvents extends JPanelMsgHandler implements ITabPanel {
    private JButtonAccess btnRun;

    public PanelTimingEvents() {
        setLayout(new MigLayout("", "[grow][]", "[][grow]"));

        btnRun = new JButtonAccess("Run Opis", AccessLevel.PRIVILEGED);
        add(btnRun, "cell 1 0");
        btnRun.addActionListener(new ActionRunOpis());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 2 1,grow");

        table = new JTableStats(
                new String[] {"Event", "Mod", "Class", "Handler", "Calls", "Timing"},
                new Class[] {
                    CachedString.class,
                    CachedString.class,
                    CachedString.class,
                    CachedString.class,
                    Long.class,
                    DataTiming.class
                },
                new int[] {
                    SwingConstants.LEFT,
                    SwingConstants.LEFT,
                    SwingConstants.LEFT,
                    SwingConstants.LEFT,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER
                });
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_TIMING_EVENTS: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = table.getModel();
                    int row = this.getTable().clearTable(DataEvent.class);

                    for (Object o : rawdata.array) {
                        DataEvent data = (DataEvent) o;
                        model.addRow(new Object[] {
                            data.event, data.mod, data.package_, data.handler, data.nCalls, data.update
                        });
                    }

                    this.getTable().dataUpdated(row);
                });
                break;
            }

            case STATUS_START:
            case STATUS_RUNNING: {
                SwingUtilities.invokeLater(() -> {
                    this.getBtnRun().setText("Running...");
                });
                break;
            }
            case STATUS_STOP: {
                SwingUtilities.invokeLater(() -> {
                    this.getBtnRun().setText("Run Opis");
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
        return SelectedTab.TIMINGEVENTS;
    }

    public JButton getBtnRun() {
        return btnRun;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
