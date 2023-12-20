package mcp.mobius.opis.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.events.OpisClientTickHandler;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.widgets.JButtonAccess;

public class SwingUI extends JFrame implements WindowListener, ChangeListener, IMessageHandler {

    public static HashSet<JButtonAccess> registeredButtons = new HashSet<JButtonAccess>();

    private static SwingUI _instance = new SwingUI();

    public static SwingUI instance() {
        return _instance;
    }

    private JPanel contentPane;
    private JTabbedPane tabbedPane;

    public void showUI() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    SwingUI.instance().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    private SwingUI() {

        setTitle("Opis Control Panel");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 893, 455);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.addChangeListener(this);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        this.addWindowListener(this);
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(60 * 1000);
    }

    @Override
    public void windowActivated(WindowEvent arg0) {}

    @Override
    public void windowClosed(WindowEvent arg0) {
        modOpis.swingOpen = false;
        PacketManager.sendToServer(new PacketReqData(Message.COMMAND_UNREGISTER_SWING));
    }

    @Override
    public void windowClosing(WindowEvent arg0) {}

    @Override
    public void windowDeactivated(WindowEvent arg0) {}

    @Override
    public void windowDeiconified(WindowEvent arg0) {}

    @Override
    public void windowIconified(WindowEvent arg0) {}

    @Override
    public void windowOpened(WindowEvent arg0) {}

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case STATUS_ACCESS_LEVEL: {
                SwingUtilities.invokeLater(() -> {
                    AccessLevel level = AccessLevel.values()[((SerialInt) rawdata.value).value];
                    for (JButtonAccess button : SwingUI.registeredButtons) {
                        if (level.ordinal() < button.getAccessLevel().ordinal()) {
                            button.setEnabled(false);
                        } else {
                            button.setEnabled(true);
                        }
                    }
                });
                break;
            }
            case CLIENT_SHOW_SWING: {
                /*
                 * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
                 */

                modOpis.swingOpen = true;
                this.showUI();
                OpisClientTickHandler.INSTANCE
                        .scheduleOnClientThread(() -> { Minecraft.getMinecraft().displayGuiScreen(new GuiChat()); });
                PacketManager.sendToServer(
                        new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(SelectedTab.SUMMARY.ordinal())));
                break;
            }
            default:
                return false;
        }
        return true;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Component source = ((JTabbedPane) e.getSource()).getSelectedComponent();

        if (source instanceof ITabPanel) {
            ITabPanel panel = (ITabPanel) source;
            PacketManager.sendToServer(
                    new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }

        if (source instanceof JTabbedPane) {
            JTabbedPane pane = (JTabbedPane) source;
            ITabPanel panel = (ITabPanel) pane.getSelectedComponent();
            PacketManager.sendToServer(
                    new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }
    }
}
