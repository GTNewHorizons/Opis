package mcp.mobius.opis.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import java.awt.GridLayout;

import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import cpw.mods.fml.common.network.PacketDispatcher;
import mapwriter.Mw;
import mapwriter.api.MwAPI;
import mapwriter.gui.MwGui;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.data.client.DataCache;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialString;
import mcp.mobius.opis.data.holders.basetypes.TargetEntity;
import mcp.mobius.opis.data.holders.stats.StatAbstract;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.holders.stats.StatsEntity;
import mcp.mobius.opis.data.holders.stats.StatsPlayer;
import mcp.mobius.opis.data.holders.stats.StatsTileEntity;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.gui.overlay.OverlayMeanTime;
import mcp.mobius.opis.gui.overlay.entperchunk.OverlayEntityPerChunk;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.Packet_ReqData;
import net.minecraft.client.Minecraft;

import javax.swing.ListSelectionModel;
import javax.swing.JSeparator;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

public class SwingUI extends JFrame implements  ActionListener, ItemListener, WindowListener{

	public static HashSet<JButtonAccess> registeredButtons = new HashSet<JButtonAccess>();
	
	private static SwingUI _instance = new SwingUI();
	public  static SwingUI instance(){
		return _instance;
	}
	
	private JPanel contentPane;

	private JPanel panelSummary;
	private JLabel lblSummary_3;
	private JLabel lblSummary_4;
	private JLabel lblSummary_5;
	private JLabel lblSummary_6;
	private JLabel lblSummaryTimingTileEnts;
	private JLabel lblSummaryTimingHandlers;
	private JLabel lblSummaryTimingEntities;
	private JLabel lblSummaryTimingTotal;
	private JLabel lblSummary_7;
	private JLabel lblSummary_8;
	private JLabel lblSummary_9;
	private JLabel lblSummary_10;
	private JLabel lblSummaryAmountTileEnts;
	private JLabel lblSummaryAmountEntities;
	private JLabel lblSummaryAmountHandlers;
	private JLabel lblSummary_1;
	private JLabel label;
	private JButtonAccess btnSummaryRefresh;
	private JLabel lblSummary_11;
	private JLabel lblSummary_12;
	private JLabel lblSummaryTimingTick;
	private JLabel lblSummaryTickChart;
	private JLabel lblSummary_13;
	private JLabel lblSummaryTimingWorldTick;
	private JLabel lblSummary_14;
	private JLabel lblNewLabel;
	private JLabel lblSummary_15;
	private JLabel lblSummaryDownload;
	private JLabel lblSummaryUpload;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JProgressBar progBarSummaryOpis;
	private JLabel lblSummaryTimeStampLastRun;
	private JButton btnTimingTERemoveHighlight;
	private JLabel lblSummary_16;
	private JLabel lblSummary_17;
	private JLabel lblSummaryForcedChunks;
	private JLabel lblSummaryLoadedChunk;

	private PanelPlayers        panelPlayers;	
	private PanelAmountEntities panelAmountEntities;	
	private PanelTimingTileEnts panelTimingTileEnts;
	private PanelTimingEntities panelTimingEntities;
	private PanelTimingHandlers panelTimingHandlers;	
	private PanelTimingChunks   panelTimingChunks;
	
