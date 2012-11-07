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
package me.jascotty2.bukkit.bettershop3;

import java.util.HashMap;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.enums.PricelistType;
import me.jascotty2.libv2.util.Str;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingsManager {

	protected final BetterShop3 plugin;
	public String locale = "en";
	public double econ_internal_startAmount = 50;
	// discount permissions groups
	HashMap<String, Double> econ_discountGroups = new HashMap<String, Double>();
	public String econ_bankName = "", // if bank is to be used, what bank to use for global
			econ_currency_s = "Dollar", // singular
			econ_currency_m = "Dollars", // multiple
			econ_currency_minor_s = "Cent",
			econ_currency_minor_m = "Cents";
	public boolean econ_currency_multi = false; // if internal currency formatting should be seperated (eg. 2 Dollars 25 Cents)
	public String sql_username = "root",
			sql_password = "root",
			sql_database = "minecraft",
			sql_pricetable = "pricelist",
			sql_hostName = "localhost",
			sql_portNum = "3306";
	public PricelistType pricelist_type = PricelistType.CSV,
			pricelist_type_default = PricelistType.CSV;
	
	protected SettingsManager(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	void load() {

		try {
			FileConfiguration config = plugin.getConfig();
			// if any important setting found missing, what they were
			//	(will be more lenient than last BetterShop.. 
			//		only check those that have large influence on plugin operation)
			String missing = "";
			// add as such: if(!config.contains("node")) missing += (missing.isEmpty() ? "" : ", ") + "node";
			// non-categorial settings
			locale = config.getString("Language", locale);
			// sub-settings
			Object node = config.get("Database");
			if (node instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				String type = n.getString("Type");
				if(type != null) {
					if(Str.isInIgnoreCase(type, "MySQL", "SQL")) {
						pricelist_type = PricelistType.MYSQL;
					} else if(Str.isInIgnoreCase(type, "YAML", "YML")) {
						pricelist_type = PricelistType.YAML;
					} else if(type.equalsIgnoreCase("CSV")) {
						pricelist_type = PricelistType.CSV;
					} else {
						plugin.getLogger().warning(String.format("Unknown type '%s' (Defaulting to %s)", type, pricelist_type_default.name()));
						pricelist_type = pricelist_type_default;
					}
				} else {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.Type";
				}
				sql_database = n.getString("SQL_Database", sql_database);
				if(pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Database")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Database";
				}
				sql_username = n.getString("SQL_Username", sql_username);
				if(pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Username")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Username";
				}
				sql_password = n.getString("SQL_Password", sql_password);
				if(pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Password")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Password";
				}
				sql_pricetable = n.getString("SQL_PriceTable", sql_pricetable);
				sql_hostName = n.getString("SQL_HostName", sql_hostName);
				sql_portNum = n.getString("SQL_PortNum", sql_portNum);
			} else {
				missing += (missing.isEmpty() ? "" : ", ") + "Database.*";
			}
			
			node = config.get("Economy");
			if (node instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				econ_bankName = n.getString("Bank Name", econ_bankName);
				econ_currency_s = n.getString("Currency", econ_currency_s);
				econ_currency_m = n.getString("Currency Plural", econ_currency_m);
				econ_currency_minor_s = n.getString("Currency Minor", econ_currency_minor_s);
				econ_currency_minor_m = n.getString("Currency Minor Plural", econ_currency_minor_m);
			}

			if (missing.length() > 0) {
				plugin.getLogger().warning(String.format("Missing Configuration Nodes: \n%s", missing));
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error Loading Config", e);
		}
	}
}
