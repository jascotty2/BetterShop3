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

import java.io.File;
import java.util.logging.Level;
import me.jascotty2.libv2.io.FileIO;

public class FileManager {
	
	protected final BetterShop3 plugin;
	public static final File pluginDir = new File("plugins", "BetterShop3");	
	public static final File oldPluginDir = new File("plugins", "BetterShop");
	public static final File langDir = new File(pluginDir, "lang");
	
	protected FileManager(BetterShop3 plugin) {
		this.plugin = plugin;
		//langDir = new File(plugin.getDataFolder(), "lang");
	}

	protected void extractFiles() {
		try {
			// FileIO.OVERWRITE_CASE.IF_NEWER does not work if jar is symbolic link
			FileIO.extractResource("lang/en.yml", langDir, BetterShop3.class);// , FileIO.OVERWRITE_CASE.IF_NEWER);			
			FileIO.extractResource("config.yml", pluginDir, BetterShop3.class);//, FileIO.OVERWRITE_CASE.NEVER);
		} catch (Exception ex) {
			plugin.getLogger().log(Level.SEVERE, "Failed to extract Config Files", ex);
		}
	}

}
