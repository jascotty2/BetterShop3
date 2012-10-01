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
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class BetterShop3 extends JavaPlugin {

	public final EconomyHandler economy = new EconomyHandler(this);
	public final Messages messages = new Messages(this);
	public final FileManager fileManager = new FileManager(this);
	public final SettingsManager config = new SettingsManager(this);
	public final PermissionsHandler permissions = new PermissionsHandler(this);
	
	@Override
	public void onEnable() {
		fileManager.extractFiles();
		config.load();
		messages.load(config.locale);
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

}
