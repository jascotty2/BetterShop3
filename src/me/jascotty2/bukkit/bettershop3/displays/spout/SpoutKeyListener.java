/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description: for
 * opening & closing shop menu
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
package me.jascotty2.bukkit.bettershop3.displays.spout;

import java.util.HashMap;
import java.util.logging.Level;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.Messages;
import me.jascotty2.bukkit.bettershop3.database.Shop;
import me.jascotty2.bukkit.bettershop3.enums.BetterShopPermission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;

public class SpoutKeyListener implements Listener {

	final static HashMap<String, String> keys = new HashMap<String, String>() {
		{
			put("'", "APOSTROPHE");
			put("+", "ADD");
			put("@", "AT");
			put("\\", "BACKSLASH");
			put(":", "COLON");
			put(",", "COMMA");
			put("/", "SLASH");
			put("=", "EQUALS");
			put("`", "GRAVE");
			put("[", "LBRACKET");
			put("-", "MINUS");
			put("*", "MULTIPLY");
			put(".", "PERIOD");
			put("^", "POWER");
			put("]", "RBRACKET");
			put(";", "SEMICOLON");
			put(" ", "SPACE");
			put("-", "SUBTRACT");
			put("_", "UNDERLINE");
			put("\t", "TAB");
		}
	};
	final BetterShop3 plugin;
	Keyboard listenKey = null;
	boolean keyPressError = false;

	public SpoutKeyListener(BetterShop3 plugin) {
		this.plugin = plugin;
		reloadKey();
	} // end default constructor

	public final void reloadKey() {
		String k = plugin.config.spout_popupKey;
		try {
			if (k != null && !(k = k.replace("KEY_", "")).isEmpty()) {
				if (keys.containsKey(k)) {
					k = keys.get(k);
				}
				listenKey = Keyboard.valueOf("KEY_" + k);
				return;
			}
		} catch (Exception e) {
		}
		plugin.getLogger().warning("Invalid Key in Spout Config: defaulting to 'B'");
		listenKey = Keyboard.KEY_B;
	}

	@EventHandler
	public void onKeyPressedEvent(KeyPressedEvent event) {
		if (plugin.config.spout_enabled) {
			try {
				if (event.getKey() == Keyboard.KEY_ESCAPE) {
					SpoutPopupDisplay.closePopup(event.getPlayer());
				} else if (event.getKey() == listenKey) {
					if (event.getScreenType() == ScreenType.GAME_SCREEN
							&& plugin.permissions.hasPermission(event.getPlayer(), BetterShopPermission.USER_SPOUT, true)) {
						Shop s = plugin.shopManager.getRegionShop(event.getPlayer().getLocation());
						if(s != null) {
							SpoutPopupDisplay.popup(plugin, event.getPlayer(), s);
						} else {
							plugin.messages.SendMessage(event.getPlayer(), Messages.SHOP.REGION_DISABLED);
						}
					}
				}
//			else if(event.getKey() == Keyboard.KEY_P) { SpoutPopupDisplay.testScreen(event.getPlayer()); }
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Unexpected error in KeyListener", e);
				keyPressError = true;
			}
		}
	}
} // end class SpoutKeyListener

