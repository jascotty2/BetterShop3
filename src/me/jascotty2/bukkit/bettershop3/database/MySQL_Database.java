/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 *
 * Description: (TODO)
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
package me.jascotty2.bukkit.bettershop3.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.libv2.mysql.MySQL;
import me.jascotty2.libv2.util.ArrayManip;
import me.jascotty2.libv2.util.Numbers;

public class MySQL_Database extends PricelistDatabaseHandler {

	private String sql_tableName = "pricelist", conn_pw;
	private MySQL sqlConnection = null;
	/**
	 * local copy of the database
	 */
	protected final HashMap<String, Map<Integer, ItemPrice>> DBprices = new HashMap<String, Map<Integer, ItemPrice>>();

	public MySQL_Database(BetterShop3 plugin) throws Exception {
		super(plugin);
		sqlConnection = new MySQL(plugin.config.sql_database, plugin.config.sql_username,
				plugin.config.sql_password, plugin.config.sql_hostName, plugin.config.sql_portNum);
		sql_tableName = plugin.config.sql_pricetable;
		conn_pw = plugin.config.sql_password;
		if (!sqlConnection.tableExists(sql_tableName)) {
			// table does not exist, so create it
			createPricelistTable(sql_tableName);
		} else {
			// verify table version
			ResultSet table = sqlConnection.getColumns(sql_tableName);
			boolean hasShopKey = false;
			for (; table.next();) {
				if (table.getString("COLUMN_NAME").equalsIgnoreCase("Shop")) {
					hasShopKey = true;
				} else if (table.getString("COLUMN_NAME").equalsIgnoreCase("Sub")
						&& table.getString("TYPE_NAME").equalsIgnoreCase("TINYINT")) {
					// new ver. uses int for both
					sqlConnection.runUpdate("ALTER TABLE `" + sql_tableName + "` "
							+ "MODIFY COLUMN `Sub` INT(11) UNSIGNED NOT NULL;");
				} else if (table.getString("COLUMN_NAME").equalsIgnoreCase("NAME")
						&& !table.getBoolean("IS_NULLABLE") && table.getString("COLUMN_DEF") == null) {
					// modify to allow null default for deprecated entry 'name'
					sqlConnection.runUpdate("ALTER TABLE `" + sql_tableName + "` "
							+ " MODIFY COLUMN `NAME` VARCHAR(" + table.getString("COLUMN_SIZE") + ")");
				}
			}
			if (!hasShopKey) {
				sqlConnection.runUpdate(
						"ALTER TABLE `" + sql_tableName + "` ADD COLUMN `Shop` VARCHAR(" + MAX_SHOPLEN + ") NOT NULL FIRST, "
						+ "DROP PRIMARY KEY, "
						+ "ADD PRIMARY KEY  USING BTREE(`Shop`, `ID`, `SUB`);");
			}
		}
	}
	
	public void disconnect() {
		flushSave();
		sqlConnection.disconnect();
		sqlConnection = null;
	}

	private void verifyConnection() {
		if (sqlConnection == null
				|| !plugin.config.sql_database.equals(sqlConnection.getDatabaseName())
				|| !plugin.config.sql_username.equals(sqlConnection.getUserName())
				|| !plugin.config.sql_username.equals(sqlConnection.getUserName())
				|| !plugin.config.sql_password.equals(conn_pw)) {
			try {
				if (sqlConnection != null) {
					sqlConnection.disconnect();
				}
				sqlConnection.connect(plugin.config.sql_database, plugin.config.sql_username,
						plugin.config.sql_password, plugin.config.sql_hostName, plugin.config.sql_portNum);
				sql_tableName = plugin.config.sql_pricetable;
				conn_pw = plugin.config.sql_password;
			} catch (Exception ex) {
				plugin.getLogger().log(Level.SEVERE, "Error Re-Connecting to MySQL Database", ex);
				sqlConnection = null;
			}
		}
	}

	@Override
	public void initialize() {
		fullReload();
	}

