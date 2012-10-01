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

public class SettingsManager {
	
	protected final BetterShop3 plugin;
	public String locale = "en";
	public double econ_internal_startAmount = 50;
	// discount permissions groups
	HashMap<String, Double> econ_discountGroups = new HashMap<String, Double>();
	// if bank is to be used, what bank to use for global
	public String econ_bankName = "",
			econ_currency_s = "Dollar",
			econ_currency_m = "Dollars",
			econ_currency_minor_s = "Cent",
			econ_currency_minor_m = "Cents";
	public boolean econ_currency_multi = false;
	
	protected SettingsManager(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	void load() {
		
	}
}
