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

import java.util.HashMap;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.Messages;
import me.jascotty2.libv2.util.Str;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommands implements CommandExecutor {
	
	final BetterShop3 plugin;
	protected HashMap<String, String> lastBuy = new HashMap<String, String>();
	
	public BuyCommands(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.messages.SendMessage(sender, Messages.COMMANDS.PLAYER_REQUIRED);
			return true;
		} else if (command.getName().equals("buyagain")) {
			String last = lastBuy.get(((Player)sender).getName());
			if(last == null) {
				plugin.messages.SendMessage(sender, Messages.SHOP.NO_LAST_BUY);
			} else {
				// attempt sale
				_buy((Player) sender, last);
			}
		} else {
			// attempt sale
			return buy((Player) sender, Str.concatStr(args, " "));
		}
		return true;
	}

	public boolean buy(Player p, String sellArgs) {
		// attempt sale
		if (_buy(p, sellArgs)) {
			lastBuy.put(p.getName(), sellArgs);
			return true;
		}
		return false;
	}

	private boolean _buy(Player p, String sellArgs) {
		return true;
	}
}
