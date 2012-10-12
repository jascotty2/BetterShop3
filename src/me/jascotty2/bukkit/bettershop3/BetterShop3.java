/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 *
 * Description: Global Shop System for Minecraft
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

import java.io.IOException;
import java.util.List;
import me.jascotty2.libv2.util.Str;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class BetterShop3 extends JavaPlugin {

	public final EconomyHandler economy = new EconomyHandler(this);
	public final Messages messages = new Messages(this);
	public final FileManager fileManager = new FileManager(this);
	public final SettingsManager config = new SettingsManager(this);
	public final PermissionsHandler permissions = new PermissionsHandler(this);
	public final ItemLookupTable itemDB = new ItemLookupTable(this);

	@Override
	public void onEnable() {
		fileManager.extractFiles();
		config.load();
		messages.load(config.locale, itemDB);
		economy.enable();
		permissions.enable();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		
	}

	@Override
	public void onDisable() {
		economy.flushSave();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			System.out.println(((Player) sender).getName());
			for (ItemStack i : ((Player) sender).getInventory().getContents()) {
				System.out.println(i == null ? "null" : i.toString() + " - " + i.getDurability());
			}
		}
		return true;
	}
	
}
