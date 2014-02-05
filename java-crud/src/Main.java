import java.util.HashMap;

public class Main {
	private HashMap<Integer, String[]> myList = null;
	public DBH db = null;
	protected View view;
	
	public Main(DBH db) throws Exception {
		this.view = new View(this);
		this.db = db;
	}
	
	public void read() throws Exception {
		myList = db.getList(null);
		view.loadRead(myList, db);
	}
	
	public void create() throws Exception {
		
		
	}
	
	public void update() throws Exception {
		
		
	}
	
	public void delete() throws Exception {
		
		
	}

	public HashMap<Integer, String[]> reloadData() throws Exception {
		return db.getList(null);
	}
}
