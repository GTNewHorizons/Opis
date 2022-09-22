package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.timingserver.PanelTimingChunks;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class ActionTimingChunks implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelTimingChunks panel = (PanelTimingChunks) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.TIMINGCHUNKS);

        JTableStats table = panel.getTable();
        if (table == null || table.getSelectedRow() == -1) return;
        int indexData = table.convertRowIndexToModel(table.getSelectedRow());
        StatsChunk data = (StatsChunk) table.getTableData().get(indexData);

        if (e.getSource() == panel.getBtnTeleport()) {
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_CHUNK, data.getChunk()));
        }
    }
}
