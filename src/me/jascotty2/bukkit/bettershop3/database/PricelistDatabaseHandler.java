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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.libv2.util.Str;

public abstract class PricelistDatabaseHandler {

	protected final BetterShop3 plugin;
	/**
	 * internal save of the shop database <br />
	 * Shop Name => Item ID => Price <br />
	 * Item ID is divided into two sections: first 15 bits are for the id number, last 16 bits are for the data for the item
	 */
	protected final HashMap<String, Map<Integer, ItemPrice>> prices = new HashMap<String, Map<Integer, ItemPrice>>();
	/**
	 * what the global shop is identified in the database as
	 */
	protected static final String GLOBAL_IDENTIFIER = "__global__";
	/**
	 * time (in milliseconds) before the cached database is considered out of
	 * date
	 */
	protected long cacheTimeout = -1;
	/**
	 * last time the full db was reloaded
	 */
	protected long lastReload = -1;
	/**
	 * for individual data reads, when they were last reloaded
	 */
	protected final HashMap<String, Map<Integer, Long>> lastRead = new HashMap<String, Map<Integer, Long>>();
	/**
	 * db is frozen when an active reload is processing
	 */
	private boolean frozen = false;
	/**
	 * number of bytes in ID reserved for data
	 */
	public final static int DATA_BYTE_LEN = 16;
	/**
	 * for convenience: ID & DATA_BYTES == item data value <br />
	 * (65535)
	 */
	public final static int DATA_BYTES = Math.abs(Integer.MAX_VALUE << DATA_BYTE_LEN) - 1;
	/**
	 * for convenience: ID & ID_BYTES == item id byte value
	 */
	public final static int ID_BYTES = Integer.MAX_VALUE ^ DATA_BYTES;
	
	public final static int MAX_SHOPLEN = 30;
	
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
	 *
	 * @param id
	 * @param data
	 */
	protected abstract void reloadItemPrice(String shop, int id, int data);

	/**
	 * save the database
	 */
	protected abstract void saveFull();

	/**
	 * load the database
	 */
	public final void load() {
		lastReload = System.currentTimeMillis();
		initialize();
	}

	protected final void _fullReload() {
		frozen = true;
		try {
			lastReload = System.currentTimeMillis();
			fullReload();
		} catch (Throwable t) {
			plugin.getLogger().log(Level.SEVERE, "Error while reloading Pricelist Database", t);
		}
		frozen = false;
	}

	protected final void _reloadItemPrice(String shop, int id, int data) {
		shop = safeShopName(shop);
		if (prices.containsKey(shop) && id > 0 && data > 0) {
			try {
				frozen = true;
				if (!lastRead.containsKey(shop)) {
					lastRead.put(shop, new HashMap<Integer, Long>());
				}
				lastRead.get(shop).put((id << DATA_BYTE_LEN) + data, System.currentTimeMillis());
				reloadItemPrice(shop, id, data);
			} catch (Throwable t) {
				plugin.getLogger().log(Level.SEVERE, "Error while reloading Pricelist Database", t);
			}
			frozen = false;
		}
	}

	protected final boolean needReload(String shop, int id, int data) {
		if (cacheTimeout > 0 && data >= 0) {
			if (id <= 0) {
				id = data = 0;
			}
			if (shop == null) {
				if (id == 0) {
					// full db
					return System.currentTimeMillis() - lastReload > cacheTimeout;
				} else {
					// global shop
					shop = GLOBAL_IDENTIFIER;
				}
			} else {
				shop = safeShopName(shop);
			}
			if (!lastRead.containsKey(shop)) {
				if (!prices.containsKey(shop)) {
					// no such shop
					return false;
				} else {
					lastRead.put(shop, new HashMap<Integer, Long>());
				}
			} else {
				Long last = lastRead.get(shop).get((id << DATA_BYTE_LEN) + data);
				if (last != null && lastReload > last) {
					return System.currentTimeMillis() - last > cacheTimeout;
				}
			}
			return System.currentTimeMillis() - lastReload > cacheTimeout;
		}
		return false;
	}

	protected synchronized void syncReload() {
		if (frozen) {
			try {
				while (frozen) {
					this.wait(50);
				}
			} catch (InterruptedException ex) {
				plugin.getLogger().log(Level.SEVERE, null, ex);
			}
		}
	}

	public final Map<Integer, ItemPrice> getPricelist(String shop) {
		syncReload();
		shop = safeShopName(shop);
		if (needReload(shop, 0, 0)) {
			_fullReload();
		}
		return prices.get(shop);
	}

	public final Set<String> getShops() {
		return prices.keySet();
	}
	/**
	 * @param shop
	 * @param id
	 * @param data
	 * @return the ItemPrice that represents this price, or null if none
	 */
	public ItemPrice getPrice(String shop, int id, int data) {
		syncReload();
		shop = safeShopName(shop);
		if (prices.containsKey(shop)) {
			if (needReload(shop, id, data)) {
				_reloadItemPrice(shop, id, data);
			}
			return prices.get(shop).get((id << DATA_BYTE_LEN) + data);
		}
		return null;
	}

	public void setBuyPrice(String shop, int id, int data, double price) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<Integer, ItemPrice>());
		}
		if (!prices.get(shop).containsKey((id << DATA_BYTE_LEN) + data)) {
			prices.get(shop).put((id << DATA_BYTE_LEN) + data, new ItemPrice(price, -1));
		} else {
			prices.get(shop).get((id << DATA_BYTE_LEN) + data).buyPrice = price;
		}
		save();
	}

	public void setSellPrice(String shop, int id, int data, double price) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<Integer, ItemPrice>());
		}
		if (!prices.get(shop).containsKey((id << DATA_BYTE_LEN) + data)) {
			prices.get(shop).put((id << DATA_BYTE_LEN) + data, new ItemPrice(-1, price));
		} else {
			prices.get(shop).get((id << DATA_BYTE_LEN) + data).sellPrice = price;
		}
		save();
	}
	
	public void setPrice(String shop, int id, int data, double buy, double sell) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<Integer, ItemPrice>());
		}
		if (!prices.get(shop).containsKey((id << DATA_BYTE_LEN) + data)) {
			prices.get(shop).put((id << DATA_BYTE_LEN) + data, new ItemPrice(buy, sell));
		} else {
			prices.get(shop).get((id << DATA_BYTE_LEN) + data).set(buy, sell);
		}
		save();
	}
	
	public static String safeShopName(String shop) {
		return shop == null ? GLOBAL_IDENTIFIER : Str.strTrim(shop, MAX_SHOPLEN);
	}
	
	protected void clearPrices() {
		for(String shop : prices.keySet()) {
			prices.get(shop).clear();
		}
		prices.clear();
	}
	/// threaded saving ///
	private Saver saveTask = null;

	public final void save() {
		if (saveTask != null) {
			saveTask.cancel();
		}
		saveTask = new Saver();
		saveTask.start(30000);
	}

	public void flushSave() {
		if (saveTask != null) {
			saveTask.cancel();
			saveTask.run();
		}
		saveTask = null;
	}

	class Saver extends TimerTask {

		public void start(long wait) {
			(new Timer()).schedule(this, wait);
		}

		@Override
		public void run() {
			saveFull();
		}
	}
}
