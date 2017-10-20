package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private Connection connection;
	private String url = "jdbc:mysql://localhost:3306/library?useSSL = true";
	private final String username = "root";
	private final String password = "1234";
	
	public void Connect(){
		try{
			connection = DriverManager.getConnection(url, username, password);
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
	
	public ResultSet executeQuery(){
		Statement statement;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM book");
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
