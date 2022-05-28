package com.dbase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DataBaseHandler {

	public static Connection dbConnection;

	protected static Connection getDBConnection(String connectionString) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection(connectionString);
		return connection;
	}

	public static void openSQLFile(String nameFile) throws ClassNotFoundException, SQLException {
		if (nameFile != null) {
			try {
				String connection = "jdbc:sqlite:" + nameFile;
				dbConnection = getDBConnection(connection);
			} catch (Exception e) {
				System.out.println("File not found");
			}
		} else
			System.out.println("File not found");
		System.out.println("File found");
	}

	public static ResultSet getTablesSet() throws SQLException, ClassNotFoundException {
		java.sql.Statement statement = dbConnection.createStatement();

		String query = "SELECT name FROM sqlite_master WHERE type='table';";
		ResultSet rs = null;

		rs = statement.executeQuery(query);
		// statement.close();
		return rs;
	}

	public static ResultSet getTableItem(String item_type) throws SQLException, ClassNotFoundException {
		java.sql.Statement statement = dbConnection.createStatement();

		String query = "SELECT * FROM " + item_type + ";";
		ResultSet rs = null;
		rs = statement.executeQuery(query);
		// statement.close();
		return rs;
	}

	public static void updateValue(String table, String field, int rowid, String value) throws SQLException {
		getPrimaryKeys(table);
		String query = "UPDATE " + table + " SET " + field + " = ?" + " Where " + "rowid" + " = ?" + ";";
		System.out.println(query);
		PreparedStatement statement = dbConnection.prepareStatement(query);
		statement.setString(1, value);
		statement.setInt(2, rowid);
		System.out.println(rowid + "  "+ value);
		statement.executeUpdate();
	}

	public static ResultSet getPrimaryKeys(String table) throws SQLException {
		DatabaseMetaData meta = dbConnection.getMetaData();
		ResultSet primaryKeys = null;
		ResultSet tables = meta.getTables(null, null, "%", new String[] { "TABLE" });
		String catalog = tables.getString("TABLE_CAT");
		String schema = tables.getString("TABLE_SCHEM");
		String tableName = table;
		primaryKeys = meta.getPrimaryKeys(catalog, schema, tableName);
		return primaryKeys;
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
		query = query + ");";
		PreparedStatement statement = dbConnection.prepareStatement(query);
		for (int i = 0; i < fields.size(); i++) {
			if (rs.getMetaData().getColumnType((i + 1)) == java.sql.Types.INTEGER) {
				statement.setInt((i + 1), Integer.valueOf(fields.get(i)));
			}
			if (rs.getMetaData().getColumnType((i + 1)) == java.sql.Types.VARCHAR) {
				statement.setString((i + 1), fields.get(i));
			}
		}

		statement.executeUpdate();
	}
}
