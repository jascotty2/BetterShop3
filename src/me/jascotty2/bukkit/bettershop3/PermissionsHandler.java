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

import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.enums.BetterShopPermission;
import me.jascotty2.libv2.util.Str;
import net.milkbowl.vault.Vault;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionsHandler {

	final BetterShop3 plugin;
	net.milkbowl.vault.permission.Permission vaultPerms = null;
	com.nijikokun.bukkit.Permissions.Permissions nijPerms = null;

	protected PermissionsHandler(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	protected void enable() {
		// attempt to load external permissions plugins
		Plugin p = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (p instanceof Vault) {
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp =
					plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			vaultPerms = rsp == null ? null : rsp.getProvider();
			if (vaultPerms != null) {
				plugin.getLogger().info("Using Vault " + p.getDescription().getVersion() + " for Permissions");
				return;
			}
		}
		if ((p = plugin.getServer().getPluginManager().getPlugin("Permissions")) != null
				&& p instanceof com.nijikokun.bukkit.Permissions.Permissions) {
			nijPerms = (com.nijikokun.bukkit.Permissions.Permissions) p;
			plugin.getLogger().info("Using nijikokun's Permissions " + p.getDescription().getVersion());
			return;
		}
		plugin.getLogger().info("No Permissions Plugin -  using Bukkit Permissions (SuperPerms)");
	}
	
	
	public boolean hasPermission(CommandSender player, BetterShopPermission node) {
		return hasPermission(player, node.toString(), false);
	}

	public boolean hasPermission(CommandSender player, BetterShopPermission node, boolean notify) {
		return hasPermission(player, node.toString(), notify);
	}

	public boolean hasPermission(CommandSender player, String node) {
		return hasPermission(player, node, false);
	}

	public boolean hasPermission(CommandSender player, String node, boolean notify) {
		if (player == null || player.isOp() || !(player instanceof Player) // ops override permission check (double-check is a Player)
				|| node == null || node.length() == 0) {
			return true;
		}
		if (has((Player) player, node)) {
			return true;
		} else if (notify) {
			plugin.messages.SendMessage(player, Messages.PERMISSION.DENIED, node);
		}
		return false;
	}

	public boolean has(Player player, String node) {
		try {
			if (vaultPerms != null) {
				return vaultPerms.has(player, node);
			} else if (nijPerms != null) {
				return nijPerms.getHandler().has(player, node);
			}
//			System.out.println("no perm: checking superperm for " + player.getName() + ": " + node);
//			System.out.println(player.hasPermission(node));
//			for(PermissionAttachmentInfo i : player.getEffectivePermissions()){
//				System.out.println(i.getPermission());
//			}
			if (player.hasPermission(node)) {
				return true;
			} else if (!node.contains("*") && Str.count(node, '.') >= 2) {
				return player.hasPermission(node.substring(0, node.lastIndexOf('.') + 1) + "*");
			}
			return false;
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error Checking Permissions", e);
		}
		return node.length() < 16 // if invalid node, assume true
				|| (!node.substring(0, 16).equalsIgnoreCase("BetterShop.admin") // only ops have access to .admin
				&& !node.substring(0, 19).equalsIgnoreCase("BetterShop.discount"));
	}
}