	public void showUI(){
		EventQueue.invokeLater(new Runnable() {
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
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 893, 455);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		panelSummary = new JPanel();
		tabbedPane.addTab("Summary", null, panelSummary, null);
		GridBagLayout gbl_panelSummary = new GridBagLayout();
		gbl_panelSummary.columnWidths = new int[]{0, 50, 20, 0, 70, 0, 20, 50, 50, 0, 20, 70, 0, 0, 42, 0, 0};
		gbl_panelSummary.rowHeights = new int[]{0, -4, 0, 0, 0, 0, 20, 10, 30, 0, 0, 0, 0, 0};
		gbl_panelSummary.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelSummary.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelSummary.setLayout(gbl_panelSummary);
		
		lblSummary_1 = new JLabel("Update time");
		GridBagConstraints gbc_lblSummary_1 = new GridBagConstraints();
		gbc_lblSummary_1.anchor = GridBagConstraints.EAST;
		gbc_lblSummary_1.gridwidth = 2;
		gbc_lblSummary_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_1.gridx = 4;
		gbc_lblSummary_1.gridy = 1;
		panelSummary.add(lblSummary_1, gbc_lblSummary_1);
		
		label = new JLabel("Amount");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.LINE_END;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 7;
		gbc_label.gridy = 1;
		panelSummary.add(label, gbc_label);
		
		lblNewLabel_6 = new JLabel("Amount");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 11;
		gbc_lblNewLabel_6.gridy = 1;
		panelSummary.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		btnSummaryRefresh = new JButtonAccess("Run Opis", AccessLevel.PRIVILEGED);
		GridBagConstraints gbc_btnSummaryReset = new GridBagConstraints();
		gbc_btnSummaryReset.anchor = GridBagConstraints.EAST;
		gbc_btnSummaryReset.insets = new Insets(0, 0, 5, 0);
		gbc_btnSummaryReset.gridx = 15;
		gbc_btnSummaryReset.gridy = 1;
		btnSummaryRefresh.addActionListener(this);
		panelSummary.add(btnSummaryRefresh, gbc_btnSummaryReset);
		
		lblSummary_13 = new JLabel("World Tick");
		lblSummary_13.setToolTipText("This is the world tick profiling.\nThe server will update the time, do random block ticks\nand this kind of things while inside this code part.");
		GridBagConstraints gbc_lblSummary_13 = new GridBagConstraints();
		gbc_lblSummary_13.anchor = GridBagConstraints.LINE_START;
		gbc_lblSummary_13.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_13.gridx = 1;
		gbc_lblSummary_13.gridy = 2;
		panelSummary.add(lblSummary_13, gbc_lblSummary_13);
		
		lblSummaryTimingWorldTick = new JLabel("0");
		GridBagConstraints gbc_lblSummaryTimingWorldTick = new GridBagConstraints();
		gbc_lblSummaryTimingWorldTick.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingWorldTick.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingWorldTick.gridx = 4;
		gbc_lblSummaryTimingWorldTick.gridy = 2; 
		panelSummary.add(lblSummaryTimingWorldTick, gbc_lblSummaryTimingWorldTick);
		
		lblSummary_14 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_14 = new GridBagConstraints();
		gbc_lblSummary_14.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_14.gridx = 5;
		gbc_lblSummary_14.gridy = 2;
		panelSummary.add(lblSummary_14, gbc_lblSummary_14);
		
		lblNewLabel = new JLabel("Upload");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 9;
		gbc_lblNewLabel.gridy = 2;
		panelSummary.add(lblNewLabel, gbc_lblNewLabel);
		
		lblSummaryUpload = new JLabel("0");
		GridBagConstraints gbc_lblSummaryUpload = new GridBagConstraints();
		gbc_lblSummaryUpload.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryUpload.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryUpload.gridx = 11;
		gbc_lblSummaryUpload.gridy = 2;
		panelSummary.add(lblSummaryUpload, gbc_lblSummaryUpload);
		
		lblNewLabel_4 = new JLabel("kB/s");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 12;
		gbc_lblNewLabel_4.gridy = 2;
		panelSummary.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		progBarSummaryOpis = new JProgressBar();
		GridBagConstraints gbc_progBarSummaryOpis = new GridBagConstraints();
		gbc_progBarSummaryOpis.fill = GridBagConstraints.HORIZONTAL;
		gbc_progBarSummaryOpis.insets = new Insets(0, 0, 5, 0);
		gbc_progBarSummaryOpis.gridx = 15;
		gbc_progBarSummaryOpis.gridy = 2;
		panelSummary.add(progBarSummaryOpis, gbc_progBarSummaryOpis);
		
		lblSummary_3 = new JLabel("Tile Entities");
		GridBagConstraints gbc_lblSummary_3 = new GridBagConstraints();
		gbc_lblSummary_3.anchor = GridBagConstraints.LINE_START;
		gbc_lblSummary_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_3.gridx = 1;
		gbc_lblSummary_3.gridy = 3;
		panelSummary.add(lblSummary_3, gbc_lblSummary_3);
		
		lblSummaryTimingTileEnts = new JLabel("0");
		lblSummaryTimingTileEnts.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblSummaryTimingTileEnts = new GridBagConstraints();
		gbc_lblSummaryTimingTileEnts.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingTileEnts.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingTileEnts.gridx = 4;
		gbc_lblSummaryTimingTileEnts.gridy = 3;
		panelSummary.add(lblSummaryTimingTileEnts, gbc_lblSummaryTimingTileEnts);
		
		lblSummary_9 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_9 = new GridBagConstraints();
		gbc_lblSummary_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_9.gridx = 5;
		gbc_lblSummary_9.gridy = 3;
		panelSummary.add(lblSummary_9, gbc_lblSummary_9);
		
		lblSummaryAmountTileEnts = new JLabel("0");
		GridBagConstraints gbc_lblSummaryAmountTileEnts = new GridBagConstraints();
		gbc_lblSummaryAmountTileEnts.anchor = GridBagConstraints.LINE_END;
		gbc_lblSummaryAmountTileEnts.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryAmountTileEnts.gridx = 7;
		gbc_lblSummaryAmountTileEnts.gridy = 3;
		panelSummary.add(lblSummaryAmountTileEnts, gbc_lblSummaryAmountTileEnts);
		
		lblSummary_15 = new JLabel("Download");
		GridBagConstraints gbc_lblSummary_15 = new GridBagConstraints();
		gbc_lblSummary_15.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_15.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_15.gridx = 9;
		gbc_lblSummary_15.gridy = 3;
		panelSummary.add(lblSummary_15, gbc_lblSummary_15);
		
		lblSummaryDownload = new JLabel("0");
		GridBagConstraints gbc_lblSummaryDownload = new GridBagConstraints();
		gbc_lblSummaryDownload.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryDownload.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryDownload.gridx = 11;
		gbc_lblSummaryDownload.gridy = 3;
		panelSummary.add(lblSummaryDownload, gbc_lblSummaryDownload);
		
		lblNewLabel_5 = new JLabel("kB/s");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 12;
		gbc_lblNewLabel_5.gridy = 3;
		panelSummary.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		lblSummaryTimeStampLastRun = new JLabel("Last run : Never");
		GridBagConstraints gbc_lblSummaryTimeStampLastRun = new GridBagConstraints();
		gbc_lblSummaryTimeStampLastRun.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimeStampLastRun.insets = new Insets(0, 0, 5, 0);
		gbc_lblSummaryTimeStampLastRun.gridx = 15;
		gbc_lblSummaryTimeStampLastRun.gridy = 3;
		panelSummary.add(lblSummaryTimeStampLastRun, gbc_lblSummaryTimeStampLastRun);
		
		lblSummary_4 = new JLabel("Entities");
		GridBagConstraints gbc_lblSummary_4 = new GridBagConstraints();
		gbc_lblSummary_4.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_4.gridx = 1;
		gbc_lblSummary_4.gridy = 4;
		panelSummary.add(lblSummary_4, gbc_lblSummary_4);
		
		lblSummaryTimingEntities = new JLabel("0");
		GridBagConstraints gbc_lblSummaryTimingEntities = new GridBagConstraints();
		gbc_lblSummaryTimingEntities.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingEntities.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingEntities.gridx = 4;
		gbc_lblSummaryTimingEntities.gridy = 4;
		panelSummary.add(lblSummaryTimingEntities, gbc_lblSummaryTimingEntities);
		
		lblSummary_7 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_7 = new GridBagConstraints();
		gbc_lblSummary_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_7.gridx = 5;
		gbc_lblSummary_7.gridy = 4;
		panelSummary.add(lblSummary_7, gbc_lblSummary_7);
		
		lblSummaryAmountEntities = new JLabel("0");
		GridBagConstraints gbc_lblSummaryAmountEntities = new GridBagConstraints();
		gbc_lblSummaryAmountEntities.anchor = GridBagConstraints.LINE_END;
		gbc_lblSummaryAmountEntities.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryAmountEntities.gridx = 7;
		gbc_lblSummaryAmountEntities.gridy = 4;
		panelSummary.add(lblSummaryAmountEntities, gbc_lblSummaryAmountEntities);
		
		lblSummary_5 = new JLabel("Handlers");
		lblSummary_5.setToolTipText("This is a generic profiling of server side tickhandlers.\nTickHandlers are a part of Forge and used by mods to do specific\nthings at the start and end of every tick.");
		GridBagConstraints gbc_lblSummary_5 = new GridBagConstraints();
		gbc_lblSummary_5.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_5.gridx = 1;
		gbc_lblSummary_5.gridy = 5;
		panelSummary.add(lblSummary_5, gbc_lblSummary_5);
		
		lblSummaryTimingHandlers = new JLabel("0");
		GridBagConstraints gbc_lblSummaryTimingHandlers = new GridBagConstraints();
		gbc_lblSummaryTimingHandlers.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingHandlers.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingHandlers.gridx = 4;
		gbc_lblSummaryTimingHandlers.gridy = 5;
		panelSummary.add(lblSummaryTimingHandlers, gbc_lblSummaryTimingHandlers);
		
		lblSummary_8 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_8 = new GridBagConstraints();
		gbc_lblSummary_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_8.gridx = 5;
		gbc_lblSummary_8.gridy = 5;
		panelSummary.add(lblSummary_8, gbc_lblSummary_8);
		
		lblSummaryAmountHandlers = new JLabel("0");
		GridBagConstraints gbc_lblSummaryAmountHandlers = new GridBagConstraints();
		gbc_lblSummaryAmountHandlers.anchor = GridBagConstraints.LINE_END;
		gbc_lblSummaryAmountHandlers.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryAmountHandlers.gridx = 7;
		gbc_lblSummaryAmountHandlers.gridy = 5;
		panelSummary.add(lblSummaryAmountHandlers, gbc_lblSummaryAmountHandlers);
		
		lblSummary_16 = new JLabel("Forced chunks");
		GridBagConstraints gbc_lblSummary_16 = new GridBagConstraints();
		gbc_lblSummary_16.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_16.gridx = 9;
		gbc_lblSummary_16.gridy = 5;
		panelSummary.add(lblSummary_16, gbc_lblSummary_16);
		
		lblSummaryForcedChunks = new JLabel("0");
		GridBagConstraints gbc_lblSummaryForcedChunks = new GridBagConstraints();
		gbc_lblSummaryForcedChunks.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryForcedChunks.gridwidth = 2;
		gbc_lblSummaryForcedChunks.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryForcedChunks.gridx = 11;
		gbc_lblSummaryForcedChunks.gridy = 5;
		panelSummary.add(lblSummaryForcedChunks, gbc_lblSummaryForcedChunks);
		
		lblSummary_17 = new JLabel("Loaded chunks");
		GridBagConstraints gbc_lblSummary_17 = new GridBagConstraints();
		gbc_lblSummary_17.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_17.gridx = 9;
		gbc_lblSummary_17.gridy = 6;
		panelSummary.add(lblSummary_17, gbc_lblSummary_17);
		
		lblSummaryLoadedChunk = new JLabel("0");
		GridBagConstraints gbc_lblSummaryLoadedChunk = new GridBagConstraints();
		gbc_lblSummaryLoadedChunk.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryLoadedChunk.gridwidth = 2;
		gbc_lblSummaryLoadedChunk.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryLoadedChunk.gridx = 11;
		gbc_lblSummaryLoadedChunk.gridy = 6;
		panelSummary.add(lblSummaryLoadedChunk, gbc_lblSummaryLoadedChunk);
		
		lblSummary_6 = new JLabel("Total");
		GridBagConstraints gbc_lblSummary_6 = new GridBagConstraints();
		gbc_lblSummary_6.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_6.gridx = 1;
		gbc_lblSummary_6.gridy = 7;
		panelSummary.add(lblSummary_6, gbc_lblSummary_6);
		
		lblSummaryTimingTotal = new JLabel("0");
		GridBagConstraints gbc_lblSummaryTimingTotal = new GridBagConstraints();
		gbc_lblSummaryTimingTotal.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingTotal.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingTotal.gridx = 4;
		gbc_lblSummaryTimingTotal.gridy = 7;
		panelSummary.add(lblSummaryTimingTotal, gbc_lblSummaryTimingTotal);
		
		lblSummary_10 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_10 = new GridBagConstraints();
		gbc_lblSummary_10.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_10.gridx = 5;
		gbc_lblSummary_10.gridy = 7;
		panelSummary.add(lblSummary_10, gbc_lblSummary_10);
		
		lblSummary_11 = new JLabel("Tick Time");
		GridBagConstraints gbc_lblSummary_11 = new GridBagConstraints();
		gbc_lblSummary_11.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_11.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_11.gridx = 1;
		gbc_lblSummary_11.gridy = 9;
		panelSummary.add(lblSummary_11, gbc_lblSummary_11);
		
		lblSummaryTimingTick = new JLabel("0");
		GridBagConstraints gbc_lblSummaryTimingTick = new GridBagConstraints();
		gbc_lblSummaryTimingTick.anchor = GridBagConstraints.EAST;
		gbc_lblSummaryTimingTick.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummaryTimingTick.gridx = 4;
		gbc_lblSummaryTimingTick.gridy = 9;
		panelSummary.add(lblSummaryTimingTick, gbc_lblSummaryTimingTick);
		
		lblSummary_12 = new JLabel("ms");
		GridBagConstraints gbc_lblSummary_12 = new GridBagConstraints();
		gbc_lblSummary_12.anchor = GridBagConstraints.WEST;
		gbc_lblSummary_12.insets = new Insets(0, 0, 5, 5);
		gbc_lblSummary_12.gridx = 5;
		gbc_lblSummary_12.gridy = 9;
		panelSummary.add(lblSummary_12, gbc_lblSummary_12);
		
		lblSummaryTickChart = new JLabel("");
		GridBagConstraints gbc_lblSummaryTickChart = new GridBagConstraints();
		gbc_lblSummaryTickChart.fill = GridBagConstraints.BOTH;
		gbc_lblSummaryTickChart.gridwidth = 16;
		gbc_lblSummaryTickChart.gridx = 0;
		gbc_lblSummaryTickChart.gridy = 12;
		panelSummary.add(lblSummaryTickChart, gbc_lblSummaryTickChart);
		

		panelPlayers = new PanelPlayers();
		tabbedPane.addTab("Players", null, panelPlayers, null);		

		panelAmountEntities = new PanelAmountEntities();
		tabbedPane.addTab("Entities amount", null, panelAmountEntities, null);		
		
		panelTimingTileEnts = new PanelTimingTileEnts();
		tabbedPane.addTab("TileEntities timing", null, panelTimingTileEnts, null);		
		
		panelTimingEntities = new PanelTimingEntities();
		tabbedPane.addTab("Entities timing", null, panelTimingEntities, null);		
		
		panelTimingHandlers = new PanelTimingHandlers();
		tabbedPane.addTab("Handlers timing", null, panelTimingHandlers, null);		
		
		panelTimingChunks = new PanelTimingChunks();
		tabbedPane.addTab("Chunks timing", null, panelTimingChunks, null);		
		
		this.addWindowListener(this);
	}

	public JLabel getLblSummaryTimingTileEnts() {
		return lblSummaryTimingTileEnts;
	}
	public JLabel getLblSummaryAmountTileEnts() {
		return lblSummaryAmountTileEnts;
	}
	public JLabel getLblSummaryTimingEntities() {
		return lblSummaryTimingEntities;
	}
	public JLabel getLblSummaryAmountEntities() {
		return lblSummaryAmountEntities;
	}
	public JLabel getLblSummaryTimingHandlers() {
		return lblSummaryTimingHandlers;
	}
	public JLabel getLblSummaryAmountHandlers() {
		return lblSummaryAmountHandlers;
	}
	public JLabel getLblSummaryTimingTotal() {
		return lblSummaryTimingTotal;
	}	
	public JLabel getLblSummaryTimingTick() {
		return lblSummaryTimingTick;
	}	
	public JLabel getLblSummaryTickChart() {
		return lblSummaryTickChart;
	}	
	public JLabel getLblSummaryTimingWorldTick() {
		return lblSummaryTimingWorldTick;
	}	
	public JLabel getLblSummaryUpload() {
		return lblSummaryUpload;
	}
	public JLabel getLblSummaryDownload() {
		return lblSummaryDownload;
	}	
	
	public JButton getBtnSummaryRefresh() {
		return btnSummaryRefresh;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {
		PacketDispatcher.sendPacketToServer(Packet_ReqData.create(Message.COMMAND_UNREGISTER_SWING));
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

	public JProgressBar getProgBarSummaryOpis() {
		return progBarSummaryOpis;
	}

	public JLabel getLblSummaryTimeStampLastRun() {
		return lblSummaryTimeStampLastRun;
	}
	public JButton getBtnTimingTERemoveHighlight() {
		return btnTimingTERemoveHighlight;
	}
	public JLabel getLblSummaryForcedChunks() {
		return lblSummaryForcedChunks;
	}
	public JLabel getLblSummaryLoadedChunks() {
		return lblSummaryLoadedChunk;
	}
	
	
	public PanelTimingChunks getPanelTimingChunks() {
		return panelTimingChunks;
	}
	public PanelTimingHandlers getPanelTimingHandlers() {
		return panelTimingHandlers;
	}
	public PanelTimingEntities getPanelTimingEntities() {
		return panelTimingEntities;
	}
	public PanelTimingTileEnts getPanelTimingTileEnts() {
		return panelTimingTileEnts;
	}
	public PanelAmountEntities getPanelAmountEntities() {
		return panelAmountEntities;
	}
	public PanelPlayers getPanelPlayers() {
		return panelPlayers;
	}
}
