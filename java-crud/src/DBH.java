import java.sql.*;
import java.util.HashMap;

public class DBH {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String[] colNames = null;
	private HashMap<Integer, String[]> cache = new HashMap<Integer, String[]>();
	private int colCount = 0;
	private int rowCount = 0;
	
	public void readDatabase() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/list?user=root");
		} catch (Exception e) {
			throw e;
		}
	}
	
	public HashMap<Integer, String[]> getList(Integer id) throws Exception {
		try {
			statement = connect.createStatement();
			String query = "select * from list.items";
			if (id != null) {
				String where = " where id='" + id + "'";
			
				query += where;
				getRow(query);
			} else {
				getAll(query);
			}
			
		} catch (Exception e) {
			throw e;
		}
		return cache;
	}
	
	public void setRowCount() throws SQLException {
		resultSet.last();
		rowCount = resultSet.getRow();
		resultSet.beforeFirst();		
	}
	
	public HashMap<Integer, String[]> getAll(String query) throws Exception {
		resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();
		colCount = metaData.getColumnCount();
		setRowCount();
		setColNames(metaData, colCount);
			
		int i = 0;
		while(resultSet.next()) {
			String[] cols = new String[colCount];
			for (int j = 0; j < colCount; j++) {
				cols[j] = resultSet.getString(j+1);
			}
			cache.put(i++, cols);
		}
		//parseResults();0
		statement.close();
		return cache;
		
	}
	
	public HashMap<Integer, String[]> getRow(String query) throws Exception {
		resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();
		colCount = metaData.getColumnCount();
		setRowCount();
		setColNames(metaData, colCount);
		
		String[] cols = new String[colCount];
		for (int j = 0; j < colCount; j++) {	
			cols[j] = resultSet.getString(j+1);
		}
		cache.put(0, cols);
		//parseResults();0
		statement.close();
		return cache;
		
	}
	
	public void updateRow(String col, String value, String whereValue) throws SQLException {
		PreparedStatement ps = connect.prepareStatement("update list.items set " + col + "=? where id=? limit 1");
		if (value == "Yes") {
			value = "Y";
		} else if (value == "No") {
			value = "N";
		}
		
		ps.setString(1, value);
		ps.setString(2, whereValue);
		ps.executeUpdate();
	}
	
	public int addRow(String[] cols, String[] values) throws SQLException {
		if (cols.length != values.length){
			System.err.println("Incorrect number of keys compared to values");
			System.exit(0);
		}
		String query = "insert into list.items (";
		String queryVals = ") values (";
		for (String col : cols) {
			query += col + ", ";
			queryVals += "?, ";
		}
		query = query.substring(0, -2);
		queryVals = queryVals.substring(0, -2);
		query += queryVals;
		
		PreparedStatement ps = connect.prepareStatement(query);
		int i = 1;
		for (String value : values) {
			ps.setString(i++, value);
		}
		return ps.executeUpdate();
	}
	
	public void deleteRow(String whereValue) throws SQLException {
		PreparedStatement ps = connect.prepareStatement("delete from list.items where id=? limit 1");
		ps.setString(1, whereValue);
		ps.executeUpdate();
	}
	
	public void setColNames(ResultSetMetaData md, int colCount) throws SQLException {
		String[] colNames = new String[colCount];
		for(int k = 1; k <= colCount; k++) {
			colNames[k-1] = md.getColumnName(k);
		}
		this.colNames = colNames;
	}
	
	public int getColCount() { return colCount; }
	public int getRowCount() { return rowCount; }
	public String[] getColNames() { return colNames; }
}
