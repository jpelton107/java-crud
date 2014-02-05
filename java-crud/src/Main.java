import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Main {
	private Object[][] myList = null;
	public DBH db = null;
	protected View view;
	public String[] priorities = {"1 - Urgent", "2", "3 - Normal", "4", "5 - Trivial"};
	
	public Main(DBH db) throws Exception {
		this.view = new View();
		this.db = db;
	}
	
	public void read() throws Exception {
		view.clearDisplay();
		view.addCreateListener(new CreateListener());
		view.addDeleteListener(new DeleteListener());
		view.initNav();
		myList = loadData();
		view.loadDataTable(myList, db.getColNames());
	}
	
	public void create() throws Exception {
		view.clearDisplay();
		view.loadCreate(priorities);
		view.addSubmitListener(new SubmitListener());
		view.addBackListener(new BackListener());
	}
	
	public void update() throws Exception {
		
		
	}
	
	public void delete() throws Exception {
		int row = view.table.getSelectedRow();
		try {
			// set id
			int recNum = view.table.convertRowIndexToModel(row);
			String id = (String) view.table.getModel().getValueAt(recNum, 0);
			
			// delete row in db
			db.deleteRow(id);
			view.table.clearSelection();
			
			// reset list of items
			read();
			
		} catch(Exception e) {
			System.err.println(e);
		}
		
	}

	private Object[][] loadData() throws Exception {
		HashMap<Integer, String[]> list = db.getList(null);
		int rowCount = db.getRowCount();
		int colCount = db.getColCount();
		Object[][] data = new Object[rowCount][colCount];
		int j = 0;
		
		for (Integer key : list.keySet()) {
			try {
				for(String col : list.get(key)) {				
					// set object value
					data[key][j++] = col;
				}
			} catch(Exception e) {
				
			}
			j = 0;
		}
		return data;
	}
	
	class SubmitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(dt);
			int prioritySelect = view.priorityList.getSelectedIndex() + 1;
			String prioritySelected = Integer.toString(prioritySelect);
			
			String[] cols = {"id", "description", "date", "priority", "completed"};
			String[] values = {"0", view.desc.getText(), date, prioritySelected, "N"}; 
			try {
				db.addRow(cols, values);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				try {
					read();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}				
		}
	}
	
	class BackListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.clearDisplay();
			try {
				read();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				delete();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class CreateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				create();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}

}
