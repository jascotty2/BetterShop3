/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description: class
 * for working with a MySQL server
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.libv2.mysql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.libv2.net.InstallDependency;

public class MySQL {

	// local copy of current connection info
	protected String sql_username, sql_password, sql_database, 
			sql_hostName = "localhost", sql_portNum = "3306";
	// DB connection
	protected Connection DBconnection = null;
	private static int checkedDep = 0;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public MySQL() {
	}

	public MySQL(MySQL copy) {
		DBconnection = copy.DBconnection;
		this.sql_username = copy.sql_username;
		this.sql_password = copy.sql_password;
		this.sql_database = copy.sql_database;
		this.sql_hostName = copy.sql_hostName;
		this.sql_portNum = copy.sql_portNum;
	}

	public MySQL(String database, String username, String password, String hostName, String portNum) {
		this.sql_database = database;
		this.sql_username = username;
		this.sql_password = password;
		this.sql_hostName = hostName;
		this.sql_portNum = portNum;
	}

	public MySQL(String database, String username, String password, String hostName) {
		this.sql_database = database;
		this.sql_username = username;
		this.sql_password = password;
		this.sql_hostName = hostName;
	}

	public MySQL(String database, String username, String password) throws Exception {
		this.sql_database = database;
		this.sql_username = username;
		this.sql_password = password;
	}

	public MySQL(String database, String username) throws Exception {
		this.sql_database = database;
		this.sql_username = username;
		this.sql_password = "";
	}

	/**
	 * Checks if mysql-connector-java-bin.jar exists <br />
	 * If not, will automatically download and save the jar to "lib/"
	 * @return
	 */
	public static boolean checkDependency() {
		//int checked = 0;
		if (checkedDep != 0) {
			return checkedDep > 0;
		}
		// file used by iConomy
		// 4 can try, name shared with iConomy mysql.. i don't check for all possibilities, though)
		String names[] = new String[]{"lib/mysql.jar",
			"lib/mysql-connector-java-bin.jar",
			"lib/mysql-connector-java-5.1.14-bin.jar",
			"lib/mysql-connector-java-5.1.15-bin.jar"};
		File f;
		for (int i = 0; i < names.length; ++i) {
			f = new File(names[i]);
			if (f.exists()) {
				checkedDep = 1;
				return true;
			}
		}

		// file not found: download jar to lib folder
		if (!InstallDependency.install("lib" + File.separator + "mysql-connector-java-bin.jar",
				"mysql-connector-java-5.1.15/mysql-connector-java-5.1.15-bin.jar",
				"http://mirror.services.wisc.edu/mysql/Downloads/Connector-J/mysql-connector-java-5.1.15.zip",
				"http://mirror.anigaiku.com/Dependencies/mysql-connector-java-bin.jar")) {
			// failed to download the required lib to use this class
			checkedDep = -1;
			return false;
		}
		// download successful
		checkedDep = 1;
		return true;

	}
	
	/**
	 * Connect to a database
	 *
	 * @param database MySQL database to use
	 * @param username MySQL username to connect as
	 * @param password MySQL user password
	 * @param hostName host of server
	 * @param portNum port to use
	 * @return if new connection successful (false if there was no change)
	 * @throws Exception
	 */
	public final boolean connect(String database, String username, String password, String hostName, String portNum) throws Exception {
		if (!(isConnected() && sql_database.equals(database)
				&& sql_username.equals(username)
				&& sql_password.equals(password)
				&& sql_hostName.equals(hostName)
				&& sql_portNum.equals(portNum))) {

			sql_database = database;
			sql_username = username;
			sql_password = password;
			sql_hostName = hostName;
			sql_portNum = portNum;
			return connect();
		}
		return false;
	}

	/**
	 * Connect to a database
	 *
	 * @param database MySQL database to use
	 * @param username MySQL username to connect as
	 * @param password MySQL user password
	 * @param hostName host of server
	 * @return if new connection successful (false if there was no change)
	 * @throws Exception
	 */
	public final boolean connect(String database, String username, String password, String hostName) throws Exception {
		if (!(isConnected() && sql_database.equals(database)
				&& sql_username.equals(username)
				&& sql_password.equals(password)
				&& sql_hostName.equals(hostName))) {

			sql_database = database;
			sql_username = username;
			sql_password = password;
			sql_hostName = hostName;
			sql_portNum = "3306";
			return connect();
		}
		return false;
	}

