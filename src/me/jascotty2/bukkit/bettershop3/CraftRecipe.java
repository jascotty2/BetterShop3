package me.jascotty2.bukkit.bettershop3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.jascotty2.libv2.io.CheckInput;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftRecipe {

	protected int resultAmount;
	public final static int WILD_MASK = 32767; // minecraft crafting value that indicates that any sub-data is valid
	protected HashMap<ItemValue, Integer> recipe = new HashMap<ItemValue, Integer>();
	protected static HashMap<Integer, Double> smeltingSources = new HashMap<Integer, Double>(){{
		put(263, 8.); // coal
		put(17, 1.5); // log
		put(5, 1.5); // wood plank
		put(6, .5); // sapling
		put(173, 72.); // block of coal
		put(327, 100.); // lava bucket
		put(99, 1.5); // huge brown mushroom
		put(100, 1.5); // huge red mushroom
		put(369, 12.); // blaze rod
		// not going to add crafted items...
		//put(126, .75); // wood slab
	}};

	public CraftRecipe(ShapedRecipe rec) {
		Map<Character, ItemStack> ing = rec.getIngredientMap();
		String[] rows = rec.getShape();
		for (Map.Entry<Character, ItemStack> e : ing.entrySet()) {
			if (e.getValue() != null) {
				int count = 0;
				for (String r : rows) {
					for (char c : r.toCharArray()) {
						if (e.getKey() == c) {
							++count;
						}
					}
				}
				add(new ItemValue(e.getValue().getTypeId(), e.getValue().getDurability()), count);
			}
		}
//		if(rec.getResult().getType() == org.bukkit.Material.BED) {
//			System.out.println("Bed: ");
//			for(Map.Entry<ItemValue, Integer> r : recipe.entrySet()) {
//				System.out.println(r.getKey() + " x " + r.getValue());
//			} 
//		}
		resultAmount = rec.getResult().getAmount();
	}

	public CraftRecipe(ShapelessRecipe rec) {
		List<ItemStack> ing = rec.getIngredientList();
		for (ItemStack i : ing) {
			add(new ItemValue(i.getTypeId(), i.getDurability()), i.getAmount());
		}
		resultAmount = rec.getResult().getAmount();
	}

	protected CraftRecipe() {}
	
	public static List<CraftRecipe> getSmeltRecipes(ItemValue source) {
		ArrayList<CraftRecipe> smelted = new ArrayList<CraftRecipe>();
		for(Map.Entry<Integer, Double> fuel : smeltingSources.entrySet()) {
			int fuelAmt = 1;
			double result = fuel.getValue();
			while((result - Math.floor(result)) > .001) {
				result = fuel.getValue() * (++fuelAmt);
			}
			CraftRecipe r = new CraftRecipe();
			r.add(new ItemValue(fuel.getKey(), WILD_MASK), fuelAmt);
			r.add(source, (int) result);
			r.resultAmount = (int) result;
			smelted.add(r);
		}
		return smelted;
	}
	
	public CraftRecipe(String constructor, ItemLookupTable refTable) {
		// ex: 4@8+263=8

		// get result amount
		if (constructor.contains("=")) {
			if (constructor.split("=").length > 2 || constructor.length() == constructor.indexOf("=")) {
				resultAmount = 0;
				return;
			}
			resultAmount = CheckInput.GetInt(constructor.substring(constructor.indexOf("=") + 1), 0);
			constructor = constructor.substring(0, constructor.indexOf("="));
		} else {
			resultAmount = 1;
		}
		// extract all items
		for (String i : constructor.split("\\+")) {
			ItemValue ni;
			String it = i;
			if (i.contains("@")) {
				it = i.substring(0, i.indexOf("@"));
				ni = refTable.getItem(it);
				add(ni, i.length() > i.indexOf("@") ? CheckInput.GetInt(i.substring(i.indexOf("@") + 1), 0) : 1);
			} else {
				ni = refTable.getItem(i);
				add(ni, 1);
			}
			if (ni == null) {
				System.out.println("null item: " + it);
			}
		}
	}

	protected final void add(ItemValue item, int amount) {
		if (!recipe.containsKey(item)) {
			recipe.put(item, amount);
		} else {
			recipe.put(item, recipe.get(item) + amount);
		}
	}

	public int getResultAmount() {
		return resultAmount;
	}
}