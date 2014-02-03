import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class Read extends View {
	protected DBH db;
	public Read(HashMap<Integer, String[]> list, DBH db) {
		int rowCount = db.getRowCount(); 
		int colCount = db.getColCount();
		String[] colNames = db.getColNames();
		this.db = db;
		Object[][] data = new Object[rowCount][colCount];
		int j = 0;
		
		for (Integer key : list.keySet()) {
			for(String col : list.get(key)) {				
				// set object value
				data[key][j++] = col;
			}
			j = 0;
		}
		
		TableModel model = new EditableTableModel(colNames, data);
		JTable table = new JTable(model);
		table.createDefaultColumnsFromModel();
		
		String[] yn = {"Yes", "No"};
		JComboBox cb = new JComboBox(yn);
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cb));
		
		f.add(new JScrollPane(table));
		f.setVisible(true);
	}
}

class EditableTableModel extends AbstractTableModel {
	String[] colNames;
	Object[][] data;
	int rowCount;
	
	public EditableTableModel(String[] colNames, Object[][] data) {
		this.colNames = colNames;
		this.data = data;
	}
	
	public int getRowCount() { return data.length; }
	public int getColumnCount() { return colNames.length; }
	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	public String getColumnName(int col) {
		return colNames[col];
	}
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}
	
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		
		// update db
		DBH db = new DBH();
		try {
			db.readDatabase();
			db.updateRow(colNames[col], value.toString(), data[row][0].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}