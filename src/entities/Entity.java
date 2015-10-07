package entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class Entity {
	private static Properties props = new Properties();
	private static final String url = "jdbc:postgresql://localhost/address_book";
	private static Connection conn;
	
	Entity(){	
	}
	
	protected Connection getDBConnection() throws SQLException{
		props.setProperty("user","matthewmorales");
		props.setProperty("password","wavy45OO");
		conn = DriverManager.getConnection(url, props);
		return conn;
	}
}
