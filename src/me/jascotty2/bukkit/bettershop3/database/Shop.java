
package me.jascotty2.bukkit.bettershop3.database;

import java.util.ArrayList;
import me.jascotty2.bukkit.bettershop3.ItemValue;

public class Shop {
	final PricelistDatabaseHandler pricelist;
	public final String name;

	public Shop(String name, PricelistDatabaseHandler pricelist) {
		this.name = PricelistDatabaseHandler.safeShopName(name);
		this.pricelist = pricelist;
	}
	
	public void checkRestock() {
		pricelist.checkRestock(name);
	}
	
	public ItemPrice getPrice(int id) {
		if (id > 0) {
			return pricelist.getPrice(name, id);
		}
		return null;
	}
	
	public ItemPrice getPrice(int id, int data) {
		return id > 0 && data >= 0 ? pricelist.getPrice(name, id, data) : null;
	}
	
	public ArrayList<ItemPrice> getPrices(ArrayList<ItemValue> items) {
		return pricelist.getPrices(name, items);
	}
}
