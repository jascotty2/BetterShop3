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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.FileManager;
import me.jascotty2.bukkit.bettershop3.ItemValue;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.io.FileIO;
import me.jascotty2.libv2.util.ArrayManip;

public class CSV_Database extends PricelistDatabaseHandler {

	File saveFile = null;

	public CSV_Database(BetterShop3 plugin) {
		super(plugin);
		saveFile = new File(FileManager.pluginDir, "pricelist.csv");
	}

	@Override
	public void initialize() {
		fullReload();
	}

	@Override
	protected void fullReload() {
		if (saveFile.exists()) {
			try {
				for (Map.Entry<String, Map<ItemValue, ItemPrice>> e : prices.entrySet()) {
					e.getValue().clear();
				}
				prices.clear();
				// reload
				List<String[]> file = FileIO.loadCSVFile(saveFile);
				ItemValue validIDs[] = plugin.itemDB.getFullIdList();
				HashMap<ItemValue, ItemPrice> pricelist = new HashMap<ItemValue, ItemPrice>();
				String shopName = GLOBAL_IDENTIFIER;
				for (String[] dataLine : file) {
					if (dataLine.length == 0) {
						continue;
					}
					if (!CheckInput.IsInt(dataLine[0])) {
						if (!pricelist.isEmpty()) {
							prices.put(shopName, pricelist);
							pricelist = new HashMap<ItemValue, ItemPrice>();
						}
						shopName = dataLine[0];
					} else if (dataLine.length >= 4) {
						ItemValue idv = new ItemValue(CheckInput.GetInt(dataLine[0], -1), dataLine[1].length() == 0 ? 0 : CheckInput.GetInt(dataLine[1], 0));
						if (idv.id > 0 && idv.data >= 0 && ArrayManip.indexOf(validIDs, idv) != -1) {
							pricelist.put(idv,
									new ItemPrice(idv, 
											CheckInput.GetDouble(dataLine[2], -1), CheckInput.GetDouble(dataLine[3], -1),
											dataLine.length > 4 ? CheckInput.GetLong(dataLine[4], -1) : -1));
						}
					}
				}
				if (!pricelist.isEmpty()) {
					prices.put(shopName, pricelist);
				}
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to reload the pricelist file", e);
			}
		}
	}

	@Override
	protected void reloadItemPrice(String shop, int id, int data) {
		if (saveFile.exists()) {
			_fullReload();
		}
	}

	@Override
	protected void saveFull() {
		try {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			FileWriter fstream = new FileWriter(saveFile.getAbsolutePath());
			BufferedWriter out = new BufferedWriter(fstream);
			for (Map.Entry<String, Map<ItemValue, ItemPrice>> e : prices.entrySet()) {
				out.write(e.getKey());
				out.newLine();
				for (Map.Entry<ItemValue, ItemPrice> p : e.getValue().entrySet()) {
					out.write(String.valueOf(p.getKey().id) + "," 
							+ String.valueOf(p.getKey().data) + "," 
							+ p.getValue().buyPrice + ","
							+ p.getValue().sellPrice + "\n");
				}
			}
			out.close();
			fstream.close();
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save the pricelist file", e);
		}
	}
}
