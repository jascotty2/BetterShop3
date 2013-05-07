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
import me.jascotty2.bukkit.bettershop3.ItemValue;
import me.jascotty2.libv2.mysql.MySQL;
import me.jascotty2.libv2.util.ArrayManip;
import me.jascotty2.libv2.util.Numbers;

public class MySQL_Database extends PricelistDatabaseHandler {

	private String sql_tableName = "pricelist", conn_pw;
	private MySQL sqlConnection = null;
	/**
	 * local copy of the database
	 */
	protected final HashMap<String, Map<ItemValue, ItemPrice>> DBprices = new HashMap<String, Map<ItemValue, ItemPrice>>();

	public MySQL_Database(BetterShop3 plugin) throws Exception {
		super(plugin);
		sqlConnection = new MySQL(plugin.config.sql_database, plugin.config.sql_username,
				plugin.config.sql_password, plugin.config.sql_hostName, plugin.config.sql_portNum);
		sqlConnection.connect();
		if (!sqlConnection.isConnected()) {
			throw new Exception("Cannot connect to MySQL Server");
		}
		sql_tableName = plugin.config.sql_pricetable;
		conn_pw = plugin.config.sql_password;
		if (!sqlConnection.tableExists(sql_tableName)) {
			// table does not exist, so create it
			createPricelistTable(sql_tableName);
		} else {
			// verify table version
			ResultSet table = sqlConnection.getColumns(sql_tableName);
			boolean hasShopKey = false, hasStock = false;
			for (; table.next();) {
				if (table.getString("COLUMN_NAME").equalsIgnoreCase("Shop")) {
					hasShopKey = true;
				} else if (table.getString("COLUMN_NAME").equalsIgnoreCase("Stock")) {
					hasStock = true;
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
			if (!hasStock) {
				sqlConnection.runUpdate(
						"ALTER TABLE `" + sql_tableName + "` ADD COLUMN `Stock` INT(19) NOT NULL;");
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
			ItemValue validIDs[] = plugin.itemDB.getFullIdList();
			ResultSet table = sqlConnection.getQuery(
					"SELECT * FROM `" + sql_tableName + "` ORDER BY ID, Sub;");
			clearPrices();
			while (table.next()) {
				ItemValue idv = new ItemValue(table.getInt("ID"), table.getInt("Sub"));
				if (ArrayManip.indexOf(validIDs, idv) != -1) {
					String shopName = table.getString("Shop");
					if (shopName.isEmpty()) {
						shopName = GLOBAL_IDENTIFIER;
					}
					Map<ItemValue, ItemPrice> pricelist = prices.get(shopName);
					Map<ItemValue, ItemPrice> pricelist2 = DBprices.get(shopName);
					if (pricelist == null) {
						pricelist = new HashMap<ItemValue, ItemPrice>();
						prices.put(shopName, pricelist);
						pricelist2 = new HashMap<ItemValue, ItemPrice>();
						DBprices.put(shopName, pricelist2);
					}
					pricelist.put(idv, new ItemPrice(idv, table.getDouble("buy"), table.getDouble("sell")));
					pricelist2.put(idv, new ItemPrice(idv, table.getDouble("buy"), table.getDouble("sell")));
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
			ItemValue idv = new ItemValue(id, data);
			ResultSet value = sqlConnection.getQuery("SELECT * FROM `" + sql_tableName + "` "
					+ "WHERE Shop='" + shop + "' AND ID=" + id + " AND Sub=" + data + ";");
			if (value.next()) {
				if (!prices.containsKey(shop)) {
					prices.put(shop, new HashMap<ItemValue, ItemPrice>());
					DBprices.put(shop, new HashMap<ItemValue, ItemPrice>());
				}
				if (!prices.get(shop).containsKey(idv)) {
					prices.get(shop).put(idv, new ItemPrice(id, (short) data, value.getDouble("buy"), value.getDouble("sell"), 
							value.getLong("Stock")));
				} else {
					prices.get(shop).get(idv).set(value.getDouble("buy"), value.getDouble("sell"), 
							value.getLong("Stock"));
				}
				if (!DBprices.get(shop).containsKey(idv)) {
					DBprices.get(shop).put(idv, new ItemPrice(id, (short) data, value.getDouble("buy"), value.getDouble("sell"), 
							value.getLong("Stock")));
				} else {
					DBprices.get(shop).get(idv).set(value.getDouble("buy"), value.getDouble("sell"), 
							value.getLong("Stock"));
				}
			} else {
				if (prices.containsKey(shop)) {
					prices.get(shop).remove(idv);
					DBprices.get(shop).remove(idv);
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
			for (Map.Entry<String, Map<ItemValue, ItemPrice>> shop : prices.entrySet()) {
				boolean global = shop.getKey().equalsIgnoreCase(GLOBAL_IDENTIFIER);
				if (!DBprices.containsKey(shop.getKey())) {
					Map<ItemValue, ItemPrice> dbValues = new HashMap<ItemValue, ItemPrice>();
					DBprices.put(shop.getKey(), dbValues);
					// all values are new
					for (Map.Entry<ItemValue, ItemPrice> e : shop.getValue().entrySet()) {
						addValue(global ? "" : shop.getKey(), e.getKey(), e.getValue());
						dbValues.put(e.getKey(), e.getValue().clone());
					}
				} else {
					Map<ItemValue, ItemPrice> oldValues = DBprices.get(shop.getKey());
					for (ItemValue item : plugin.itemDB.getFullIdList()) {
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

	protected void addValue(String shop, ItemValue idv, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("INSERT INTO `" + sql_tableName + "`(Shop, ID, Sub, Buy, Sell) "
				+ "VALUES('" + safeShopName(shop) + "'," + idv.id + "," + idv.data
				+ "," + price.buyPrice + "," + price.sellPrice + ");");
	}

	protected void addValue(String shop, int id, int data, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("INSERT INTO `" + sql_tableName + "`(Shop, ID, Sub, Buy, Sell) "
				+ "VALUES('" + safeShopName(shop) + "'," + id + "," + data
				+ "," + price.buyPrice + "," + price.sellPrice + ");");
	}

	protected void updateValue(String shop, ItemValue idv, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("UPDATE `" + sql_tableName + "` SET Buy=" + price.buyPrice + " AND Sell=" + price.sellPrice
				+ " WHERE Shop='" + safeShopName(shop) + "' AND ID=" + idv.id + " AND Sub=" + idv.data + ";");
	}

	protected void updateValue(String shop, int id, int data, ItemPrice price) throws SQLException {
		sqlConnection.runUpdate("UPDATE `" + sql_tableName + "` SET Buy=" + price.buyPrice + " AND Sell=" + price.sellPrice
				+ " WHERE Shop='" + safeShopName(shop) + "' AND ID=" + id + " AND Sub=" + data + ";");
	}

	protected void removeValue(String shop, ItemValue idv) throws SQLException {
		sqlConnection.runUpdate("DELETE FROM `" + sql_tableName + "` "
				+ "WHERE Shop='" + safeShopName(shop) + "' AND ID=" + idv.id + " AND Sub=" + idv.data + ";");
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
