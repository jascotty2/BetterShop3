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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.database.PricelistDatabaseHandler;
import me.jascotty2.bukkit.bettershop3.enums.ExtendedMaterials;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.util.Str;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftFurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class ItemLookupTable {

	final BetterShop3 plugin;
	// do NOT add these id's to the table
	//		(must be in order)
	final static int[] invalidItems = new int[]{
		0, 26, 34, 36, 55, 59, 62, 63, 68, 75, 90,
		92, 93, 94, 104, 105, 115, 117, 118, 119,
		132, 140, 141, 142, 144
	};
	// items (other than tools) that aren't good to stack
	final static int[] unsafeStack = new int[]{
		282, 325, 326, 327, 335, 387};
	// first 15 bits == id, last 16 bits == data
	final protected HashMap<ItemValue, ArrayList<String>> itemNames = new HashMap<ItemValue, ArrayList<String>>();
	final protected HashMap<ItemValue, ChatColor> itemColors = new HashMap<ItemValue, ChatColor>();
	final protected HashMap<String, ItemValue> items = new HashMap<String, ItemValue>();
	final protected HashMap<Integer, HashMap<String, Short>> itemSubdata = new HashMap<Integer, HashMap<String, Short>>();
	final protected ArrayList<ItemValue> sortedItemList = new ArrayList<ItemValue>();
	final protected HashMap<ItemValue, List<CraftRecipe>> recipes = new HashMap<ItemValue, List<CraftRecipe>>();
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
		/*  6 */ "Potion of Night Vision",
		/*  7 */ "Clear Potion",
		/*  8 */ "Potion of Weakness",
		/*  9 */ "Potion of Strength",
		/* 10 */ "Potion of Slowness",
		/* 11 */ "Diffuse Potion",
		/* 12 */ "Potion of Harming",
		/* 13 */ "Artless Potion",
		/* 14 */ "Potion of Invisibility",
		/* 15 */ "Thin Potion",
		/* 16 */ "Awkward Potion",
		/* 17 */ "Potion of Regeneration",
		/* 18 */ "Potion of Swiftness",
		/* 19 */ "Potion of Fire Resistance",
		/* 20 */ "Potion of Poison",
		/* 21 */ "Potion of Healing",
		/* 22 */ "Potion of Night Vision",
		/* 23 */ "Bungling Potion",
		/* 24 */ "Potion of Weakness",
		/* 25 */ "Potion of Strength",
		/* 26 */ "Potion of Slowness",
		/* 27 */ "Smooth Potion",
		/* 28 */ "Potion of Harming",
		/* 29 */ "Suave Potion",
		/* 30 */ "Potion of Invisibility",
		/* 31 */ "Debonair Potion",
		/* 32 */ "Thick Potion",
		/* 33 */ "Potion of Regeneration II",
		/* 34 */ "Potion of Swiftness II",
		/* 35 */ "Potion of Fire Resistance",
		/* 36 */ "Potion of Poison II",
		/* 37 */ "Potion of Healing II",
		/* 38 */ "Potion of Night Vision II",
		/* 39 */ "Charming Potion",
		/* 40 */ "Potion of Weakness",
		/* 41 */ "Potion of Strength II",
		/* 42 */ "Potion of Slowness",
		/* 43 */ "Refined Potion",
		/* 44 */ "Potion of Harming II",
		/* 45 */ "Cordial Potion",
		/* 46 */ "Potion of Invisibility II",
		/* 47 */ "Sparkling Potion",
		/* 48 */ "Potent Potion",
		/* 49 */ "Potion of Regeneration II",
		/* 50 */ "Potion of Swiftness II",
		/* 51 */ "Potion of Fire Resistance",
		/* 52 */ "Potion of Poison II",
		/* 53 */ "Potion of Healing II",
		/* 54 */ "Potion of Night Vision II",
		/* 55 */ "Rank Potion",
		/* 56 */ "Potion of Weakness",
		/* 57 */ "Potion of Strength II",
		/* 58 */ "Potion of Slowness",
		/* 59 */ "Acrid Potion",
		/* 60 */ "Potion of Harming II",
		/* 61 */ "Gross Potion",
		/* 62 */ "Potion of Invisibility II",
		/* 63 */ "Stinky Potion"
	};
	protected static String potion_longFormat = DEFAULT_LONG_FORMAT;
	protected static String potion_splash = DEFAULT_SPLASH_POTION;
	protected static String potion_Name = DEFAULT_POTION;
	/**
	 * when comparing strings for typos, the 'distance' between the strings to
	 * compare as equal
	 */
	public int MAX_LEVENSHTEIN_DIST = 2;

	public ItemLookupTable(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	private void loadDefaults() {
		for (ArrayList<String> names : itemNames.values()) {
			names.clear();
		}
		for (List<CraftRecipe> rs : recipes.values()) {
			rs.clear();
		}
		recipes.clear();
		itemNames.clear();
		items.clear();
		for (HashMap<String, Short> subs : itemSubdata.values()) {
			subs.clear();
		}
		itemSubdata.clear();
		potion_longFormat = DEFAULT_LONG_FORMAT;
		potion_splash = DEFAULT_SPLASH_POTION;
		potion_Name = DEFAULT_POTION;

		// initialize item list
		int ignore_i = 0, extended_i = 0;
		for (Material m : Material.values()) {
			if (ignore_i < invalidItems.length && m.getId() == invalidItems[ignore_i]) {
				++ignore_i;
			} else if (m == Material.POTION) {
				addEntry(m.getId(), 0, DEFAULT_WATER_BOTTLE);
				for (int d = 0; d < 64; ++d) {
					addEntry(m.getId(), d, DEFAULT_POTION_NAMES[d]);
					addEntry(m.getId(), d + (1 << 6), // extra time
							String.format(DEFAULT_LONG_FORMAT, DEFAULT_POTION_NAMES[d]));
					addEntry(m.getId(), d + (1 << 14), // splash
							DEFAULT_POTION_NAMES[d].replace(DEFAULT_POTION, DEFAULT_SPLASH_POTION));
					addEntry(m.getId(), d + (1 << 6) + (1 << 14), // extra time, splash
							String.format(DEFAULT_LONG_FORMAT, DEFAULT_POTION_NAMES[d].replace(DEFAULT_POTION, DEFAULT_SPLASH_POTION)));
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
					case 127:
						name = "Cocoa Plant";
						break;
					default:
						name = Str.titleCase(m.name().replace('_', ' '));
				}
				addEntry(m.getId(), 0, name);
			}
		}
		// now load recipes

		Iterator serverRecipes = plugin.getServer().recipeIterator();
		String errors = ""; // only show each error once
		while (serverRecipes.hasNext()) {
			Object rec = serverRecipes.next();
			if (rec instanceof ShapedRecipe) {
				ShapedRecipe r = (ShapedRecipe) rec;
				addRecipe(new ItemValue(r.getResult().getTypeId(), r.getResult().getData().getData()), new CraftRecipe(r));
			} else if (rec instanceof ShapelessRecipe) {
				ShapelessRecipe r = (ShapelessRecipe) rec;
				addRecipe(new ItemValue(r.getResult().getTypeId(), r.getResult().getData().getData()), new CraftRecipe(r));
			} else if (!Compatibility.check() && rec instanceof CraftFurnaceRecipe) {
				CraftFurnaceRecipe r = (CraftFurnaceRecipe) rec;
				ItemValue idvRes = new ItemValue(r.getResult().getTypeId(), r.getResult().getData().getData());
				for(CraftRecipe rc : CraftRecipe.getSmeltRecipes(new ItemValue(r.getInput()))) {
					addRecipe(idvRes, rc);
				}
			} else {
				if(!errors.contains("'" + rec.getClass().getName())) {
					errors += "'" + rec.getClass().getName();
					System.out.println("unknown recipe: " + rec.getClass().getName());
				}
			}
		}
//		int i = 0;
//		for (List<CraftRecipe> rs : recipes.values()) {
//			i += rs.size();
//		}
//		System.out.println(i + " recipes saved");
	}

	protected void addRecipe(ItemValue idv, CraftRecipe r) {
		if (!recipes.containsKey(idv)) {
			recipes.put(idv, new ArrayList<CraftRecipe>());
		}
		recipes.get(idv).add(r);
	}

	protected final void setItem(int id, int data, String value) {
		ItemValue idv = new ItemValue(id, data);
		ArrayList<String> orig = itemNames.get(idv);
		String colored = Messages.convertColorChars(value);
		if (colored.contains(String.valueOf(ChatColor.COLOR_CHAR))) {
			itemColors.put(new ItemValue(id, data),
					ChatColor.getByChar(colored.charAt(colored.indexOf(ChatColor.COLOR_CHAR) + 1)));
			value = ChatColor.stripColor(colored);
		}
		if (orig == null) {
			addEntries(id, data, value);
		} else {
			LinkedList<String> names = new LinkedList<String>(orig);
			ArrayList<String> origKeys = new ArrayList<String>();
			for (String name : orig) {
				origKeys.add(name.replace(" ", "").toLowerCase());
			}
			if (value.contains(",")) {
				String v, values[] = value.split(",");
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
								// don't show notice if is a potion:
								//   there are alot of conflicting potion names...
								//   some increment with level (bit 4), some don't
								//   and tier 2 starts at bit 5, so not sure where that's going..
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
							items.put(valKey, new ItemValue(id, data));
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
				ItemValue val = getItem(keyValue);
				plugin.getLogger().info(String.format(
						"Notice: Name \"%s\" for item %d:%d conficts with an existing entry: %d:%d (ignoring)",
						keyValue, id, data, val == null ? -1 : val.id, val == null ? -1 : val.data));
			}
			return;
		}
		ItemValue idv = new ItemValue(id, data);
		if (!itemNames.containsKey(idv)) {
			itemNames.put(idv, new ArrayList<String>());
		}
		itemNames.get(idv).add(value);
		items.put(keyValue, idv);
	}

	protected final void addSubEntries(int id, int data, String value) {
		addSubEntries(new ItemValue(id, data), value);
	}

	protected final void addSubEntries(ItemValue idv, String value) {
		if (!itemSubdata.containsKey(idv.id)) {
			itemSubdata.put(idv.id, new HashMap<String, Short>());
		}
		HashMap<String, Short> map = itemSubdata.get(idv.id);
		for (String v : value.split(",")) {
			if ((v = v.trim()).length() > 0) {
				//addSubEntry(id, data, v);
				// only slightly more efficient than calling the funtion
				if (map.containsKey(v.toLowerCase())) {
					plugin.getLogger().info(String.format(
							"Notice: Sub-Name \"%s\" for item %d:%d conficts with an existing entry (" + map.get(v.toLowerCase()) + ") : ignoring",
							v, idv.id, idv.data));
				} else {
					map.put(v.toLowerCase(), (short) idv.data);
				}
			}
		}
	}

	protected final void addSubEntry(int id, int data, String value) {
		addSubEntry(new ItemValue(id, data), value);
	}

	protected final void addSubEntry(ItemValue idv, String value) {
		if (!itemSubdata.containsKey(idv.id)) {
			itemSubdata.put(idv.id, new HashMap<String, Short>());
		}
		HashMap<String, Short> map = itemSubdata.get(idv.id);
		if (map.containsKey(value.toLowerCase())) {
			plugin.getLogger().info(String.format(
					"Notice: Sub-Name \"%s\" for item %d:%d conficts with an existing entry (" + map.get(value.toLowerCase()) + ") : ignoring",
					value, idv.id, idv.data));
			return;
		}
		map.put(value.toLowerCase(), (short) idv.data);
	}

	public void load(YamlConfiguration conf) {
		loadDefaults();
		sortedItemList.clear();
		sortedItemList.addAll(itemNames.keySet());
		reorderSortedIds(null);
		// sanity check
		if (conf == null || !(conf.get("Items") instanceof MemorySection)) {
			return;
		}
		MemorySection itemNameSection = (MemorySection) conf.get("Items");
		int ignore_i = 0, extended_i = 0;
		for (Material m : Material.values()) {
			if (ignore_i < invalidItems.length && m.getId() == invalidItems[ignore_i]) {
				++ignore_i;
			} else {
				String idStr = String.valueOf(m.getId());
				if (itemNameSection.contains(idStr)) {
					setItem(m.getId(), 0, itemNameSection.getString(idStr));
				}
				if (extended_i < ExtendedMaterials.idList.length && m.getId() == ExtendedMaterials.idList[extended_i]) {
					++extended_i;
					for (ExtendedMaterials m2 : ExtendedMaterials.values()) {
						if (m2.getId() == m.getId()) {
							String idDatStr = idStr + "-" + m2.getData();

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

	public boolean isValidItem(int id) {
		if (id > 0) {
			if ((id & PricelistDatabaseHandler.ID_BYTES) == 0) {
				return Material.getMaterial(id) != null;
			} else if (itemNames.containsKey(id)) {
				return Material.getMaterial(id >> PricelistDatabaseHandler.DATA_BYTE_LEN) != null;
			}
		}
		return false;
	}

	public boolean isValidItem(int id, int data) {
		if (id > 0 && data >= 0) {
			if (Material.getMaterial(id) != null) {
				return true; //itemNames.containsKey(idVal(id, data));
			}
		}
		return false;
	}

	public ItemValue getItem(String search) {
		search = search.replace(" ", "").toLowerCase();
		if (CheckInput.IsInt(search)) {
			ItemValue idv = new ItemValue(CheckInput.GetInt(search, -1));
			if (idv.id > 0 && itemNames.containsKey(idv)) {
				return idv;
			}
		} else if (search.contains(":") || search.contains("-")) {
			char delim;
			if (search.contains("-")) {
				delim = '-';
			} else { // if(search.contains(":")) {
				delim = ':';
			}
			if (Str.count(search, delim) == 1) {
				String idStr = search.substring(0, search.indexOf(delim));
				ItemValue find = getItem(idStr);
				if (find != null) {
					if (ExtendedMaterials.usesData(find.id)) {
						String datStr = search.substring(search.indexOf(delim) + 1);
						if (CheckInput.IsInt(datStr)) {
							short dat = CheckInput.GetShort(datStr, (short) -1);
							if ((dat & 2147418112) == 0 && dat > 0 && ExtendedMaterials.validData(find.id, dat)) {
								find.data = dat;
								return find;
							}
						} else if (itemSubdata.containsKey(find.id)) {
							// look up using sub table (eg. wool:black)
							HashMap<String, Short> subs = itemSubdata.get(find.id);
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
			return items.get(search);
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
				return items.get(close);
			}
		}
		return null;
	}

	public String getItemName(int id) {
		ItemValue idv = new ItemValue(id);
		return itemNames.containsKey(idv) ? itemNames.get(idv).get(0) : null;
	}

	public String getItemName(int id, int data) {
		ItemValue idv = new ItemValue(id, data);
		return itemNames.containsKey(idv) ? itemNames.get(idv).get(0) : null;
	}

	public String getItemName(ItemValue idv) {
		return idv != null && itemNames.containsKey(idv) ? itemNames.get(idv).get(0) : null;
	}

	public String getColoredItemName(int id) {
		ItemValue idv = new ItemValue(id);
		if (itemNames.containsKey(idv)) {
			if (itemColors.containsKey(idv)) {
				return itemColors.get(idv) + itemNames.get(idv).get(0);
			}
			return itemNames.get(idv).get(0);
		}
		return null;
	}

	public String getColoredItemName(int id, short data) {
		ItemValue idv = new ItemValue(id, data);
		if (itemNames.containsKey(idv)) {
			if (itemColors.containsKey(idv)) {
				return itemColors.get(idv) + itemNames.get(idv).get(0);
			}
			return itemNames.get(idv).get(0);
		}
		return null;
	}

	public String getColoredItemName(ItemValue idv) {
		if (itemNames.containsKey(idv)) {
			if (itemColors.containsKey(idv)) {
				return itemColors.get(idv) + itemNames.get(idv).get(0);
			}
			return itemNames.get(idv).get(0);
		}
		return null;
	}

	public ArrayList<String> getItemNames(int id) {
		ItemValue idv = new ItemValue(id);
		return itemNames.containsKey(idv) ? (ArrayList<String>) itemNames.get(idv).clone() : null;
	}

	public ArrayList<String> getItemNames(int id, int data) {
		ItemValue idv = new ItemValue(id, data);
		return itemNames.containsKey(idv) ? (ArrayList<String>) itemNames.get(idv).clone() : null;
	}

	public ArrayList<String> getItemNames(ItemValue idv) {
		return idv != null && itemNames.containsKey(idv) ? (ArrayList<String>) itemNames.get(idv).clone() : null;
	}

//	public static int idVal(int id, int data) {
//		return id >= 0 && data >= 0 ? (id << PricelistDatabaseHandler.DATA_BYTE_LEN) + data : -1;
//	}

	public ItemValue[] getFullIdList() {
		//return itemNames.keySet().toArray(new Integer[0]);
		return sortedItemList.toArray(new ItemValue[0]);
	}

	public void reorderSortedIds(final ArrayList<Integer> sortFirst) {
		Collections.sort(sortedItemList, new Comparator<ItemValue>() {
			@Override
			public int compare(ItemValue o1, ItemValue o2) {
				if (sortFirst != null && !sortFirst.isEmpty()) {
					int o1i = sortFirst.indexOf(o1);
					int o2i = sortFirst.indexOf(o2);
					if (o1i != -1 && o2i != -1) {
						return o1i - o2i;
					} else if (o1i != -1) {
						return -1;
					} else if (o2i != -1) {
						return 1;
					}
				}
				return o1.toIDVal() - o2.toIDVal();
			}
		});
	}

	public List<String> getItemNameMatches(String search) {
		/**
		 * min length of string for spelling and inStr checks
		 */
		final int MIN_STR_LEN = 3;
		ArrayList<String> partialMatches = new ArrayList<String>(),
				spellingMatches = new ArrayList<String>(),
				stringMatches = new ArrayList<String>();
		search = search.replace(" ", "").toLowerCase();
		boolean requireDataItem = search.contains(":");
		String dataValue = "";
		if (requireDataItem) {
			dataValue = search.substring(search.indexOf(':') + 1);
			search = search.substring(0, search.indexOf(':'));
		}
		boolean start, instr, spell;
		for (Map.Entry<ItemValue, ArrayList<String>> e : itemNames.entrySet()) {
			if (!requireDataItem || ExtendedMaterials.usesData(e.getKey().id)) {
				for (String item : e.getValue()) {
					String check = item.replace(" ", "").toLowerCase();
					start = instr = spell = false;
					if (check.startsWith(search)) {
						start = true;
					} else if (search.length() > MIN_STR_LEN && Str.getLevenshteinDistance(check, search) <= MAX_LEVENSHTEIN_DIST) {
						spell = true;
					} else if (search.length() > MIN_STR_LEN && check.contains(search)) {
						instr = true;
					}
					// partial matches
					if (start || instr || spell) {
						if (requireDataItem) {
							if ((e.getKey().data) == 0 && itemSubdata.containsKey(e.getKey().id)) {
								// look up using sub table (eg. wool:black)
								HashMap<String, Short> subs = itemSubdata.get(e.getKey().id);
								if (subs.containsKey(dataValue)) {
									partialMatches.add(item + ":" + dataValue);
								} else {
									ArrayList<Short> used = new ArrayList<Short>();
									// now do string-compare for matches
									for (Map.Entry<String, Short> de : subs.entrySet()) {
										if (!used.contains(de.getValue())) {
											if (de.getKey().startsWith(dataValue)) { // partial matches
												partialMatches.add(item + ":" + de.getKey());
												used.add(de.getValue());
											} else if (dataValue.length() > MIN_STR_LEN && Str.getLevenshteinDistance(de.getKey(), dataValue) <= MAX_LEVENSHTEIN_DIST) {// spelling
												spellingMatches.add(item + ":" + de.getKey());
												used.add(de.getValue());
											} else if (dataValue.length() > MIN_STR_LEN && de.getKey().contains(dataValue)) { // string
												stringMatches.add(item + ":" + de.getKey());
												used.add(de.getValue());
											}
										}
									}
									used.clear();
								}
							}
						} else if (start) {
							partialMatches.add(item);
						} else if (spell) {
							spellingMatches.add(item);
						} else if (instr) {
							stringMatches.add(item);
						}
						break;
					}
				}
			}
		}
		partialMatches.addAll(spellingMatches);
		partialMatches.addAll(stringMatches);
		return partialMatches;
	}
}
