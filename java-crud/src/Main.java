import java.util.HashMap;

public class Main {
	private HashMap<Integer, String[]> myList = null;
	public DBH db = null;
	public void read() throws Exception {
		myList = db.getList(null);
		
		new Read(myList, db);
	}
	
	public void create() throws Exception {
		
		
	}
	
	public void update() throws Exception {
		
		
	}
	
	public void delete() throws Exception {
		
		
	}
		
	public Main(DBH dbh) throws Exception {
		db = dbh;
	}
}
