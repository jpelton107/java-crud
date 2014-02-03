
enum Controller {
	MAIN, CREATE, READ, UPDATE, DELETE
}

public class Bootstrap {
	protected DBH db;
	static Controller active;

	public Bootstrap() throws Exception {
		db = new DBH();
		db.readDatabase();
	}
	public static void main(String[] args) throws Exception {

		Bootstrap boot = new Bootstrap();
		active = Controller.MAIN;
		boot.loadController();

	}
	
	public void loadController() throws Exception {
		Main c = new Main(db); 
		switch(active) {
		case MAIN:
		case READ: c.read(); break;
		case CREATE:
			c.create();
			break;
		case UPDATE:
			c.update();
			break;
		case DELETE:
			c.delete();
			break;
			
		}
	}
}
