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

	protected SettingsManager(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	void load() {

		try {
			FileConfiguration config = plugin.getConfig();
			config.load(FileManager.configFile);
			// if any important setting found missing, what they were
			//	(will be more lenient than last BetterShop.. 
			//		only check those that have large influence on plugin operation)
			String missing = "";
			// add as such: if(!config.contains("node")) missing += (missing.isEmpty() ? "" : ", ") + "node";
			// non-categorial settings
			locale = config.getString("Language", locale);
			// sub-settings
			Object node = config.get("Economy");
			if (node instanceof MemorySection) {
				MemorySection n = (MemorySection) node;
				econ_bankName = n.getString("Bank Name", econ_bankName);
				econ_currency_s = n.getString("Currency", econ_currency_s);
				econ_currency_m = n.getString("Currency Plural", econ_currency_m);
				econ_currency_minor_s = n.getString("Currency Minor", econ_currency_minor_s);
				econ_currency_minor_m = n.getString("Currency Minor Plural", econ_currency_minor_m);
			}

			if (missing.length() > 0) {
				plugin.getLogger().warning("Missing Configuration Nodes: \n" + missing);
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error Loading Config", e);
		}
	}
}
