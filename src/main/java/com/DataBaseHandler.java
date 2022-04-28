package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHandler {

    public static Connection dbConnection;

    protected static Connection getDBConnection(String connectionString ) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection(connectionString);
		return connection;
	}
    
	public static void openSQLFile(String nameFile) throws ClassNotFoundException, SQLException {
		if(nameFile != null) { 
			try {
				String connection = "jdbc:sqlite:" + nameFile + ".db";
				dbConnection = getDBConnection(connection);
			} catch(Exception e) {
				System.out.println("File not found");
			}
		} else System.out.println("File not found");
        System.out.println("File found");
	}

    public static ResultSet getTablesSet() throws SQLException, ClassNotFoundException {
		java.sql.Statement statement = dbConnection.createStatement();
        
		String query = "SELECT name FROM sqlite_master WHERE type='table';";
		ResultSet rs = null;
			
			rs = statement.executeQuery(query);
		//statement.close();
		return rs;
	}

    public static ResultSet getTableItem(String item_type) throws SQLException, ClassNotFoundException {
		java.sql.Statement statement = dbConnection.createStatement();
        
		String query = "SELECT * FROM " + item_type + ";";
		ResultSet rs = null;
			
			rs = statement.executeQuery(query);
		//statement.close();
		return rs;
	}
}
