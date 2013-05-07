/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: tracks items in a database with buy/sell prices
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.bukkit.bettershop3.database;

import me.jascotty2.bukkit.bettershop3.ItemValue;

public class ItemPrice {

	public final int id;
	public final short data;
	public double buyPrice, sellPrice;
	public long stockAmount = -1;

	public ItemPrice(int id, short data) {
		this.id = id;
		this.data = data;
		buyPrice = sellPrice = -1;
	}

	public ItemPrice(ItemValue idv) {
		this.id = idv.id;
		this.data = idv.data;
		buyPrice = sellPrice = -1;
	}
	
	public ItemPrice(int id, short data, double buyPrice, double sellPrice) {
		this.id = id;
		this.data = data;
		set(buyPrice, sellPrice);
	}

	public ItemPrice(ItemValue idv, double buyPrice, double sellPrice) {
		this.id = idv.id;
		this.data = idv.data;
		set(buyPrice, sellPrice);
	}
	
	public ItemPrice(int id, short data, double buyPrice, double sellPrice, long stock) {
		this.id = id;
		this.data = data;
		stockAmount = stock;
		set(buyPrice, sellPrice);
	}
	
	public ItemPrice(ItemValue idv, double buyPrice, double sellPrice, long stock) {
		this.id = idv.id;
		this.data = idv.data;
		stockAmount = stock;
		set(buyPrice, sellPrice);
	}
	
	public void set(ItemPrice copy) {
		this.buyPrice = copy.buyPrice;
		this.sellPrice = copy.sellPrice;
		this.stockAmount = copy.stockAmount;
	}

	public final void set(double buyPrice, double sellPrice) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}
	
	public final void set(double buyPrice, double sellPrice, long stockAmount) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.stockAmount = stockAmount;
	}
	
	@Override
	public String toString() {
		return "ItemPrice{" + "buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + '}';
	}
	
	@Override
	public ItemPrice clone() {
		return new ItemPrice(id, data, buyPrice, sellPrice, stockAmount);
	}
}