	/**
	 * Connect to a database on localhost
	 *
	 * @param database MySQL database to use
	 * @param username MySQL username to connect as
	 * @param password MySQL user password
	 * @return if new connection successful (false if there was no change)
	 * @throws Exception
	 */
	public final boolean connect(String database, String username, String password) throws Exception {
		if (!(isConnected() && sql_database.equals(database)
				&& sql_username.equals(username)
				&& sql_password.equals(password))) {

			sql_database = database;
			sql_username = username;
			sql_password = password;
			sql_hostName = "localhost";
			sql_portNum = "3306";
			return connect();
		}
		return false;
	}

	/**
	 * Connect to a database on localhost (blank password)
	 *
	 * @param database MySQL database to use
	 * @param username MySQL username to connect as
	 * @return if new connection successful (false if there was no change)
	 * @throws Exception
	 */
	public final boolean connect(String database, String username) throws Exception {
		if (!(isConnected() && sql_database.equals(database)
				&& sql_username.equals(username))) {

			sql_database = database;
			sql_username = username;
			sql_password = "";
			sql_hostName = "localhost";
			sql_portNum = "3306";
			return connect();
		}
		return false;
	}


	/**
	 * Connect/Reconnect using current info
	 *
	 * @return if can connect & connected
	 * @throws Exception
	 */
	public final boolean connect() throws Exception {
		if (DBconnection == null) {
			// double-check that mysql-bin.jar exists
			if (!checkDependency()) {
				return false;
			}

			// connect to database
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			DBconnection = DriverManager.getConnection(
					String.format("jdbc:mysql://%s:%s/%s?create=true,autoReconnect=true",
					sql_hostName, sql_portNum, sql_database),
					sql_username, sql_password);

			// or append "user=%s&password=%s", sql_username, sql_password);
			// create=true: create database if not already exist
			// autoReconnect=true: should fix errors that occur if the connection times out
		} else {
//			if (isConnected()) {
//				disconnect();
//			}

			if (DBconnection.isClosed()) {
				DBconnection = DriverManager.getConnection(
						String.format("jdbc:mysql://%s:%s/%s?create=true,autoReconnect=true",
						sql_hostName, sql_portNum, sql_database),
						sql_username, sql_password);
			}
		}
		return DBconnection != null && DBconnection.getCatalog() != null; // .getSchema()
	}

	/**
	 * close the MySQL server connection
	 */
	public void disconnect() { //  throws Exception
		try {
			if (DBconnection != null && !DBconnection.isClosed()) {
				DBconnection.close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, "Error closing MySQL connection", ex);
		}
		DBconnection = null;
	}

	/**
	 * check if is currently connected to a server preforms a pre-check, and if
	 * not & connection info exists, will attempt reconnect
	 */
	public boolean isConnected() {
		return isConnected(true);
	}

