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

import java.util.ArrayList;
import java.util.Map;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.ItemValue;
import me.jascotty2.bukkit.bettershop3.Messages;
import me.jascotty2.bukkit.bettershop3.database.ItemPrice;
import me.jascotty2.libv2.io.CheckInput;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PriceCheckCommands implements CommandExecutor {

	final BetterShop3 plugin;

	public PriceCheckCommands(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("pricelist")) {
			// display full pricelist (paged)
			int page = sender instanceof Player ? 1 : 0;
			String[] searchArgs = null;
			if (args.length > 0) {
				// last arg would be page number
				if (CheckInput.IsInt(args[args.length - 1])) {
					page = CheckInput.GetInt(args[args.length - 1], 1);
				}
				// todo: add search option
			}
			sendPricelist(sender, page, searchArgs);
		} else {
			// individual item lookup
		}
		return true;
	}
	
	public void sendPricelist(CommandSender sendTo, int pageNum, String[] args) {
		if (!(sendTo instanceof Player) && pageNum <= 0 && args == null) {
			for (String shop : plugin.getPricelist().getShops()) {
				plugin.getServer().getConsoleSender().sendMessage(shop + " PriceList:");
				_sendPricelist(sendTo, pageNum, new String[]{shop});
			}
		} else {
			_sendPricelist(sendTo, pageNum, args);
		}
	}

	private void _sendPricelist(CommandSender sendTo, int pageNum, String[] args) {
		if (!(sendTo instanceof Player) && pageNum <= 0 && args == null) {
			for (String shop : plugin.getPricelist().getShops()) {
				plugin.getServer().getConsoleSender().sendMessage(shop + " PriceList:");
				sendPricelist(sendTo, pageNum, new String[]{shop});
			}
		} else {
			String shop = null;
			Map<ItemValue, ItemPrice> prices = null;
			if (sendTo instanceof Player) {
				// todo: get the name of the shop the player is in, if applicable
			} else if (args != null && args.length > 0) {
				prices = plugin.getPricelist().getPricelist(args[0]);
				if (prices != null) {
					shop = args[0];
				}
			}
			ArrayList<Object[]> paramList = new ArrayList<Object[]>();
			if (prices == null) {
				prices = plugin.getPricelist().getPricelist(shop);
			}
			int totalPages = pageNum > 0 ? (int) Math.ceil((double) prices.size() / plugin.config.itemsPerPage) : 1;
			if (pageNum > totalPages) {
				pageNum = totalPages;
			}
			int pageStart = (pageNum - 1) * plugin.config.itemsPerPage,
					display = pageNum > 0 ? plugin.config.itemsPerPage : Integer.MAX_VALUE;

			for (ItemValue idVal : plugin.itemDB.getFullIdList()) {
				if (prices.containsKey(idVal)) {
					if (--pageStart < 0) {
						ItemPrice p = prices.get(idVal);
						paramList.add(new Object[]{plugin.itemDB.getItemName(idVal), 
								p.buyPrice >= 0 ? plugin.economy.numFormat(p.buyPrice) : "--", // "No", 
								p.sellPrice >= 0 ? plugin.economy.numFormat(p.sellPrice) : "--", // "No",
								plugin.economy.getCurrencyName(), 
								plugin.economy.format(p.buyPrice), plugin.economy.format(p.sellPrice),
								p.stockAmount < 0 ? plugin.messages.getMessage(Messages.SHOP_LIST.INFINITE_STOCK_NAME) : p.stockAmount});
						if (--display <= 0) {
							break;
						}
					}
				}
			}
			int maxBuyLen1 = 1, maxSellLen1 = 1,
					maxBuyLen2 = 1, maxSellLen2 = 1;
			for (Object[] param : paramList) {
				int l;
				if ((l = ((String) param[1]).length()) > maxBuyLen1) {
					maxBuyLen1 = l;
				}
				if ((l = ((String) param[2]).length()) > maxSellLen1) {
					maxSellLen1 = l;
				}
				if ((l = ((String) param[4]).length()) > maxBuyLen2) {
					maxBuyLen2 = l;
				}
				if ((l = ((String) param[5]).length()) > maxSellLen2) {
					maxSellLen2 = l;
				}
			}
			for (Object[] param : paramList) {
				param[1] = String.format("%" + maxBuyLen1 + "s", ((String) param[1]));
				param[2] = String.format("%" + maxSellLen1 + "s", ((String) param[2]));
				param[4] = String.format("%" + maxBuyLen2 + "s", ((String) param[4]));
				param[5] = String.format("%" + maxSellLen2 + "s", ((String) param[5]));
			}
			plugin.messages.SendMessage(null, Messages.SHOP_LIST.HEADER, pageNum > 0 ? pageNum : 1, totalPages);
			plugin.messages.SendMessages(null, Messages.SHOP_LIST.LISTING, paramList);
			plugin.messages.SendMessage(null, Messages.SHOP_LIST.FOOTER);
		}
	}
}
