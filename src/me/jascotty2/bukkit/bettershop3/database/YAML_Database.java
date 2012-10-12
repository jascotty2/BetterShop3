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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.FileManager;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAML_Database extends PricelistDatabaseHandler {

	File saveFile = null;
	FileConfiguration db = null;

	public YAML_Database(BetterShop3 plugin) {
		super(plugin);
		saveFile = new File(FileManager.pluginDir, "pricelist.yml");
	}

	@Override
	public void initialize() {
		if (saveFile.exists()) {
			try {
				// attempt to load
				db = YamlConfiguration.loadConfiguration(saveFile);
				load();
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to load the pricelist config", e);
			}
		}
	}

	protected void load() {
		for (Map.Entry<String, Object> val : db.getValues(false).entrySet()) {
			if (val.getValue() instanceof MemorySection) {
				MemorySection m = (MemorySection) val.getValue();
				HashMap<Integer, ItemPrice> pricelist = new HashMap<Integer, ItemPrice>();
				String itemN, itemN_s;
				for (int item : plugin.itemDB.getFullIdList()) {
					itemN = itemN_s = String.valueOf(item >> 16);
					itemN_s += "-" + String.valueOf(item & 65535);
					if ((item & 65535) == 0 && (m.contains(itemN + "_buy") || m.contains(itemN + "_sell"))) {
						ItemPrice p = new ItemPrice();
						p.buyPrice = m.getDouble(itemN + "_buy", -1);
						p.sellPrice = m.getDouble(itemN + "_sell", -1);
						pricelist.put(item, p);
					} else if (m.contains(itemN_s + "_buy") || m.contains(itemN_s + "_sell")) {
						ItemPrice p = new ItemPrice();
						p.buyPrice = m.getDouble(itemN_s + "_buy", -1);
						p.sellPrice = m.getDouble(itemN_s + "_sell", -1);
						pricelist.put(item, p);
					}
				}
				prices.put(val.getKey(), pricelist);
			}
		}
	}

	@Override
	protected void fullReload() {
		try {
			for (Map.Entry<String, HashMap<Integer, ItemPrice>> e : prices.entrySet()) {
				e.getValue().clear();
			}
			prices.clear();
			// reload
			db.load(saveFile);
			load();
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to reload the pricelist config", e);
		}
	}

	@Override
	protected void reloadItemPrice(String shop, int id, int data) {
		if (saveFile.exists()) {
			if (shop == null) {
				shop = GLOBAL_IDENTIFIER;
			}
			try {
				// reload
				db.load(saveFile);
				Object section = db.get(shop);
				if (section instanceof MemorySection) {
					MemorySection m = (MemorySection) section;
					if (!prices.containsKey(shop)) {
						prices.put(shop, new HashMap<Integer, ItemPrice>());
					}
					String itemN, itemN_s;
					itemN = itemN_s = String.valueOf(id);
					itemN_s += "-" + String.valueOf(data);
					ItemPrice p = prices.get(shop).get(id << 16 + data);
					if (p == null) {
						p = new ItemPrice();
					}
					if (data == 0 && (m.contains(itemN + "_buy") || m.contains(itemN + "_sell"))) {
						p.buyPrice = m.getDouble(itemN + "_buy", -1);
						p.sellPrice = m.getDouble(itemN + "_sell", -1);
					} else if (m.contains(itemN_s + "_buy") || m.contains(itemN_s + "_sell")) {
						p.buyPrice = m.getDouble(itemN_s + "_buy", -1);
						p.sellPrice = m.getDouble(itemN_s + "_sell", -1);
					}
					prices.get(shop).put(id << 16 + data, p);
				}
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to reload the pricelist config", e);
			}
		}
	}

	@Override
	public void save() {
		try {
			for (Map.Entry<String, HashMap<Integer, ItemPrice>> e : prices.entrySet()) {
				for (Map.Entry<Integer, ItemPrice> p : e.getValue().entrySet()) {
					String itemN = String.valueOf(p.getKey() >> 16);
					if ((p.getKey() & 65535) != 0) {
						itemN += String.valueOf(p.getKey() & 65535);
					}
					if (p.getValue().buyPrice >= 0) {
						db.set(e.getKey() + "." + itemN + "_buy", p.getValue().buyPrice);
					} else if (db.contains(e.getKey() + "." + itemN + "_buy")) {
						db.set(e.getKey() + "." + itemN + "_buy", -1);
					}
					if (p.getValue().sellPrice >= 0) {
						db.set(e.getKey() + "." + itemN + "_sell", p.getValue().sellPrice);
					} else if (db.contains(e.getKey() + "." + itemN + "_sell")) {
						db.set(e.getKey() + "." + itemN + "_sell", -1);
					}

				}
			}
			db.save(saveFile);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save the pricelist config", e);
		}
	}
}
