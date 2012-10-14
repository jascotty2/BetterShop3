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
package me.jascotty2.bukkit.bettershop3.commands;

import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.libv2.util.ArrayManip;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopCommands implements CommandExecutor {
	
	final BetterShop3 plugin;
	
	public ShopCommands(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length >= 1) {
			if(args[0].equals("sell") || args[0].equals("s")) {
				return plugin.com_sell.onCommand(sender, command, label, ArrayManip.arraySub(args, 1));
			} else if(args[0].equals("sellagain") || args[0].equals("sa")) {
				return plugin.com_sell.onCommand(sender, plugin.getCommand("sellagain"), label, ArrayManip.arraySub(args, 1));
			} else if(args[0].equals("buy") || args[0].equals("b")) {
				return plugin.com_buy.onCommand(sender, command, label, ArrayManip.arraySub(args, 1));
			} else if(args[0].equals("buyagain") || args[0].equals("ba")) {
				return plugin.com_buy.onCommand(sender, plugin.getCommand("buyagain"), label, ArrayManip.arraySub(args, 1));
			}
		}
		// todo: display help
		return true;
	}
	
}
