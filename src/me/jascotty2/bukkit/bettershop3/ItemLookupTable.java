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
import me.jascotty2.bukkit.bettershop3.enums.ExtendedMaterials;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.util.Str;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemLookupTable {

	final BetterShop3 plugin;
	// do NOT add these id's to the table
	//		(must be in order)
	final static int[] invalidItems = new int[]{
		0, 26, 34, 36, 55, 59, 62, 63, 68, 75, 90,
		92, 93, 94, 104, 105, 115, 117, 118, 119,
		132, 140, 141, 142, 144
	};
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
	// first 15 bits == id, last 16 bits == data
	final protected HashMap<Integer, ArrayList<String>> itemNames = new HashMap<Integer, ArrayList<String>>();
	final protected HashMap<String, Integer> items = new HashMap<String, Integer>();
	/**
	 * when comparing strings for typos, the 'distance' between the strings to compare as equal
	 */
	public int MAX_LEVENSHTEIN_DIST = 2;
	
	public ItemLookupTable(BetterShop3 plugin) {
		this.plugin = plugin;
		// initialize item list
		int ignore_i = 0, extended_i = 0;
		for (Material m : Material.values()) {
			if (m.getId() == invalidItems[ignore_i]) {
				++ignore_i;
			} else if (m.getId() == ExtendedMaterials.idList[extended_i]) {
				for (ExtendedMaterials m2 : ExtendedMaterials.values()) {
					if (m2.getId() == m.getId()) {
						addEntry(m2.getId(), m2.getData(), Str.titleCase(m2.name().replace('_', ' ')));
					}
				}
			} else {
				addEntry(m.getId(), 0, Str.titleCase(m.name().replace('_', ' ')));
			}
		}
	}

	protected final void addEntry(int id, int data, String value) {
		id = (id << 16) + data;
		if (!itemNames.containsKey(id)) {
			itemNames.put(id, new ArrayList<String>());
		}
		itemNames.get(id).add(value);
		items.put(value, id);
	}

	public ItemValue getItem(String search) {
		if (CheckInput.IsInt(search)) {
			int id = CheckInput.GetInt(search, -1);
			if((id & 2147418112) == 0 && id > 0 && itemNames.containsKey(id << 16)) {
				return new ItemValue(id, 0);
			}
		} else if (search.contains(":")) {
			if(Str.count(search, ":") == 1) {
				String idStr = search.substring(0, search.indexOf(':'));
				ItemValue find = getItem(idStr);
				
				if(find != null) {
					if(ExtendedMaterials.usesData(find.id)) {
						String datStr = search.substring(search.indexOf(':') + 1);
						if (CheckInput.IsInt(datStr)) {
							int dat = CheckInput.GetInt(search, -1);
							if((dat & 2147418112) == 0 && dat > 0 && ExtendedMaterials.validData(find.id, dat)) {
								find.data = dat;
								return find;
							}
						} else {
							// look up using sub table (eg. wool:black)
						}
					}
				}
			}
		} else {
			
		}
		return null;
	}
			
	public void load(YamlConfiguration conf) {
		// sanity check
		if (conf == null || !(conf.get("Items") instanceof MemorySection)) {
			return;
		}
		MemorySection itemNames = (MemorySection) conf.get("Items");

	}

	public static class ItemValue {

		public int id, data;

		ItemValue() {
		}

		public ItemValue(int id) {
			this.id = id;
		}

		public ItemValue(int id, int data) {
			this.id = id;
			this.data = data;
		}
	}
}