	public boolean isConnected(boolean reconnect) {
		try {
			if (reconnect && (DBconnection == null || DBconnection.isClosed())) {
				try {
					connect();
				} catch (Exception ex) {
					// should not reach here, since is only thrown if creating a new connection
					// (while connecting to the mysql lib)
				}
			}
			return DBconnection != null && !DBconnection.isClosed() && DBconnection.getCatalog() != null; //.getSchema() != null;
		} catch (SQLException ex) {
			Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, "Error checking MySQL connection status", ex);
		} catch (AbstractMethodError e) {
		} catch (Throwable e) {
		}
		DBconnection = null;
		return false;
	}

	/**
	 * manually force database to save
	 */
	public void commit() throws SQLException {
		if (isConnected()) {
			DBconnection.createStatement().executeUpdate("COMMIT;");
		}
	}

	public ResultSet getQuery(String qry) throws SQLException {
		// System.out.println("\nGet: " + qry + "\n");
		if (isConnected()) {
			try {
				if (!qry.trim().endsWith(";")) {
					qry += ";";
				}
				//System.out.println("MySQL: ------------\n" + qry + "\n-------------------");
				//BetterShopLogger.Log(Level.INFO, String.format("SELECT * FROM %s WHERE NAME='%s';", sql_tableName, name));
				return DBconnection.createStatement().executeQuery(qry);
			} catch (SQLException ex) {
				// if lost connection & successfully reconnected, try again
				if (!isConnected(false) && isConnected(true)) {
					try {
						return DBconnection.createStatement().executeQuery(qry);
					} catch (SQLException ex2) {
						throw new SQLException("Query Error: " + qry, ex2);
					}
				}
				//disconnect();
				throw new SQLException("Query Error: " + qry, ex);
			}
		} else {
			return null;
		}
	}
	
	public int runUpdate(String qry) throws SQLException { //
		if (isConnected()) {
			try {
				if (!qry.trim().endsWith(";")) {
					qry += ";";
				}
				return DBconnection.prepareStatement(qry).executeUpdate();
			} catch (SQLException ex) {
				// if lost connection & successfully reconnected, try again
				if (!isConnected(false) && isConnected(true)) {
					try {
						return DBconnection.prepareStatement(qry).executeUpdate();
					} catch (SQLException ex2) {
						throw new SQLException("Query Error: " + qry, ex2);
					}
				}
				//disconnect();
				throw new SQLException("Query Error: " + qry, ex);
			}
		} else {
			return -1;
		}
	}

	public Object getField(String qry, int field) throws SQLException, Exception {
		ResultSet res = getQuery(qry);
		res.next();
		return res.getObject(field);
	}

	public Object getField(String qry, String field) throws SQLException, Exception {
		ResultSet res = getQuery(qry);
		res.next();
		return res.getObject(field);
	}

	public Object getField(String qry, int row, int field) throws SQLException, Exception {
		ResultSet res = getQuery(qry);
		int r = 0;
		do {
			if (!res.next()) {
				return null;
			}
		} while (++r < row);
		return res.getObject(field);
	}

	public Object getField(String qry, int row, String field) throws SQLException, Exception {
		ResultSet res = getQuery(qry);
		int r = 0;
		do {
			if (!res.next()) {
				return null;
			}
		} while (++r < row);
		return res.getObject(field);
	}

	/**
	 * check if connected & a table exists in this database
	 *
	 * @param tableName table to look up
	 * @throws SQLException
	 */
	public boolean tableExists(String tableName) throws SQLException {
		if (isConnected()) {
			//try {
			return DBconnection.getMetaData().getTables(null, null, tableName, null).next();
			//} catch (SQLException ex) {
			//    Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, "Error retrieving table list", ex);
			//}
		}
		return false;
	}

	public boolean columnExists(String tableName, String columnName) throws SQLException {
		if (isConnected()) {
			ResultSet t = DBconnection.getMetaData().getColumns(null, null, tableName, null);//.getTables(null, null, tableName, null);//
			for (; t.next();) {
				//for(int i=1; i<=7; ++i)
				//System.out.println(t.getString(i));
				//System.out.println();
				if (t.getString(4).equals(columnName)) {
					return true;
				}
				//try {
				//t.getRowId(columnName);
				//t.findColumn(columnName);
				//} catch (SQLException ex) {
				//    Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage(), ex);
				//    return false;
				//}
			}
		}
		return false;
	}

	public boolean columnsExist(String tableName, String... columns) throws SQLException {
		if (isConnected()) {
			String[] tcols = getColumnNames(tableName);
			// for each column checking, scan if that column name exists
			for (String c : columns) {
				boolean exists = false;
				for(String tc : tcols) {
					if((tc == null && c == null) || (tc != null && c != null && tc.equalsIgnoreCase(c))) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public String[] getColumnNames(String tableName) throws SQLException {
		if (isConnected()) {
			ResultSet t = DBconnection.getMetaData().getColumns(null, null, tableName, null);//.getTables(null, null, tableName, null);//
			t.last();
			String[] cols = new String[t.getRow()];
			t.beforeFirst();
			while(t.next()) {
				cols[t.getRow() - 1] = t.getString(4); // "COLUMN_NAME"
			}
			return cols;
//			ArrayList<String> cols = new ArrayList<String>();
//			for (; t.next();) {
//				cols.add(t.getString(4));
//			}
//			return cols.toArray(new String[0]);
		}
		return null;
	}

	public ResultSet getColumns(String tableName) throws SQLException {
		if (isConnected()) {
			return DBconnection.getMetaData().getColumns(null, null, tableName, null);
		}
		return null;
	}

	public String getUserName() {
		return sql_username;
	}

	public String getDatabaseName() {
		return sql_database;
	}

	public String getHostName() {
		return sql_hostName;
	}

	public String getPortNum() {
		return sql_portNum;
	}
} // end class BSMySQL

