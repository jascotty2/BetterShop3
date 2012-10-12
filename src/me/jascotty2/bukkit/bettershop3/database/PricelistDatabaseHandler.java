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

import java.util.HashMap;
import java.util.Map;
import me.jascotty2.bukkit.bettershop3.BetterShop3;

public abstract class PricelistDatabaseHandler {

	protected final BetterShop3 plugin;
	/**
	 * internal save of the shop database
	 */
	protected final HashMap<String, HashMap<Integer, ItemPrice>> prices = new HashMap<String, HashMap<Integer, ItemPrice>>();
	/**
	 * what the global shop is identified in the database as
	 */
	protected final String GLOBAL_IDENTIFIER = "__global__";

	public PricelistDatabaseHandler(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	/**
	 * initialize the database
	 */
	protected abstract void initialize();

	/**
	 * reload the full db
	 */
	protected abstract void fullReload();

	/**
	 * for a dynamic pricelist, reload the price of this item
	 * @param id
	 * @param data 
	 */
	protected abstract void reloadItemPrice(String shop, int id, int data);
	
	/**
	 * save the database
	 */
	public abstract void save();

	public Map<Integer, ItemPrice> getPricelist(String shop) {
		return prices.get(shop == null ? GLOBAL_IDENTIFIER : shop);
	}
}
