package mcp.mobius.opis.data.client;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

import net.minecraft.item.ItemStack;
import mcp.mobius.opis.data.holders.EntityStats;
import mcp.mobius.opis.data.holders.TickHandlerStats;
import mcp.mobius.opis.data.holders.TileEntityStats;
import mcp.mobius.opis.gui.swing.SwingUI;
import mcp.mobius.opis.tools.ModIdentification;

public class DataCache {

	private static DataCache _instance = new DataCache();
	public  static DataCache instance() { return _instance; };
	
	private HashMap<String, Integer>    amountEntities = new HashMap<String, Integer>();
	private ArrayList<TickHandlerStats> timingHandlers = new ArrayList<TickHandlerStats>(); 
	private ArrayList<EntityStats>      timingEntities = new ArrayList<EntityStats>(); 
	private ArrayList<TileEntityStats>  timingTileEnts = new ArrayList<TileEntityStats>(); 
	
	public HashMap<String, Integer> getAmountEntities(){
		return this.amountEntities;
	}
	
	public ArrayList<TickHandlerStats> getTimingHandlers(){
		return this.timingHandlers;
	}
	
	public ArrayList<EntityStats> getTimingEntities(){
		return this.timingEntities;
	}	

	public ArrayList<TileEntityStats> getTimingTileEnts(){
		return this.timingTileEnts;
	}		
	
	public void setAmountEntities(HashMap<String, Integer> stats){
		this.amountEntities = stats;

		DefaultTableModel model = (DefaultTableModel)SwingUI.instance().getTableEntityList().getModel();
		this.clearRows(model);
		
		int totalEntities = 0;
		
		for (String type : DataCache.instance().getAmountEntities().keySet()){
			model.addRow(new Object[] {type, DataCache.instance().getAmountEntities().get(type)});
			totalEntities += DataCache.instance().getAmountEntities().get(type);
		}

		SwingUI.instance().getLabelAmountValue().setText(String.valueOf(totalEntities));
		model.fireTableDataChanged();		
	}
	
	public void setTimingHandler(ArrayList<TickHandlerStats> timingHandlers){
		this.timingHandlers = timingHandlers;
		
		DefaultTableModel model = (DefaultTableModel)SwingUI.instance().getTableTimingHandler().getModel();
		this.clearRows(model);
		
		for (TickHandlerStats stat : DataCache.instance().getTimingHandlers()){
			model.addRow(new Object[] {stat.getName(), String.format("%.3f \u00B5s", stat.getGeometricMean())});
		}

		model.fireTableDataChanged();		
	}
	
	public void setTimingEntities(ArrayList<EntityStats> timingEntities){
		this.timingEntities = timingEntities;
		
		DefaultTableModel model = (DefaultTableModel)SwingUI.instance().getTableTimingEnt().getModel();
		this.clearRows(model);
		
		for (EntityStats stat : DataCache.instance().getTimingEntities()){
			model.addRow(new Object[]  {stat.getName(), 
										stat.getID(),
										stat.getCoord().dim,
										String.format("[ %4d %4d %4d ]", 	stat.getCoord().x, stat.getCoord().y, stat.getCoord().z), 
										String.format("%.3f \u00B5s", stat.getGeometricMean()),
										String.valueOf(stat.getNData())});
		}
		
		for (int i = model.getRowCount() - 1; i >= 0; i--)
			model.removeRow(i);
		
		model.fireTableDataChanged();			
		
	}
	
	public void setTimingTileEnts(ArrayList<TileEntityStats> timingTileEnts){
		this.timingTileEnts = timingTileEnts;
		
		DefaultTableModel model = (DefaultTableModel)SwingUI.instance().getTableTimingTE().getModel();
		this.clearRows(model);
		
		for (TileEntityStats stat : DataCache.instance().getTimingTileEnts()){
			ItemStack is;
			String name  = String.format("te.%d.%d", stat.getID(), stat.getMeta());
			String modID = "<UNKNOWN>";
			
			try{
				is = new ItemStack(stat.getID(), 1, stat.getMeta());
				name  = is.getDisplayName();
				modID = ModIdentification.idFromStack(is);
			}  catch (Exception e) {	}			
			
			model.addRow(new Object[]  {
					 name,
					 modID,
				     stat.getCoordinates().dim,
				     String.format("[ %4d %4d %4d ]", 	stat.getCoordinates().x, stat.getCoordinates().y, stat.getCoordinates().z),  
				     String.format("%.3f \u00B5s",stat.getGeometricMean())});
		}
		
		model.fireTableDataChanged();				
	}
	
	private void clearRows(DefaultTableModel model){
		if (model.getRowCount() > 0)
			for (int i = model.getRowCount() - 1; i >= 0; i--)
				model.removeRow(i);		
	}
}
