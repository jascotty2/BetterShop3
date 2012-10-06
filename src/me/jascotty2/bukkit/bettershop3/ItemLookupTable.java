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

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemLookupTable {
	
	final BetterShop3 plugin;
	// items that require data to define
	final static int[] useData = new int[]{
		5, 6, 17, 18, 24, 31, 35, 43, 44, 
		78, 97, 98, 99, 100, 125, 126,
		263, 351, 373, 383, 397};
	// what item ids' data resemble max damage value
	// (must be in ascending order)
	final static int[] tools = new int[]{
		256, 257, 258, 259, 261, 267, 268, 269, 270, 271,
		272, 273, 274, 275, 276, 277, 278, 278, 279,
		283, 284, 285, 286, 290, 291, 292, 293, 294,
		298, 299, 300, 301, 302, 303, 304, 305,
		306, 307, 308, 309, 310, 311, 312, 313,
		314, 315, 316, 317, 346, 359};
	// items (other than tools) that aren't good to stack
	final static int[] unsafeStack = new int[]{
		282, 325, 326, 327, 335};
	// first 16 bits == id, last 16 bits == data
	final protected HashMap<Integer, ArrayList<String>> itemNames = new HashMap<Integer, ArrayList<String>>();
	final protected HashMap<String, Integer> items = new HashMap<String, Integer>();

	public ItemLookupTable(BetterShop3 plugin) {
		this.plugin = plugin;
		// initialize item list
		for(Material m : Material.values()) {
			
		}
	}
	
	public void load(YamlConfiguration conf) {
		// sanity check
		if(conf == null || !(conf.get("Items") instanceof MemorySection) ) {
			return;
		}
		MemorySection itemNames = (MemorySection) conf.get("Items");
		
	}
	
	public static boolean usesData(int id) {
		for (int i = 0; i < useData.length; i++) {
			if(id == useData[i]) {
				return true;
			}
		}
		return false;
	}
}
