package ua.rokytne.bakery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

/**
 * Contain database connection.
 * @version 1.0
 * @author Vadym
 *
 */
public class DataBase {
	
	private static DataBase _this = null;
	
	private Connection connection = null;
	private boolean isConnected = false;
	
	private DataBase() { 
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			setConnection(conn);
		}
		catch(Exception ex) {
			setConnection(null);
			setConnected(false);
		}
	}
	
	public static DataBase getInstance(){
		if(_this==null)
			_this = new DataBase();
		
		return _this;
	}
	
	// Connected
	public void setConnected(boolean value){
		this.isConnected = value;
	}
	
	public boolean getConnected(){
		return this.isConnected;
	}
	
	// Connection
	private void setConnection(Connection value){
		this.connection = value;
	}
	
	public Connection getConnection(){
		return this.connection;
	}
	
	public void closeConnection(){
		if(this.connection!=null){
			try{
				this.connection.close();
			}
			catch(Exception exception){
				// З'єднання може бути вже зачиненим.
			}
			setConnection(null);
		}
		setConnected(false);
	}
	
}
