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
import java.util.LinkedList;
import me.jascotty2.bukkit.bettershop3.enums.ExtendedMaterials;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.util.Str;
import org.bukkit.ChatColor;
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
	final protected HashMap<Integer, ChatColor> itemColors = new HashMap<Integer, ChatColor>();
	final protected HashMap<String, Integer> items = new HashMap<String, Integer>();
	final protected HashMap<Integer, HashMap<String, Integer>> itemSubdata = new HashMap<Integer, HashMap<String, Integer>>();
	//final protected ArrayList<String>[] potions = new ArrayList[64];
	final protected static String DEFAULT_WATER_BOTTLE = "Water Bottle"; // 373:0
	final protected static String DEFAULT_LONG_FORMAT = "Extended %s";
	final protected static String DEFAULT_SPLASH_POTION = "Splash Potion";
	final protected static String DEFAULT_POTION = "Potion";
	final protected static String DEFAULT_POTION_NAMES[] = new String[]{
		/*  0 */"Mundane Potion",
		/*  1 */ "Potion of Regeneration",
		/*  2 */ "Potion of Swiftness",
		/*  3 */ "Potion of Fire Resistance",
		/*  4 */ "Potion of Poison",
		/*  5 */ "Potion of Healing",
		/*  6 */ "Clear Potion",
		/*  7 */ "Clear Potion",
		/*  8 */ "Potion of Weakness",
		/*  9 */ "Potion of Strength",
		/* 10 */ "Potion of Slowness",
		/* 11 */ "Diffuse Potion",
		/* 12 */ "Potion of Harming",
		/* 13 */ "Artless Potion",
		/* 14 */ "Thin Potion",
		/* 15 */ "Thin Potion",
		/* 16 */ "Awkward Potion",
		/* 17 */ "Potion of Regeneration",
		/* 18 */ "Potion of Swiftness",
		/* 19 */ "Potion of Fire Resistance",
		/* 20 */ "Potion of Poison",
		/* 21 */ "Potion of Healing",
		/* 22 */ "Bungling Potion",
		/* 23 */ "Bungling Potion 2",
		/* 24 */ "Potion of Weakness",
		/* 25 */ "Potion of Strength",
		/* 26 */ "Potion of Slowness",
		/* 27 */ "Smooth Potion",
		/* 28 */ "Potion of Harming",
		/* 29 */ "Suave Potion",
		/* 30 */ "Debonair Potion",
		/* 31 */ "Debonair Potion",
		/* 32 */ "Thick Potion",
		/* 33 */ "Potion of Regeneration II",
		/* 34 */ "Potion of Swiftness II",
		/* 35 */ "Potion of Fire Resistance",
		/* 36 */ "Potion of Poison II",
		/* 37 */ "Potion of Healing II",
		/* 38 */ "Charming Potion",
		/* 39 */ "Charming Potion",
		/* 40 */ "Potion of Weakness",
		/* 41 */ "Potion of Strength II",
		/* 42 */ "Potion of Slowness",
		/* 43 */ "Refined Potion",
		/* 44 */ "Potion of Harming II",
		/* 45 */ "Cordial Potion",
		/* 46 */ "Sparkling Potion",
		/* 47 */ "Sparkling Potion 2",
		/* 48 */ "Potent Potion",
		/* 49 */ "Potion of Regeneration II",
		/* 50 */ "Potion of Swiftness II",
		/* 51 */ "Potion of Fire Resistance",
		/* 52 */ "Potion of Poison II",
		/* 53 */ "Potion of Healing II",
		/* 54 */ "Rank Potion",
		/* 55 */ "Rank Potion",
		/* 56 */ "Potion of Weakness",
		/* 57 */ "Potion of Strength II",
		/* 58 */ "Potion of Slowness",
		/* 59 */ "Acrid Potion",
		/* 60 */ "Potion of Harming II",
		/* 61 */ "Gross Potion",
		/* 62 */ "Stinky Potion",
		/* 63 */ "Stinky Potion"
	};
	protected static String potion_longFormat = DEFAULT_LONG_FORMAT;
	protected static String potion_splash = DEFAULT_SPLASH_POTION;
	protected static String potion_Name = DEFAULT_POTION;
	/**
	 * when comparing strings for typos, the 'distance' between the strings to compare as equal
	 */
	public int MAX_LEVENSHTEIN_DIST = 2;

	public ItemLookupTable(BetterShop3 plugin) {
		this.plugin = plugin;

		//loadDefaults();
	}

	private void loadDefaults() {
		for (ArrayList<String> names : itemNames.values()) {
			names.clear();
		}
		itemNames.clear();
		items.clear();
		for (HashMap<String, Integer> subs : itemSubdata.values()) {
			subs.clear();
		}
		itemSubdata.clear();
		potion_longFormat = DEFAULT_LONG_FORMAT;
		potion_splash = DEFAULT_SPLASH_POTION;
		potion_Name = DEFAULT_POTION;

		// initialize item list
		int ignore_i = 0, extended_i = 0;
		for (Material m : Material.values()) {
			if (m.getId() == invalidItems[ignore_i]) {
				++ignore_i;
			} else if (m == Material.POTION) {
				addEntry(m.getId(), 0, DEFAULT_WATER_BOTTLE);
				for (int splash = 0; splash <= 1; ++splash) {
					for (int extra = 0; extra <= 1; ++extra) {
						for (int i = 0; i < 64; ++i) {
							addEntry(m.getId(), i + (extra << 6) + (splash << 14),
									extra == 1
									? (splash == 1 ? String.format(DEFAULT_LONG_FORMAT, DEFAULT_POTION_NAMES[i].replace(DEFAULT_POTION, DEFAULT_SPLASH_POTION))
									: String.format(DEFAULT_LONG_FORMAT, DEFAULT_POTION_NAMES[i]))
									: (splash == 1 ? DEFAULT_POTION_NAMES[i].replace(DEFAULT_POTION, DEFAULT_SPLASH_POTION)
									: DEFAULT_POTION_NAMES[i]));
						}
					}
				}
			} else if (m.getId() == ExtendedMaterials.idList[extended_i]) {
				for (ExtendedMaterials m2 : ExtendedMaterials.values()) {
					if (m2.getId() == m.getId()) {
						addEntry(m2.getId(), m2.getData(), Str.titleCase(m2.name().replace('_', ' ')));
					}
				}
			} else {
				String name;
				// special cases that would not be desireable to be left as default
				switch (m.getId()) {
					case 64:
						name = "Wooden Door Block";
						break;
					case 45:
						name = "Bricks";
						break;
					case 82:
						name = "Clay Block";
						break;
					default:
						name = Str.titleCase(m.name().replace('_', ' '));
				}
				addEntry(m.getId(), 0, name);
			}
		}
	}

	protected final void setItem(int id, int data, String value) {
		ArrayList<String> orig = itemNames.get((id << 16) + data);
		if (orig == null) {
			if(id == 5) {
				System.out.println(id + ":" + data + " - no known set? " + ((id << 16) + data));
			}
			addEntries(id, data, value);
		} else {
			LinkedList<String> names = new LinkedList<String>(orig);
			ArrayList<String> origKeys = new ArrayList<String>();
			for (String name : orig) {
				origKeys.add(name.replace(" ", "").toLowerCase());
			}
			if (value.contains(",")) {
				String v, values[] = value.split(",");
				if(id == 5) {
					System.out.println("Setting " + id + ":" + data + " ('" + value + "' = " + values.length + ") " + Str.concatStr(values, ";"));
				}
				boolean firstIn = false;
				for (int i = 0; i < values.length; ++i) {
					if ((v = values[i].trim()).length() > 0) {
						String valKey = v.replace(" ", "").toLowerCase();
						if (origKeys.contains(valKey)) {
							// already in the old list..
							if (!firstIn) {
								// if not yet decided on the display name, 
								// check if this key is the 1st value
								if (origKeys.indexOf(valKey) != 0) {
									// not already the 1st, so set the first elem to this
									names.removeFirstOccurrence(v);
									names.addFirst(v);
								}
								firstIn = true;
							}
							// else, don't need to do anything special: it's already in the list
						} else if (items.containsKey(valKey)) {
							// if this element exists elsewhere, don't add
							if (id != 373) {
								// there are alot of conflicting potion names...
								// some increment with level (bit 4), some don't
								// and tier 2 starts at bit 5, so not sure where that's going..
								ItemValue val = getItem(v);
								plugin.getLogger().info(String.format(
										"Notice: Name \"%s\" for item %d:%d conficts with an existing entry: %d:%d (ignoring)",
										v, id, data, val == null ? -1 : val.id, val == null ? -1 : val.data));

							}
							continue;
						} else {
							// add the new item to this list
							if (!firstIn) {
								names.addFirst(v);
								firstIn = true;
							} else {
								names.add(v);
							}
							// as well to the global index
							items.put(valKey, id);
							if(id == 5) {
								System.out.println("adding " + v + " to 5:" + data);
							}
						}
					}
				}
			} else {
				names.addFirst(value);
			}
			orig.clear();
			orig.addAll(names);
			//itemNames.put((id << 16) + data, orig);
		}
	}

	protected final void addEntries(int id, int data, String value) {
		for (String v : value.split(",")) {
			if ((v = v.trim()).length() > 0) {
				addEntry(id, data, v);
			}
		}
	}

	protected final void addEntry(int id, int data, String value) {
		String keyValue = value.replace(" ", "").toLowerCase();
		if (items.containsKey(keyValue)) {
			// there are alot of conflicting potion names...
			// some increment with level (bit 4), some don't
			// and tier 2 starts at bit 5, so not sure where that's going..
			if (id != 373) {
				//plugin.getLogger().info(
				System.out.println(String.format(
						"Notice: Name \"%s\" for item %d:%d conficts with an existing entry: ignoring",
						value, id, data));
			}
			return;
		}
		id = (id << 16) + data;
		if (!itemNames.containsKey(id)) {
			itemNames.put(id, new ArrayList<String>());
		}
		itemNames.get(id).add(value);
		items.put(keyValue, id);
	}

	protected final void addSubEntries(int id, int data, String value) {
		if (!itemSubdata.containsKey(id)) {
			itemSubdata.put(id, new HashMap<String, Integer>());
		}
		HashMap<String, Integer> map = itemSubdata.get(id);
		for (String v : value.split(",")) {
			if ((v = v.trim()).length() > 0) {
				//addSubEntry(id, data, v);
				// only slightly more efficient than calling the funtion
				if (map.containsKey(v)) {
					plugin.getLogger().info(String.format(
							"Notice: Sub-Name \"%s\" for item %d:%d conficts with an existing entry: ignoring",
							v, id, data));
				} else {
					map.put(v, data);
				}
			}
		}
	}

	protected final void addSubEntry(int id, int data, String value) {
		if (!itemSubdata.containsKey(id)) {
			itemSubdata.put(id, new HashMap<String, Integer>());
		}
		HashMap<String, Integer> map = itemSubdata.get(id);
		if (map.containsKey(value)) {
			plugin.getLogger().info(String.format(
					"Notice: Sub-Name \"%s\" for item %d:%d conficts with an existing entry: ignoring",
					value, id, data));
			return;
		}
		map.put(value, data);
	}

	public void load(YamlConfiguration conf) {
		loadDefaults();
		// sanity check
		if (conf == null || !(conf.get("Items") instanceof MemorySection)) {
			return;
		}
		MemorySection itemNameSection = (MemorySection) conf.get("Items");
		int ignore_i = 0, extended_i = 0;
		for (Material m : Material.values()) {
			if (m.getId() == invalidItems[ignore_i]) {
				++ignore_i;
			} else {
				String idStr = String.valueOf(m.getId());
				if (itemNameSection.contains(idStr)) {
					setItem(m.getId(), 0, itemNameSection.getString(idStr));
				}
				if (m.getId() == ExtendedMaterials.idList[extended_i]) {
					++extended_i;
					for (ExtendedMaterials m2 : ExtendedMaterials.values()) {
						if (m2.getId() == m.getId()) {
							String idDatStr = idStr + "_" + m2.getData();
							if (itemNameSection.contains(idDatStr)) {
								setItem(m.getId(), m2.getData(), itemNameSection.getString(idDatStr));
							}
							idDatStr += "_sub";
							if (itemNameSection.contains(idDatStr)) {
								addSubEntries(m.getId(), m2.getData(), itemNameSection.getString(idDatStr));
							}
						}
					}
				}
			}
		}
		if (conf.get("Potions") instanceof MemorySection) {
			itemNameSection = (MemorySection) conf.get("Potions");
			if (itemNameSection.contains("Extra_Time")) {
				potion_longFormat = itemNameSection.getString("Extra_Time").replace("%s", "%%s").replace("<name>", "%s");
				if (!potion_longFormat.replace("%%s", "").contains("%s")) {
					potion_longFormat = potion_longFormat + " %s";
				}
			}
			potion_splash = itemNameSection.getString("Splash_Potion", potion_splash);
			potion_Name = itemNameSection.getString("Potion_Name", potion_Name);

			for (int i = 0; i < 64; ++i) {
				if (itemNameSection.contains(String.valueOf(i))) {
					String name = itemNameSection.getString(String.valueOf(i));
					for (String v : name.trim().split(",")) {
						if ((v = v.trim()).length() > 0) {
							for (int extra = 0; extra <= 1; ++extra) {
								addEntry(373, i + (extra << 6),
										extra == 1 ? String.format(potion_longFormat, v) : v);
								// now add splash variant
								if (v.contains(potion_Name)) {
									addEntry(373, i + (extra << 6) + (1 << 14),
											extra == 1
											? String.format(potion_longFormat, v.replace(potion_Name, potion_splash))
											: v.replace(potion_Name, potion_splash));
								} else {
									addEntry(373, i + (extra << 6) + (1 << 14),
											extra == 1
											? String.format(potion_longFormat, potion_splash + " " + v)
											: potion_splash + " " + v);
								}
							}
						}
					}
				}
			}
		}
	}

	public ItemValue getItem(String search) {
		search = search.replace(" ", "").toLowerCase();
		if (CheckInput.IsInt(search)) {
			int id = CheckInput.GetInt(search, -1);
			if ((id & 2147418112) == 0 && id > 0 && itemNames.containsKey(id << 16)) {
				return new ItemValue(id, 0);
			}
		} else if (search.contains(":")) {
			if (Str.count(search, ":") == 1) {
				String idStr = search.substring(0, search.indexOf(':'));
				ItemValue find = getItem(idStr);

				if (find != null) {
					if (ExtendedMaterials.usesData(find.id)) {
						String datStr = search.substring(search.indexOf(':') + 1);
						if (CheckInput.IsInt(datStr)) {
							int dat = CheckInput.GetInt(search, -1);
							if ((dat & 2147418112) == 0 && dat > 0 && ExtendedMaterials.validData(find.id, dat)) {
								find.data = dat;
								return find;
							}
						} else if (itemSubdata.containsKey(find.id)) {
							// look up using sub table (eg. wool:black)
							HashMap<String, Integer> subs = itemSubdata.get(find.id);
							if (subs.containsKey(datStr)) {
								find.data = subs.get(datStr);
								return find;
							} else {
								// now do string-compare for the closest match
								String close = null;
								int dist = MAX_LEVENSHTEIN_DIST + 1;
								for (String k : subs.keySet()) {
									int d = Str.getLevenshteinDistance(datStr, k);
									if (d < dist) {
										close = k;
										dist = d;
									} else if (d == dist) {
										close = null;
									}
								}
								if (close != null) {
									find.data = subs.get(close);
									return find;
								}
							}
						}
					}
				}
			}
		} else if (items.containsKey(search)) {
			// direct match to name or alias
			return new ItemValue(items.get(search) >> 16, items.get(search) & 65535);
		} else {
			// now do string-compare for the closest match
			String close = null;
			int dist = MAX_LEVENSHTEIN_DIST + 1;
			for (String k : items.keySet()) {
				int d = Str.getLevenshteinDistance(search, k);
				if (d < dist) {
					close = k;
					dist = d;
				} else if (d == dist) {
					close = null;
				}
			}
			if (close != null) {
				return new ItemValue(items.get(close) >> 16, items.get(close) & 65535);
			}
		}
		System.out.println("no result for '" + search + "'");
		System.out.println("all values: " + Str.concatStr(items.keySet(), ", "));
		return null;
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

		@Override
		public String toString() {
			return "ItemValue{" + "id=" + id + ", data=" + data + '}';
		}
	}
}
