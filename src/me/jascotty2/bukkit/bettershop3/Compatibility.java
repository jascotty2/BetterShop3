package me.jascotty2.bukkit.bettershop3;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Compatibility {

	private static boolean compatMode = false, checked = false;

	public static boolean check() {
		if (!checked) {
			try {
				// any of the cb-specific classes will do here..
				Class test = Class.forName("org.bukkit.craftbukkit.v1_5_R3.inventory.CraftFurnaceRecipe");
				org.bukkit.craftbukkit.v1_5_R3.inventory.CraftFurnaceRecipe testSource;
			} catch (Throwable t) {
				Logger.getAnonymousLogger().log(Level.WARNING, "[BetterShop3] Using compatibility mode for specific features");
				compatMode = true;
			}
			checked = true;
		}
		return compatMode;
	}
}
