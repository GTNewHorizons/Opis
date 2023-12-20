package mcp.mobius.opis.swing.panels.timingclient;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.clienttypes.DataRenderEvent;
import mcp.mobius.opis.data.holders.newtypes.CachedString;
import mcp.mobius.opis.data.holders.newtypes.DataEvent;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionRunOpisClient;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class PanelEventClient extends JPanel implements ITabPanel, IMessageHandler {

    private JTableStats table;
    private JButton btnRunRender;

    /**
     * Create the panel.
     */
    public PanelEventClient() {
        setLayout(new MigLayout("", "[grow][grow][]", "[][grow][]"));

        btnRunRender = new JButton("Run Render");
        add(btnRunRender, "cell 2 0");
        btnRunRender.addActionListener(new ActionRunOpisClient());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 3 1,grow");

        table = new JTableStats(
                new String[] { "Event", "Mod", "Class", "Handler", "Calls", "Timing" },
                new Class[] { CachedString.class, CachedString.class, CachedString.class, CachedString.class,
                        Long.class, DataTiming.class },
                new int[] { SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.LEFT,
                        SwingConstants.CENTER, SwingConstants.CENTER });

        scrollPane.setViewportView(table);
    }

    public void setTable(ArrayList<DataRenderEvent> data) {

        DefaultTableModel model = table.getModel();
        int row = this.updateData(table, model, DataEvent.class);

        for (DataRenderEvent o : data) {
            model.addRow(new Object[] { o.event, o.mod, o.package_, o.handler, o.nCalls, o.update });
        }

        this.dataUpdated(table, model, row);
    }

    public JTableStats getTable() {
        return table;
    }

    public <U> int updateData(JTable table, DefaultTableModel model, Class<U> datatype) {
        int row = table.getSelectedRow();

        if (model.getRowCount() > 0) for (int i = model.getRowCount() - 1; i >= 0; i--) model.removeRow(i);

        return row;
    }

    public void dataUpdated(JTable table, DefaultTableModel model, int row) {
        model.fireTableDataChanged();

        try {
            table.setRowSelectionInterval(row, row);
        } catch (IllegalArgumentException e) {

        }
    }

    public JButton getBtnRunRender() {
        return btnRunRender;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.CLIENTEVENTS;
    }

    @Override
    public boolean refreshOnString() {
        return false;
    }

    @Override
    public boolean refresh() {
        return false;
    }
}
