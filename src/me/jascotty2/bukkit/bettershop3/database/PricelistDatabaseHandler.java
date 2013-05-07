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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.ItemValue;
import me.jascotty2.libv2.util.Str;

public abstract class PricelistDatabaseHandler {

	protected final BetterShop3 plugin;
	/**
	 * internal save of the shop database <br />
	 * Shop Name => Item ID => Price <br />
	 * Item ID is divided into two sections: first 15 bits are for the id number, last 16 bits are for the data for the item
	 */
	protected final HashMap<String, Map<ItemValue, ItemPrice>> prices = new HashMap<String, Map<ItemValue, ItemPrice>>();
	/**
	 * what the global shop is identified in the database as
	 */
	protected static final String GLOBAL_IDENTIFIER = "__global__";
	/**
	 * time (in milliseconds) before the cached database is considered out of
	 * date
	 */
	protected long restockTimeout = -1;
	/**
	 * for individual data reads, when they were last reloaded
	 */
	protected final HashMap<String, Long> lastRestock = new HashMap<String, Long>();
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
	 * number of bytes in ID reserved for data <br />
	 * ( [num >> DATA_BYTE_LEN] == item id )
	 */
	public final static int DATA_BYTE_LEN = 16;
	/**
	 * for convenience: ID & DATA_BYTES == item data value <br />
	 * (65535)
	 */
	public final static int DATA_BYTES = Math.abs(Integer.MAX_VALUE << DATA_BYTE_LEN) - 1;
	/**
	 * for convenience: ID & ID_BYTES == item id byte value <br />
	 * (2147418112)
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
		lastRestock.clear();
		// todo: load stock times from file
		// for each shop, load lastRestock.put(shop, System.currentTimeMillis() - time);
		
		for (String shop : prices.keySet()) {
			if(!lastRestock.containsKey(shop)) {
				lastRestock.put(shop, System.currentTimeMillis());
			}
		}
	}
	
	protected final void saveStockTimes() {
		// todo
		// for each shop, save System.currentTimeMillis() - lastRestock.get(shop);
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

	public final Map<ItemValue, ItemPrice> getPricelist(String shop) {
		syncReload();
		shop = safeShopName(shop);
		if (needReload(shop, 0, 0)) {
			_fullReload();
		}
		if (prices.get(shop) == null && GLOBAL_IDENTIFIER.equals(shop)) {
			prices.put(shop, new HashMap<ItemValue, ItemPrice>());
		}
		return prices.get(shop);
	}

	public final Set<String> getShops() {
		return prices.keySet();
	}

	public final int totalEntries() {
		int n = 0;
		for (Map<ItemValue, ItemPrice> list : prices.values()) {
			n += list.size();
		}
		return n;
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
			return prices.get(shop).get(new ItemValue(id, data));
		}
		return null;
	}
	
	public ItemPrice getPrice(String shop, ItemValue idv) {
		syncReload();
		shop = safeShopName(shop);
		if (prices.containsKey(shop)) {
			if (needReload(shop, idv.id, idv.data)) {
				_reloadItemPrice(shop, idv.id, idv.data);
			}
			return prices.get(shop).get(idv);
		}
		return null;
	}

	public ItemPrice getPrice(String shop, int id) {
		syncReload();
		shop = safeShopName(shop);
		if (prices.containsKey(shop)) {
			ItemValue idv = new ItemValue(id);
			if (needReload(shop, idv.id, idv.data)) {
				_reloadItemPrice(shop, idv.id, idv.data);
			}
			return prices.get(shop).get(idv);
		}
		return null;
	}

	public ArrayList<ItemPrice> getPrices(String shop, ArrayList<ItemValue> items) {
		syncReload();
		shop = safeShopName(shop);
		Map<ItemValue, ItemPrice> pricelist = prices.get(shop);
		if (pricelist != null && !pricelist.isEmpty()) {
			ArrayList<ItemPrice> ret = new ArrayList<ItemPrice>();
			for (ItemValue item : items) {
				if (item.id > 0) {
					ItemPrice p = pricelist.get(item);
					if (p != null) {
						ret.add(p);
					}
				}
			}
			return ret;
		}
		return null;
	}

	public void setBuyPrice(String shop, int id, int data, double price) {
		setBuyPrice(shop, new ItemValue(id, data), price);
	}

	public void setBuyPrice(String shop, ItemValue idv, double price) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<ItemValue, ItemPrice>());
		}
		if (!prices.get(shop).containsKey(idv)) {
			prices.get(shop).put(idv,
					new ItemPrice(idv, price, -1, plugin.config.stock_default));
		} else {
			prices.get(shop).get(idv).buyPrice = price;
		}
		save();
	}

	public void setSellPrice(String shop, int id, int data, double price) {
		setSellPrice(shop, new ItemValue(id, data), price);
	}
	
	public void setSellPrice(String shop, ItemValue idv, double price) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<ItemValue, ItemPrice>());
		}
		if (!prices.get(shop).containsKey(idv)) {
			prices.get(shop).put(idv,
					new ItemPrice(idv, -1, price, plugin.config.stock_default));
		} else {
			prices.get(shop).get(idv).sellPrice = price;
		}
		save();
	}

	public void setPrice(String shop, int id, int data, double buy, double sell) {
		setPrice(shop, new ItemValue(id, data), buy, sell);
	}
	
	public void setPrice(String shop, ItemValue idv, double buy, double sell) {
		syncReload();
		shop = safeShopName(shop);
		if (!prices.containsKey(shop)) {
			prices.put(shop, new HashMap<ItemValue, ItemPrice>());
		}
		if (!prices.get(shop).containsKey(idv)) {
			prices.get(shop).put(idv,
					new ItemPrice(idv, buy, sell, plugin.config.stock_default));
		} else {
			prices.get(shop).get(idv).set(buy, sell);
		}
		save();
	}

	public void checkRestock() {
		if (plugin.config.stock_useStock) {
			if (plugin.config.stock_onlyGlobal) {
				checkRestock(null);
			} else {
				for (String s : prices.keySet()) {
					checkRestock(s);
				}
			}
		}
	}

	public void checkRestock(String shop) {
		if (plugin.config.stock_useStock) {
			shop = safeShopName(shop);
			if (prices.containsKey(shop)
					&& (!plugin.config.stock_onlyGlobal || shop.equals(GLOBAL_IDENTIFIER))) {
				if(!lastRestock.containsKey(shop)) {
					lastRestock.put(shop, System.currentTimeMillis());
				} else if(System.currentTimeMillis() - lastRestock.get(shop) > restockTimeout) {
					boolean lower = plugin.config.stock_restockLower;
					for (ItemPrice ip : prices.get(shop).values()) {
						long def = plugin.config.getDefaultStock(ip.id, ip.data);
						if(ip.stockAmount < def || lower) {
							ip.stockAmount = def;
						}
					}
					lastRestock.put(shop, System.currentTimeMillis());
				}
			}
		}
	}

	public static String safeShopName(String shop) {
		return shop == null ? GLOBAL_IDENTIFIER : Str.strTrim(shop, MAX_SHOPLEN);
	}

	protected void clearPrices() {
		for (String shop : prices.keySet()) {
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
		saveStockTimes();
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
