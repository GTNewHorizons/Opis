package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.newtypes.DataDimension;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.tracking.PanelDimensions;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class ActionDimensions implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelDimensions panel = (PanelDimensions) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.DIMENSIONS);

        if (e.getSource() == panel.getBtnPurgeAll()) {
            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to kill all hostiles in game ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(new PacketReqData(Message.COMMAND_KILL_HOSTILES_ALL));
            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to remove all the dropped items ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(new PacketReqData(Message.COMMAND_KILL_STACKS_ALL));
            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to purge chunks ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(new PacketReqData(Message.COMMAND_PURGE_CHUNKS_ALL));

        } else {
            JTableStats table = panel.getTable();
            if (table == null || table.getSelectedRow() == -1) return;
            int indexData = table.convertRowIndexToModel(table.getSelectedRow());
            DataDimension data = (DataDimension) table.getTableData().get(indexData);

            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to kill all hostiles ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(
                        new PacketReqData(Message.COMMAND_KILL_HOSTILES_DIM, new SerialInt(data.dim)));
            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to remove all the dropped items ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(new PacketReqData(Message.COMMAND_KILL_STACKS_DIM, new SerialInt(data.dim)));
            if (JOptionPane.showConfirmDialog(
                            null, "Do you want to purge chunks ?", "Warning", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                PacketManager.sendToServer(
                        new PacketReqData(Message.COMMAND_PURGE_CHUNKS_DIM, new SerialInt(data.dim)));
        }
    }
}
