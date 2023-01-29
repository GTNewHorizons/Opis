package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.debug.PanelOrphanTileEntities;

public class ActionOrphanTileEntities implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelOrphanTileEntities panel = (PanelOrphanTileEntities) TabPanelRegistrar.INSTANCE
                .getTab(SelectedTab.ORPHANTES);

        if (e.getSource() == panel.getBtnRefresh()) {
            PacketManager.sendToServer(new PacketReqData(Message.LIST_ORPHAN_TILEENTS));
        }
    }
}