	@Override
	protected void fullReload() {
		verifyConnection();
		if (sqlConnection == null) {
			return;
		}
		try {
			Integer validIDs[] = plugin.itemDB.getFullIdList();
			ResultSet table = sqlConnection.getQuery(
					"SELECT * FROM `" + sql_tableName + "`  ORDER BY ID, Sub;");
			clearPrices();
			while (table.next()) {
				int id = table.getInt("ID"),
						data = table.getInt("Sub");
				if (ArrayManip.indexOf(validIDs, (Integer) ((id << DATA_BYTE_LEN) + data)) != -1) {
					String shopName = table.getString("Shop");
					if (shopName.isEmpty()) {
						shopName = GLOBAL_IDENTIFIER;
					}
					Map<Integer, ItemPrice> pricelist = prices.get(shopName);
					Map<Integer, ItemPrice> pricelist2 = DBprices.get(shopName);
					if (pricelist == null) {
						pricelist = new HashMap<Integer, ItemPrice>();
						prices.put(shopName, pricelist);
						pricelist2 = new HashMap<Integer, ItemPrice>();
						DBprices.put(shopName, pricelist2);
					}
					pricelist.put((id << DATA_BYTE_LEN) + data,
							new ItemPrice(table.getDouble("buy"), table.getDouble("sell")));
					pricelist2.put((id << DATA_BYTE_LEN) + data,
							new ItemPrice(table.getDouble("buy"), table.getDouble("sell")));
				}
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Error executing SELECT on " + sql_tableName, ex);
		}
	}

	@Override
	protected void reloadItemPrice(String shop, int id, int data) {
		try {
			shop = safeShopName(shop);
			int idVal = (id << DATA_BYTE_LEN) + data;
			ResultSet value = sqlConnection.getQuery("SELECT * FROM `" + sql_tableName + "` "
					+ "WHERE Shop='" + shop + "' AND ID=" + id + " AND Sub=" + data + ";");
			if (value.next()) {
				if (!prices.containsKey(shop)) {
					prices.put(shop, new HashMap<Integer, ItemPrice>());
					DBprices.put(shop, new HashMap<Integer, ItemPrice>());
				}
				if (!prices.get(shop).containsKey(idVal)) {
					prices.get(shop).put(idVal, new ItemPrice(value.getDouble("buy"), value.getDouble("sell")));
				} else {
					prices.get(shop).get(idVal).set(value.getDouble("buy"), value.getDouble("sell"));
				}
				if (!DBprices.get(shop).containsKey(idVal)) {
					DBprices.get(shop).put(idVal, new ItemPrice(value.getDouble("buy"), value.getDouble("sell")));
				} else {
					DBprices.get(shop).get(idVal).set(value.getDouble("buy"), value.getDouble("sell"));
				}
			} else {
				if (prices.containsKey(shop)) {
					prices.get(shop).remove(idVal);
					DBprices.get(shop).remove(idVal);
				}
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Error executing SELECT on " + sql_tableName, ex);
		}
	}

	@Override
	public void saveFull() {
		try {
			// add new items, remove ones that are missing, update changes
			for (Map.Entry<String, Map<Integer, ItemPrice>> shop : prices.entrySet()) {
				boolean global = shop.getKey().equalsIgnoreCase(GLOBAL_IDENTIFIER);
				if (!DBprices.containsKey(shop.getKey())) {
					Map<Integer, ItemPrice> dbValues = new HashMap<Integer, ItemPrice>();
					DBprices.put(shop.getKey(), dbValues);
					// all values are new
					for (Map.Entry<Integer, ItemPrice> e : shop.getValue().entrySet()) {
						addValue(global ? "" : shop.getKey(), e.getKey(), e.getValue());
						dbValues.put(e.getKey(), e.getValue().clone());
					}
				} else {
					Map<Integer, ItemPrice> oldValues = DBprices.get(shop.getKey());
					for (int item : plugin.itemDB.getFullIdList()) {
						ItemPrice current = shop.getValue().get(item);
						ItemPrice dbValue = oldValues.get(item);
						if (current != null && dbValue == null) {
							// is new: add
							addValue(global ? "" : shop.getKey(), item, current);
							oldValues.put(item, current.clone());
						} else if (current == null && dbValue != null) {
							// no longer in list: remove
							removeValue(global ? "" : shop.getKey(), item);
							oldValues.remove(item);
						} else if (current != null && dbValue != null
								&& !(Numbers.equal(current.buyPrice, dbValue.buyPrice, .00001)
								&& Numbers.equal(current.sellPrice, dbValue.sellPrice, .00001))) {
							// in both, but the price has changed
							updateValue(global ? "" : shop.getKey(), item, current);
							dbValue.set(current);
						}
					}
				}
			}
			// now check for shops that were completely removed
			for (String shop : DBprices.keySet()) {
				if (!prices.containsKey(shop)) {
					removeShop(shop);
				}
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error Saving Database", e);
		}
	}

	protected void addValue(String shop, int idValue, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("INSERT INTO `" + sql_tableName + "`(Shop, ID, Sub, Buy, Sell) "
				+ "VALUES('" + safeShopName(shop) + "'," + (idValue >> DATA_BYTE_LEN) + "," + (idValue & DATA_BYTES)
				+ "," + price.buyPrice + "," + price.sellPrice + ");");
	}

	protected void addValue(String shop, int id, int data, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("INSERT INTO `" + sql_tableName + "`(Shop, ID, Sub, Buy, Sell) "
				+ "VALUES('" + safeShopName(shop) + "'," + id + "," + data
				+ "," + price.buyPrice + "," + price.sellPrice + ");");
	}

	protected void updateValue(String shop, int idValue, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("UPDATE `" + sql_tableName + "` SET Buy=" + price.buyPrice + " AND Sell=" + price.sellPrice
				+ " WHERE Shop='" + safeShopName(shop) + "' AND ID=" + (idValue >> DATA_BYTE_LEN) + " AND Sub=" + (idValue & DATA_BYTES) + ";");
	}

	protected void updateValue(String shop, int id, int data, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("UPDATE `" + sql_tableName + "` SET Buy=" + price.buyPrice + " AND Sell=" + price.sellPrice
				+ " WHERE Shop='" + safeShopName(shop) + "' AND ID=" + id + " AND Sub=" + data + ";");
	}

	protected void removeValue(String shop, int idValue) throws SQLException {
		sqlConnection.runUpdate("DELETE FROM `" + sql_tableName + "` "
				+ "WHERE Shop='" + safeShopName(shop) + "' AND ID=" + (idValue >> DATA_BYTE_LEN) + " AND Sub=" + (idValue & DATA_BYTES) + ";");
	}

	protected void removeValue(String shop, int id, int data) throws SQLException {
		sqlConnection.runUpdate("DELETE FROM `" + sql_tableName + "` "
				+ "WHERE Shop='" + safeShopName(shop) + "' AND ID=" + id + " AND Sub=" + data + ";");
	}

	protected void removeShop(String shop) throws SQLException {
		sqlConnection.runUpdate("DELETE FROM `" + sql_tableName + "` "
				+ "WHERE Shop='" + safeShopName(shop) + "';");
	}

	private boolean createPricelistTable(String tableName) throws SQLException {
		if (!sqlConnection.isConnected() || tableName.contains(" ")) {
			return false;
		}
		try {
			// "CREATE TABLE `" + sqlConnection.getDatabaseName() + "`.`" + tableName + "`"
			sqlConnection.runUpdate("CREATE TABLE `" + tableName + "`("
					+ "Shop  VARCHAR(" + MAX_SHOPLEN + ") NOT NULL,"
					+ "ID    INTEGER UNSIGNED NOT NULL,"
					+ "Sub   INTEGER UNSIGNED NOT NULL,"
					+ "Buy   DECIMAL(11,2),"
					+ "Sell  DECIMAL(11,2),"
					+ "PRIMARY KEY (Shop, ID, Sub));");
		} catch (SQLException e) {
			throw new SQLException("Error while creating table", e);
		}
		return true;
	}

	@Override
	protected void clearPrices() {
		for (String shop : prices.keySet()) {
			prices.get(shop).clear();
		}
		for (String shop : DBprices.keySet()) {
			DBprices.get(shop).clear();
		}
		prices.clear();
		DBprices.clear();
	}
}
