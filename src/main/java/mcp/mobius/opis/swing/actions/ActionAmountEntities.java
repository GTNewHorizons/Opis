package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.SerialString;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.tracking.PanelAmountEntities;
import mcp.mobius.opis.swing.widgets.JTableStats;

public class ActionAmountEntities implements ActionListener, ItemListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelAmountEntities panel = (PanelAmountEntities) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.AMOUNTENTS);

        JTableStats table = panel.getTable();
        if (table == null || table.getSelectedRow() == -1) return;
        int indexData = table.convertRowIndexToModel(table.getSelectedRow());
        AmountHolder data = (AmountHolder) table.getTableData().get(indexData);

        if (e.getSource() == panel.getBtnKillAll()) {
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_KILLALL, new SerialString(data.key.str)));
            PacketManager.sendToServer(new PacketReqData(Message.LIST_AMOUNT_ENTITIES));
        }

        if (e.getSource() == panel.getBtnRefresh()) {
            PacketManager.sendToServer(new PacketReqData(Message.LIST_AMOUNT_ENTITIES));
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_FILTERING_TRUE));
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_FILTERING_FALSE));
        }
        PacketManager.sendToServer(new PacketReqData(Message.LIST_AMOUNT_ENTITIES));
    }
}
