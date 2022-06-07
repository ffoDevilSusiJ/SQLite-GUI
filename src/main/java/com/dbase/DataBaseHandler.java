package com.dbase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.MainPage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataBaseHandler {

	
	public static String connectionString;
	protected static Connection getDBConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection(connectionString);
		return connection;
	}

	public static void openSQLFile(String nameFile) throws ClassNotFoundException, SQLException {
		if (nameFile != null) {
			try {
				connectionString = "jdbc:sqlite:" + nameFile;
			} catch (Exception e) {
				System.out.println("File not found");
			}
		} else
			System.out.println("File not found");
		System.out.println("File found");
	}

	public static ResultSet getTablesSet() throws SQLException, ClassNotFoundException {
		Statement statement = getDBConnection().createStatement();
		String query = "SELECT name FROM sqlite_master WHERE type='table';";
		ResultSet rs = null;
		rs = statement.executeQuery(query);
		return rs;
	}

	public static ResultSet getTableItem(String item_type) throws SQLException, ClassNotFoundException {
		Statement statement = getDBConnection().createStatement();
		String query = "SELECT * FROM " + item_type + ";";
		ResultSet rs = null;
		rs = statement.executeQuery(query);
		return rs;
	}

	public static void updateValue(String table, String field, int rowid, String value) throws SQLException, ClassNotFoundException {
		String query = "UPDATE " + table + " SET " + field + " = ?" + " Where " + "rowid" + " = ?" + ";";
		System.out.println(query);
		PreparedStatement statement = getDBConnection().prepareStatement(query);
		statement.setString(1, value);
		statement.setInt(2, rowid);
		System.out.println(rowid + "  " + value);
		statement.executeUpdate();
		statement.getConnection().close();
	}

	public static ResultSet getPrimaryKeys(String table) throws SQLException, ClassNotFoundException {
		DatabaseMetaData meta = getDBConnection().getMetaData();
		ResultSet primaryKeys = null;
		ResultSet tables = meta.getTables(null, null, "%", new String[] { "TABLE" });
		String catalog = tables.getString("TABLE_CAT");
		String schema = tables.getString("TABLE_SCHEM");
		String tableName = table;
		primaryKeys = meta.getPrimaryKeys(catalog, schema, tableName);
		return primaryKeys;
	}

	// public static void formField(String table) throws ClassNotFoundException, SQLException{
	// 	ArrayList<DataBaseField> f = new ArrayList<>();
	// 	DatabaseMetaData meta = getDBConnection().getMetaData();
	// 	ResultSet columns = meta.getColumns(null, null, table, "%");
	// 	while(columns.next()){
	// 		DataBaseField field = new DataBaseField();
	// 		System.out.println(columns.getString("COLUMN_NAME"));
	// 		columns.next();
	// 	}
		

	// 	ObservableList<DataBaseField> fields = FXCollections.observableList(f);
	// 	System.out.println();
	// }

	public static boolean getAutoincrement(String table, int column) throws SQLException, ClassNotFoundException {
		String tableName = table;
		Statement statement = getDBConnection().createStatement();
		ResultSet tableInfo = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1=2");
		ResultSetMetaData rsMetaData = tableInfo.getMetaData();
		boolean a = rsMetaData.isAutoIncrement(column);
		statement.getConnection().close();
		statement.close();
		return a;
	}

	public static void addItem(String table, List<String> fields) throws SQLException, ClassNotFoundException {
		ResultSet rs = getTableItem(table);
		String query = "INSERT INTO " + table + " VALUES ( ";
		for (int i = 0; i < fields.size(); i++) {
			if (i != fields.size() - 1)
				query = query + "?" + ",";
			else
				query = query + "?";
		}
		query = query + " );";
		PreparedStatement statement = getDBConnection().prepareStatement(query);
		ResultSet primaryKeys = getPrimaryKeys(MainPage.getCurrentTable());
		for (int i = 0; i < fields.size(); i++) {
			if(!primaryKeys.getString("COLUMN_NAME").equals(fields.get(i))){
				if (rs.getMetaData().getColumnType((i + 1)) == java.sql.Types.INTEGER) {
					statement.setInt((i + 1), Integer.valueOf(fields.get(i)));
				}
				if (rs.getMetaData().getColumnType((i + 1)) == java.sql.Types.VARCHAR) {
					statement.setString((i + 1), fields.get(i));
				}
			} 
		}
		primaryKeys.getStatement().getConnection().close();
		rs.getStatement().getConnection().close();
		statement.executeUpdate();
		statement.getConnection().close();
		statement.close();
	}

	public static void removeItem(String table, String primField, String value) throws SQLException, ClassNotFoundException {
		String query = "DELETE FROM " + table + " WHERE " + primField + " = " + value + ";";
		PreparedStatement statement = getDBConnection().prepareStatement(query);
		statement.executeUpdate();
		statement.getConnection().close();
		statement.close();
	}

	
	public static void removeTable(String table) throws SQLException, ClassNotFoundException {
		String query = "DROP TABLE IF EXISTS "+ table + ";";
		Statement statement = getDBConnection().createStatement();
		statement.executeUpdate(query);
		statement.getConnection().close();
		statement.close();
	}

	public static void createTable(String table, ArrayList<DataBaseField> fields) throws SQLException, ClassNotFoundException {
		Statement statement = getDBConnection().createStatement();
		String query = "CREATE TABLE " + table + " (";
		for(int i = 0; i < fields.size(); i++){
			DataBaseField field = fields.get(i);
			query = query + field.getName() + " " + field.getType() + " ";
			if(field.isPrimaryKey()){
				query = query + "PRIMARY KEY ";
			}
			if(field.isAutoIncrement){
				query = query + "AUTOINCREMENT ";
			}
			if(field.isNOTNULL){
				query = query + "NOT NULL ";
			}
			if(field.isUNIQUE){
				query = query + "UNIQUE ";
			}
			if(i != fields.size() - 1)
			query = query + ",";
		}
		query = query + ");";
		System.out.println(query);
		statement.executeUpdate(query);
		statement.getConnection().close();
	}

	public static void addField(String table, DataBaseField field) throws SQLException, ClassNotFoundException {
		Statement statement = getDBConnection().createStatement();
		String query = "ALTER TABLE " + table + " ADD ";
			query = query + field.getName() + " " + field.getType() + " ";
			if(field.isPrimaryKey()){
				query = query + "PRIMARY KEY ";
			}
			if(field.isAutoIncrement){
				query = query + "AUTOINCREMENT ";
			}
			if(field.isNOTNULL){
				query = query + "NOT NULL ";
			}
			if(field.isUNIQUE){
				query = query + "UNIQUE ";
			}

		query = query + ";";
		System.out.println(query);
		statement.executeUpdate(query);
		statement.getConnection().close();
	}
}
