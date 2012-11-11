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
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.commands.*;
import me.jascotty2.bukkit.bettershop3.database.*;
import me.jascotty2.bukkit.bettershop3.enums.PricelistType;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class BetterShop3 extends JavaPlugin {

	public final EconomyHandler economy = new EconomyHandler(this);
	public final Messages messages = new Messages(this);
	public final FileManager fileManager = new FileManager(this);
	public final SettingsManager config = new SettingsManager(this);
	public final PermissionsHandler permissions = new PermissionsHandler(this);
	public final ItemLookupTable itemDB = new ItemLookupTable(this);
	protected PricelistDatabaseHandler pricelist;
	public final ShopCommands com_shop = new ShopCommands(this);
	public final SellCommands com_sell = new SellCommands(this);
	public final BuyCommands com_buy = new BuyCommands(this);
	public final PriceCheckCommands com_price = new PriceCheckCommands(this);
	
	@Override
	public void onEnable() {
		// housekeeping
		fileManager.extractFiles();
		// settings
		config.load();
		messages.load(config.locale, itemDB);
		config.updateSortIDs(itemDB);
		// initialize handlers
		economy.enable();
		permissions.enable();
		if (config.pricelist_type == PricelistType.MYSQL) {
			try {
				pricelist = new MySQL_Database(this);
			} catch (Throwable t) {
				getLogger().log(Level.SEVERE, "Failed to connect to MySQL Server", t);
				getLogger().info(String.format("Reverting to %s Database..", config.pricelist_type_default.name()));
				config.pricelist_type = config.pricelist_type_default;
			}
		}
		if (config.pricelist_type == PricelistType.CSV) {
			pricelist = new CSV_Database(this);
		} else if (config.pricelist_type == PricelistType.YAML) {
			pricelist = new YAML_Database(this);
		}
		pricelist.load();
		// initialize commands
		setCommand("shop", com_shop);
		setCommand("buy", com_buy);
		setCommand("buyagain", com_buy);
		setCommand("sell", com_sell);
		setCommand("sellagain", com_sell);
		setCommand("price", com_price);
		setCommand("pricelist", com_price);

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		
	}

	private void setCommand(String command, CommandExecutor exec) {
		PluginCommand c = getCommand(command);
		if (c != null) {
			c.setExecutor(exec);
		} else {
			getLogger().warning(String.format("Could not register command \"%s\"", command));
		}
	}

	@Override
	public void onDisable() {
		economy.flushSave();
		pricelist.flushSave();
		if (pricelist instanceof MySQL_Database) {
			((MySQL_Database) pricelist).disconnect();
		}
	}

	public PricelistDatabaseHandler getPricelist() {
		return pricelist;
	}
}
