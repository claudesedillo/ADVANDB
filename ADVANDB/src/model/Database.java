package model;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

	private Connection connection;
	private String url = "jdbc:mysql://localhost:3306/advandbmc01?useSSL = true";
	private final String username = "root";
	private final String password = "1234";
	
	public Database(){
		Connect();
	}
	
	public void Connect(){
		try{
			connection = DriverManager.getConnection(url, username, password);
			if(connection != null)
			System.out.println("Connected to " + url + " with " + username);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
