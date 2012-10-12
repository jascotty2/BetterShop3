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

public class ItemPrice {

	public double buyPrice, sellPrice;

	public ItemPrice() {
		buyPrice = sellPrice = -1;
	}

	public ItemPrice(float buyPrice, float sellPrice) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}

	@Override
	public String toString() {
		return "ItemPrice{" + "buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + '}';
	}
}
