import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;

public class View extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	protected JPanel panel;
	
	// Read items
	public JButton createBtn = new JButton("Create");
	public JButton deleteBtn = new JButton("Delete");
	public JTable table;
	public TableModel model;
	
	// Create items
	public JComboBox priorityList;
	public JTextField desc;
	public JButton addBtn = new JButton("Add");
	public JButton backBtn = new JButton("Cancel");
	
	public View() {
		panel = new JPanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CRUD");
		//setPreferredSize(new Dimension(800,600));
		initNav();
	}
	
	public void initNav() {
		panel.add(createBtn);
		panel.add(deleteBtn);
		pack();
	}
	
	public void loadDataTable(Object[][] data, String[] colNames) {
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
		
		panel.add(new JScrollPane(table));
		add(panel);
		pack();
		setVisible(true);
	}
	
	public void clearDisplay() {
		panel.removeAll();
		panel.setLayout(new FlowLayout());
	}
	
	public void loadCreate(String[] priorities) {
		priorityList = new JComboBox(priorities);
		desc = new JTextField();
		priorityList.setSelectedIndex(2);
		
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
	
	}
	
	public void addSubmitListener(ActionListener sal) {
		addBtn.addActionListener(sal);
	}

	public void addBackListener(ActionListener bal) {
		backBtn.addActionListener(bal);
		// navigation
	}
	
	public void addCreateListener(ActionListener cal) {
		createBtn.addActionListener(cal);
		// navigation
	}
	
	public void addDeleteListener(ActionListener dal) {
		deleteBtn.addActionListener(dal);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}