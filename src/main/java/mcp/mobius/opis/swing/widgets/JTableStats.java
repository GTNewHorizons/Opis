package mcp.mobius.opis.swing.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import mcp.mobius.opis.data.holders.ISerializable;

public class JTableStats extends JTable {

    public class OpisTableModel extends DefaultTableModel {

        String[] headers;
        Class[] columnTypes;
        boolean[] columnEditables;

        public OpisTableModel(String[] headers, Class[] columnTypes, boolean[] columnEditables) {
            super(new Object[][] {}, headers);
            this.headers = headers;
            this.columnTypes = columnTypes;
            this.columnEditables = columnEditables;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
        }
    }

    protected ArrayList<ISerializable> statistics;
    protected int[] alignList;

    public JTableStats(String[] headers, Class[] columnTypes) {
        this(
                headers,
                columnTypes,
                new int[] { SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
                        SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
                        SwingConstants.CENTER, SwingConstants.CENTER },
                new boolean[] { false, false, false, false, false, false, false, false, false, false });
    }

    public JTableStats(String[] headers, Class[] columnTypes, boolean[] editable) {
        this(
                headers,
                columnTypes,
                new int[] { SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
                        SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
                        SwingConstants.CENTER, SwingConstants.CENTER },
                editable);
    }

    public JTableStats(String[] headers, Class[] columnTypes, int[] align) {
        this(
                headers,
                columnTypes,
                align,
                new boolean[] { false, false, false, false, false, false, false, false, false, false });
    }

    public JTableStats(String[] headers, Class[] columnTypes, int[] align, boolean[] editable) {

        super();
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setAutoCreateRowSorter(true);
        this.alignList = align;
        this.setModel(new OpisTableModel(headers, columnTypes, editable));

        HashMap<Integer, DefaultTableCellRenderer> hashAlign = new HashMap<Integer, DefaultTableCellRenderer>();

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        hashAlign.put(SwingConstants.CENTER, centerRenderer);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        hashAlign.put(SwingConstants.LEFT, leftRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        hashAlign.put(SwingConstants.RIGHT, rightRenderer);

        for (int i = 0; i < this.getColumnCount(); i++)
            this.getColumnModel().getColumn(i).setCellRenderer(hashAlign.get(align[i]));
    }

    public ArrayList<ISerializable> getTableData() {
        return this.statistics;
    }

    public void setTableData(ArrayList<ISerializable> statistics) {
        this.statistics = statistics;
    }

    public void clearTableData() {
        this.statistics = new ArrayList<ISerializable>();
    }

    public void addTableData(ArrayList<ISerializable> statistics) {
        this.statistics.addAll(statistics);
    }

    @Override
    public DefaultTableModel getModel() {
        return (DefaultTableModel) super.getModel();
    }

    public <U> int clearTable(Class<U> datatype) {
        int row = this.getSelectedRow();
        try {
            this.getModel().setRowCount(0);
        } catch (NullPointerException e) {}
        return row;
    }

    public void dataUpdated(int row) {
        this.getModel().fireTableDataChanged();

        try {
            this.setRowSelectionInterval(row, row);
        } catch (IllegalArgumentException e) {

        }
    }

    /*
     * public void setStatistics(ArrayList<ISerializable> statistics){ this.statistics = new ArrayList<ISerializable>();
     * for (Object o : statistics){ this.statistics.add((StatAbstract)o); } }
     */

}
