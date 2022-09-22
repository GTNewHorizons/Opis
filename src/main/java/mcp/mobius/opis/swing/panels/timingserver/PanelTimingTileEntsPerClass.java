package mcp.mobius.opis.swing.panels.timingserver;

import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionRunOpis;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;
import net.miginfocom.swing.MigLayout;

public class PanelTimingTileEntsPerClass extends JPanelMsgHandler implements ITabPanel {
    private JButtonAccess btnRun;

    public PanelTimingTileEntsPerClass() {
        setLayout(new MigLayout("", "[grow][]", "[][grow]"));

        btnRun = new JButtonAccess("Run Opis", AccessLevel.PRIVILEGED);
        add(btnRun, "cell 1 0");
        btnRun.addActionListener(new ActionRunOpis());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 2 1,grow");

        table = new JTableStats(
                new String[] {"Name", "Mod", "Amount", "Timing", "Mean value"},
                new Class[] {String.class, String.class, Integer.class, DataTiming.class, DataTiming.class},
                new int[] {
                    SwingConstants.LEFT,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER,
                    SwingConstants.CENTER
                });
        scrollPane.setViewportView(table);
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_TIMING_TILEENTS_PER_CLASS: {
                this.cacheData(msg, rawdata);
                SwingUtilities.invokeLater(() -> {
                    this.getTable().setTableData(rawdata.array);

                    DefaultTableModel model = table.getModel();
                    int row = this.getTable().clearTable(DataBlockTileEntityPerClass.class);

                    HashMap<String, DataBlockTileEntityPerClass> cumData =
                            new HashMap<String, DataBlockTileEntityPerClass>();

                    for (Object o : rawdata.array) {
                        DataBlockTileEntityPerClass data = (DataBlockTileEntityPerClass) o;
                        String name = ModIdentification.getStackName(data.id, data.meta);

                        if (!cumData.containsKey(name))
                            cumData.put(name, new DataBlockTileEntityPerClass(data.id, data.meta));

                        cumData.get(name).add(data.amount, data.update.timing);
                    }

                    for (String s : cumData.keySet()) {
                        String name = s;
                        String modID = ModIdentification.getModStackName(cumData.get(s).id, cumData.get(s).meta);
                        model.addRow(new Object[] {
                            name,
                            modID,
                            cumData.get(s).amount,
                            cumData.get(s).update,
                            new DataTiming(cumData.get(s).update.timing / cumData.get(s).amount)
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
        return SelectedTab.TIMINGTILEENTSPERCLASS;
    }

    public JButton getBtnRun() {
        return btnRun;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
