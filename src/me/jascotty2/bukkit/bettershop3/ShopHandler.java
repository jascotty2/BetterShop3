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

import me.jascotty2.bukkit.bettershop3.database.PricelistDatabaseHandler;
import me.jascotty2.bukkit.bettershop3.database.Shop;
import me.jascotty2.bukkit.bettershop3.enums.GlobalShopMode;
import org.bukkit.Location;

public class ShopHandler {

	PricelistDatabaseHandler shop;
	final protected BetterShop3 plugin;

	public ShopHandler(BetterShop3 plugin) {
		this.plugin = plugin;
	}
	
	public boolean globalEnabled(Location l) {
		if(!plugin.config.region_useRegions) {
			return true;
		} else if (plugin.config.region_globalMode == GlobalShopMode.NONE) {
			return false;
		}
		// todo: 
		// return plugin.config.region_globalMode == GlobalShopMode.GLOBAL || (location in a global shop region);
		return plugin.config.region_globalMode == GlobalShopMode.GLOBAL;
	}
	
	public Shop getRegionShop(Location l) {
		if(!plugin.config.region_useRegions) {
			return new Shop(null, plugin.getPricelist());
		}
		// todo:
		// if (no region in location) {
		if (plugin.config.region_globalMode == GlobalShopMode.NONE
				|| plugin.config.region_globalMode == GlobalShopMode.REGIONS) {
			return null;
		} else /*if (plugin.config.region_globalMode == GlobalShopMode.GLOBAL) */ {
			return new Shop(null, plugin.getPricelist());
		}
		// }
		// else, return the shop associated with this region
		// if none: return global if (plugin.config.region_globalMode == GlobalShopMode.REGIONS), else null
		
	}
}
