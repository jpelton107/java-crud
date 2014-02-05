import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class View extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton btn;
	protected JButton deleteBtn;
	protected JTable table;
	protected DBH db;
	protected JPanel panel;
	protected HashMap<Integer, String[]> list;
	protected Main m;
	private int rowCount;
	private int colCount;
	private TableModel model;
	public View(Main m) {
		this.m = m;
		panel = new JPanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CRUD");
		//setPreferredSize(new Dimension(800,600));
		initNav();
	}
	
	public void initNav() {
		btn = new JButton("Create");
		btn.addActionListener(this);
		btn.setActionCommand("Create");
		deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(this);
		deleteBtn.setActionCommand("Delete");
		panel.add(btn);
		panel.add(deleteBtn);
		pack();
	}
	
	public void loadRead(HashMap<Integer, String[]> list, DBH db) {
		this.list = list;
		this.db = db;
		
		this.rowCount = db.getRowCount(); 
		this.colCount = db.getColCount();
		String[] colNames = db.getColNames();
		Object[][] data = new Object[rowCount][colCount];
		
		System.out.println("Rows: " + rowCount + ", Cols : " + colCount);
		
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
	
		model = new EditableTableModel(colNames, data);
		table = new JTable(model);
		table.createDefaultColumnsFromModel();
		
		//TODO: change to checkbox
		// setup combo box for Completed 
		String[] yn = {"Yes", "No"};
		JComboBox cb = new JComboBox(yn);
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cb));
		
		// set col widths
		TableColumn col1 = table.getColumnModel().getColumn(0);
		col1.setPreferredWidth(20);
		TableColumn col2 = table.getColumnModel().getColumn(1);
		col2.setPreferredWidth(200);
		TableColumn col3 = table.getColumnModel().getColumn(2);
		col3.setPreferredWidth(150);
		TableColumn col4 = table.getColumnModel().getColumn(3);
		col4.setPreferredWidth(50);
		TableColumn col5 = table.getColumnModel().getColumn(4);
		col5.setPreferredWidth(20);
		
		// finish settings up layout
		panel.add(new JScrollPane(table));
		add(panel);
		setSize(800,500);
		pack();
		setVisible(true);
		
	}
	
	public void loadCreate() {
		panel.removeAll();
		
		String[] priorities = {"1 - Urgent", "2", "3 - Normal", "4", "5 - Trivial"};
		final JComboBox priorityList = new JComboBox(priorities);
		priorityList.setSelectedIndex(2);
		JButton addBtn = new JButton("Add");
		final JTextField desc = new JTextField();
		JButton backBtn = new JButton("Cancel");
		
		panel.setLayout(new GridLayout(2,3));
		panel.add(new JLabel("Description"));
		panel.add(new JLabel("Priority"));
		panel.add(backBtn);
		panel.add(desc);
		panel.add(priorityList);
		panel.add(addBtn);
	
		add(panel);
		pack();
		setVisible(true);
	
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date dt = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdf.format(dt);
				int prioritySelect = priorityList.getSelectedIndex() + 1;
				String prioritySelected = Integer.toString(prioritySelect);
				
				String[] cols = {"id", "description", "date", "priority", "completed"};
				String[] values = {"0", desc.getText(), date, prioritySelected, "N"}; 
				try {
					db.addRow(cols, values);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					try {
						resetRead();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					panel.add(new JLabel("Entry has been successfully added"));
				}				
			}
		});
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				panel.setLayout(new FlowLayout());
				initNav();
				loadRead(list, db);
			}
		});
	}
	
	public void deleteRow() {
		int row = table.getSelectedRow();
		if (row < 0) {
			System.err.println("Error : Please select a record");
		} else {
			try {
				// set id
				int recNum = table.convertRowIndexToModel(row);
				String id = (String) table.getModel().getValueAt(recNum, 0);
				
				// delete row in db
				db.deleteRow(id);
				table.clearSelection();
				
				// reset list of items
				int rowCount = model.getRowCount();
				resetRead();
				
				System.out.println(recNum + " (" + id + ") was deleted");
			} catch(Exception e) {
				System.err.println(e);
			}
		}
	}
	
	public void resetRead() throws Exception {
		this.list = m.reloadData();
		
		// reset layout
		panel.removeAll();
		panel.setLayout(new FlowLayout());
		initNav();
		loadRead(list, db);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if (cmd.equals("Create")) {
			loadCreate();	
		} else if (cmd.equals("Delete")) {
			deleteRow();
		}
	}

	class EditableTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
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
}