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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.bukkit.bettershop3.database.PricelistDatabaseHandler;
import me.jascotty2.bukkit.bettershop3.enums.GlobalShopMode;
import me.jascotty2.bukkit.bettershop3.enums.PricelistType;
import me.jascotty2.bukkit.bettershop3.enums.SpoutCategoryMethod;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.io.FileIO;
import me.jascotty2.libv2.util.Str;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {

	protected final BetterShop3 plugin;
	//
	// General Shop Settings
	//
	public String locale = "en";
	public boolean publicMarket = false,
			useMaxStack = true,
			buybacktools = true;
	public int itemsPerPage = 9;
	//
	// Economy
	//
	public double econ_internal_startAmount = 50;
	// discount permissions groups
	HashMap<String, Double> econ_discountGroups = new HashMap<String, Double>();
	public String econ_bankName = "", // if bank is to be used, what bank to use for global
			econ_currency_s = "Dollar", // singular
			econ_currency_m = "Dollars", // multiple
			econ_currency_minor_s = "Cent",
			econ_currency_minor_m = "Cents";
	public boolean econ_currency_multi = false; // if internal currency formatting should be seperated (eg. 2 Dollars 25 Cents)
	//
	// Pricelist Database 
	//
	public PricelistType pricelist_type = PricelistType.CSV,
			pricelist_type_default = PricelistType.CSV;
	public String sql_username = "root",
			sql_password = "root",
			sql_database = "minecraft",
			sql_pricetable = "pricelist",
			sql_hostName = "localhost",
			sql_portNum = "3306";
	public ArrayList<Integer> customSortIDValues = null;
	public final HashMap<String, ArrayList<Integer>> categories = new HashMap<String, ArrayList<Integer>>();
	public boolean sortByCategories = true;
	//
	// Item Stock
	//
	public boolean stock_useStock = false,
			stock_onlyGlobal = false,
			stock_noOverStock = true,
			stock_restockLower = false;
	public long stock_default = 200,
			stock_maxStock = 500,
			stock_restockInterval = 21600000; //6h;
	public final HashMap<Integer, Long> stock_defaults = new HashMap<Integer, Long>();
	//
	// Region Shops
	//
	public boolean region_useRegions = true;
	public GlobalShopMode region_globalMode = GlobalShopMode.GLOBAL;
	//
	// SpoutPlugin Interface
	//
	public boolean spout_enabled = true;
	public String spout_popupKey = "B";
	public boolean spout_largeMenu = true,
			spout_usePages = false,
			spout_useScroll = false;
	public SpoutCategoryMethod spout_categories = SpoutCategoryMethod.NONE;

	protected SettingsManager(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	private void loadDefault(FileConfiguration config) {

		YamlConfiguration defaultConf = null;
		//String updated = "";
		try {
			File jarFile = FileIO.getJarFile(SettingsManager.class);
			URL res = SettingsManager.class.getResource("/config.yml");
			if (res == null) {
				throw new java.io.FileNotFoundException("Could not find '/config.yml' in " + jarFile.getAbsolutePath());
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			InputStream input = resConn.getInputStream();

			if (input == null) {
				throw new java.io.IOException("can't get input stream from " + res);
			} else {
				defaultConf = YamlConfiguration.loadConfiguration(input);
			}
			input.close();
		} catch (Exception ex) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load default config", ex);
			return;
		}
		String unused = "", missing = "";
		// first check for config that's not in default
		Set<String> confKeys = config.getKeys(false);
		LinkedHashSet<String> badNodes = new LinkedHashSet<String>();
		Set<String> confKeys2 = config.getKeys(true);
		for (String k : confKeys2) {
			if (!defaultConf.contains(k)) {
				String test = k;
				while (test.contains(".")) {
					test = test.substring(0, test.indexOf('.'));
					if (badNodes.contains(test)) {
						k = null;
						break;
					}
				}
				if (k != null) {
					badNodes.add(k);
					unused += (unused.isEmpty() ? "" : ", ") + k + (config.get(k) instanceof MemorySection ? ".*" : "");
				}
				//plugin.getLogger().info(String.format("Unused config: %s.*", k));
			}
		}
		badNodes.clear();
		// check for config that's not in config
		for (String k : defaultConf.getKeys(false)) {
			if (!confKeys.contains(k)) {
				badNodes.add(k);
				missing += (missing.isEmpty() ? "" : ", ") + k + ".*";
				//plugin.getLogger().info(String.format("missing config: %s.*", k));
				if (defaultConf.get(k) instanceof MemorySection) {
					MemorySection s = (MemorySection) defaultConf.get(k);
					config.createSection(k);
					MemorySection s2 = (MemorySection) config.get(k);
					for (String k2 : s.getKeys(true)) {
						Object v = s.get(k2);
						if (v instanceof MemorySection) {
							s2.createSection(k2);
						} else {
							s2.set(k2, v);
						}
					}
				}
			} else {
				if (defaultConf.get(k) instanceof MemorySection) {
					MemorySection s = (MemorySection) defaultConf.get(k);
					if (!(config.get(k) instanceof MemorySection)) {
						missing += (missing.isEmpty() ? "" : ", ") + k + ".*";
						badNodes.add(k);
						config.set(k, null);
						config.createSection(k);
					}
					MemorySection s2 = (MemorySection) config.get(k);
					for (String k2 : s.getKeys(true)) {
						Object v = s.get(k2);
						Object v2 = s2.get(k2);
						if (v2 == null || (v instanceof MemorySection && !(v2 instanceof MemorySection))) {
							String test = k2;
							while (test.contains(".")) {
								test = test.substring(0, test.indexOf('.'));
								if (badNodes.contains(test)) {
									test = null;
									break;
								}
							}
							if (test != null) {
								badNodes.add(k);
								missing += (missing.isEmpty() ? "" : ", ") + k + (config.get(k) instanceof MemorySection ? ".*" : "");
							}
							if (v instanceof MemorySection) {
								s2.createSection(k2);
							} else {
								s2.set(k2, v);
							}
						}
					}
				}
			}
		}
		if (!unused.isEmpty()) {
			plugin.getLogger().info(String.format("-CONFIG- Unused config: %s", unused));
		}
		if (!missing.isEmpty()) {
			plugin.getLogger().info(String.format("-CONFIG- Missing config: %s", missing));
			plugin.getLogger().info("-CONFIG- Updating Config with new items..");
			try {
				config.save(new File(plugin.getDataFolder(), "config.yml"));
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to save config", ex);
			}
		}
	}

	void load() {
		try {
			FileConfiguration config = plugin.getConfig();

			// load default config from jar, and update new values
			loadDefault(config);

			// if any important setting found missing, what they were
			//	(will be more lenient than last BetterShop.. 
			//		only check those that have large influence on plugin operation)
			String missing = "";
			// add as such: if(!config.contains("node")) missing += (missing.isEmpty() ? "" : ", ") + "node";
			// non-categorial settings
			locale = config.getString("Language", locale);
			// sub-settings
			Object node = config.get("Shop");
			if (node instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				itemsPerPage = n.getInt("Items Per Page", itemsPerPage);
				publicMarket = n.getBoolean("Public Market", publicMarket);
				useMaxStack = n.getBoolean("Use Max Stack", useMaxStack);
				buybacktools = n.getBoolean("Buy Back Tools", buybacktools);

				// this is modified after item names are loaded
				if (customSortIDValues != null) {
					customSortIDValues.clear();
					customSortIDValues = null;
				}

				sortByCategories = n.getBoolean("Sort Categories First", sortByCategories);
			}

			if ((node = config.get("Database")) instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				String type = n.getString("Type");
				if (type != null) {
					if (Str.isInIgnoreCase(type, "MySQL", "SQL")) {
						pricelist_type = PricelistType.MYSQL;
					} else if (Str.isInIgnoreCase(type, "YAML", "YML")) {
						pricelist_type = PricelistType.YAML;
					} else if (type.equalsIgnoreCase("CSV")) {
						pricelist_type = PricelistType.CSV;
					} else {
						plugin.getLogger().warning(String.format("-CONFIG- Unknown database type '%s' (Defaulting to %s)", type, pricelist_type_default.name()));
						pricelist_type = pricelist_type_default;
					}
				} else {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.Type";
				}
				sql_database = n.getString("SQL_Database", sql_database);
				if (pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Database")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Database";
				}
				sql_username = n.getString("SQL_Username", sql_username);
				if (pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Username")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Username";
				}
				sql_password = n.getString("SQL_Password", sql_password);
				if (pricelist_type == PricelistType.MYSQL && !n.contains("SQL_Password")) {
					missing += (missing.isEmpty() ? "" : ", ") + "Database.SQL_Password";
				}
				sql_pricetable = n.getString("SQL_PriceTable", sql_pricetable);
				sql_hostName = n.getString("SQL_HostName", sql_hostName);
				sql_portNum = n.getString("SQL_PortNum", sql_portNum);
			} else {
				missing += (missing.isEmpty() ? "" : ", ") + "Database.*";
			}

			if ((node = config.get("Economy")) instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				econ_bankName = n.getString("Bank Name", econ_bankName);
				econ_internal_startAmount = n.getDouble("Internal Start Amount", econ_internal_startAmount);
				econ_currency_multi = n.getBoolean("Internal Uses Decimal", econ_currency_multi);
				econ_currency_s = n.getString("Currency", econ_currency_s);
				econ_currency_m = n.getString("Currency Plural", econ_currency_m);
				econ_currency_minor_s = n.getString("Currency Minor", econ_currency_minor_s);
				econ_currency_minor_m = n.getString("Currency Minor Plural", econ_currency_minor_m);
			}

			if ((node = config.get("Stock")) instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				stock_useStock = n.getBoolean("Enabled", stock_useStock);
				stock_onlyGlobal = n.getBoolean("Only for Global", stock_onlyGlobal);
				stock_noOverStock = n.getBoolean("No Overstock", stock_noOverStock);
				stock_maxStock = n.getLong("Max Stock", stock_maxStock);
				stock_restockLower = n.getBoolean("Restock Lower", stock_restockLower);
				String res = n.getString("Restock Timeout");
				if (res != null) {
					stock_restockInterval = CheckInput.GetTimeSpanInSec(res, 'h', stock_restockInterval);
				}
				stock_defaults.clear();
				if ((node = n.get("Start Amount")) instanceof MemorySection) {
					n = (MemorySection) node;
					stock_default = n.getLong("default", stock_default);
					for (String idStr : n.getKeys(false)) {
						if (!idStr.equalsIgnoreCase("default")) {
							int id = 0, data = 0;
							if (CheckInput.IsInt(idStr)) {
								id = CheckInput.GetInt(idStr, 0);
							} else if (idStr.contains("-")) {
								id = CheckInput.GetInt(idStr.substring(0, idStr.indexOf("-")), 0);
								data = CheckInput.ExtractInteger(idStr.substring(idStr.indexOf("-") + 1), 0);
								if (id == 373) {
									// potion
									if (idStr.toLowerCase().contains("e")) {
										data += (1 << 6);
									}
									if (idStr.toLowerCase().contains("s")) {
										data += (1 << 14);
									}
								}
							}
							stock_defaults.put((id << PricelistDatabaseHandler.DATA_BYTE_LEN) + data, n.getLong(idStr));
						}
					}
				}
			}

			econ_discountGroups.clear();
			if ((node = config.get("DiscountGroups")) instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				for (String g : n.getKeys(false)) {
					double d = n.getDouble(g, 0) / 100.;
					econ_discountGroups.put(g, d > 1 ? 1 : (d < -1 ? -1 : d));
				}
			}

			if ((node = config.get("Spout")) instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				spout_enabled = n.getBoolean("Enabled", spout_enabled);
				spout_popupKey = n.getString("Key", spout_popupKey);
				spout_largeMenu = n.getBoolean("Large Menu", spout_largeMenu);
				spout_usePages = n.getBoolean("Use Pages", spout_usePages);
				String c = n.getString("Categories");
				if (c != null) {
					if (c.equalsIgnoreCase("cycle")) {
						spout_categories = SpoutCategoryMethod.CYCLE;
					} else if (c.equalsIgnoreCase("tab") || c.equalsIgnoreCase("tabbed")) {
						spout_categories = SpoutCategoryMethod.TABBED;
					} else {
						spout_categories = SpoutCategoryMethod.NONE;
					}
				}
				// custom value, not in config by default
				spout_useScroll = n.getBoolean("Single Item Scroll", spout_useScroll);
			}

			for (ArrayList<Integer> cat : categories.values()) {
				cat.clear();
			}
			categories.clear();
			if (missing.length() > 0) {
				plugin.getLogger().warning(String.format("Missing Configuration Nodes: \n%s", missing));
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error Loading Config", e);
		}
	}

	public void updateSortIDs(ItemLookupTable itemDB) {
		// first add categories
		for (ArrayList<Integer> cat : categories.values()) {
			cat.clear();
		}
		categories.clear();
		String[] customSortItems = null;
		Object cats = plugin.getConfig().get("Item Categories");
		if (cats instanceof MemorySection) {
			MemorySection n = (MemorySection) cats;
			for (String cat : n.getKeys(false)) {
				String bad = "";
				customSortItems = null;
				Object items = n.get(cat);
				if (items instanceof List) {
					customSortItems = new String[((List) items).size()];
					int i = 0;
					for (Object o : (List) items) {
						customSortItems[i++] = o.toString();
					}
				} else if (items != null) {
					customSortItems = items.toString().split(",");
				}
				if (customSortItems != null) {
					ArrayList<Integer> catItems = new ArrayList<Integer>();
					for (String item : customSortItems) {
						ItemValue itv = itemDB.getItem(item);
						if (itv == null) {
							bad += (bad.length() > 0 ? ", " : "") + item;
						} else {
							catItems.add(itv.toIDVal());
						}
					}
					if (!bad.isEmpty()) {
						plugin.getLogger().warning(String.format("Skipping Unknown value%s in Item Categories.%s: %s", bad.contains(",") ? "s" : "", cat, bad));
					}
					categories.put(cat.toLowerCase(), catItems);
				} else {
					plugin.getLogger().warning(String.format("Skipping Unknown Category in Item Categories: %s", cat));
				}
			}
		}
		ArrayList<Integer> sortOrder = new ArrayList<Integer>();
		Object sort = plugin.getConfig().get("Shop.Custom Sort");
		if (sort instanceof List) {
			customSortItems = new String[((List) sort).size()];
			int i = 0;
			for (Object o : (List) sort) {
				customSortItems[i++] = o.toString();
			}
		} else if (sort != null) {
			customSortItems = sort.toString().split(",");
		}
		ArrayList<String> usedCats = new ArrayList<String>();
		if (customSortItems != null) {
			String bad = "";
			for (String item : customSortItems) {
				item = item.trim();
				ItemValue itv = itemDB.getItem(item);
				if (itv == null) {
					bad += (bad.length() > 0 ? ", " : "") + item;
				} else {
					// if is not an exact match, check if is a category
					if (!(CheckInput.IsInt(item) || item.contains(":"))
							&& !Str.containsIgnoreCase(itemDB.itemNames.get(itv), item)) {
						if (Str.containsIgnoreCase(categories.keySet(), item)) {
							sortOrder.addAll(categories.get(item.toLowerCase()));
							usedCats.add(item.toLowerCase());
						} else {
							sortOrder.add(itv.toIDVal());
						}
					} else {
						sortOrder.add(itv.toIDVal());
					}
				}
			}
			if (!bad.isEmpty()) {
				plugin.getLogger().warning(String.format("Skipping Unknown value%s in Shop.CustomSort: %s", bad.contains(",") ? "s" : "", bad));
			}
		}
		if (sortByCategories) {
			for (String cat : categories.keySet()) {
				if (!usedCats.contains(cat)) {
					sortOrder.addAll(categories.get(cat));
				}
			}
		}
		// sanity check - one instance of each item allowed
		if (customSortIDValues == null) {
			customSortIDValues = new ArrayList<Integer>();
		} else {
			customSortIDValues.clear();
		}

		for (int val : sortOrder) {
			if (!customSortIDValues.contains(val)) {
				customSortIDValues.add(val);
			}
		}

		itemDB.reorderSortedIds(customSortIDValues);
	}

	public long getDefaultStock(int id) {
		Long def;
		if ((id & PricelistDatabaseHandler.ID_BYTES) == 0) {
			def = stock_defaults.get(id << PricelistDatabaseHandler.DATA_BYTE_LEN);
		} else {
			def = stock_defaults.get(id);
		}
		return def == null ? stock_default : def;
	}

	public long getDefaultStock(int id, int data) {
		Long def = stock_defaults.get((id << PricelistDatabaseHandler.DATA_BYTE_LEN) + data);
		return def == null ? stock_default : def;
	}
}
