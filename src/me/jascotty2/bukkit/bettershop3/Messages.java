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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import me.jascotty2.libv2.util.ArrayManip;
import me.jascotty2.libv2.util.Str;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {

	final BetterShop3 plugin;
	private final static String MISSING_STRING = "&d---&cMissing Message&d---";
	protected final static Class messageTypes[] = new Class[]{
		SHOP.class, PERMISSION.class, SHOP_LIST.class};
	protected final String messages[][] = new String[messageTypes.length][];

	// <editor-fold defaultstate="collapsed" desc="Message Templates">
	public static enum SHOP implements MessageType {

		PREFIX,
		UNKNOWN_ITEM("<item>"),
		BAD_PARAMETER("<error>");
		private final String[] tags;

		private SHOP(String... tags) {
			this.tags = tags;
		}

		@Override
		public int getNumberOfTags() {
			return tags.length;
		}

		@Override
		public String[] getTags() {
			return tags;
		}

		@Override
		public String getTag(int i) {
			return i >= 0 && i < tags.length ? tags[i] : null;
		}
	}

	public static enum PERMISSION implements MessageType {

		DENIED("<perm>");
		private final String[] tags;

		private PERMISSION(String... tags) {
			this.tags = tags;
		}

		@Override
		public int getNumberOfTags() {
			return tags.length;
		}

		@Override
		public String[] getTags() {
			return tags;
		}

		@Override
		public String getTag(int i) {
			return i >= 0 && i < tags.length ? tags[i] : null;
		}
	}

	public static enum SHOP_LIST implements MessageType {

		HEADER("<page>", "<pages>"),
		LISTING("item>", "<buyprice>", "<sellprice>", "<curr>", "<avail>"),
		FOOTER,
		ALIAS("<item>", "<alias>"),
		NOLIST("<item>"),
		PRICECHECK("<item>", "<buyprice>", "<sellprice>", "<curr>", "<max>", "<buycur>", "<sellcur>", "<avail>");
		private final String[] tags;

		private SHOP_LIST(String... tags) {
			this.tags = tags;
		}

		@Override
		public int getNumberOfTags() {
			return tags.length;
		}

		@Override
		public String[] getTags() {
			return tags;
		}

		@Override
		public String getTag(int i) {
			return i >= 0 && i < tags.length ? tags[i] : null;
		}
	}
// </editor-fold>

	protected Messages(BetterShop3 plugin) {
		this.plugin = plugin;
		for (int i = 0; i < messageTypes.length; ++i) {
			Class c = messageTypes[i];
			if (c.isEnum()) {
				messages[i] = new String[c.getEnumConstants().length];
			}
		}
	}

	public void load(String locale) {
		File lang = new File(FileManager.langDir, locale + ".yml");
		if (!lang.exists()) {
			if (!locale.equals("en")) {
				lang = new File(FileManager.langDir, "en.yml");
			}
			if (!lang.exists()) {
				plugin.getLogger().severe((locale.equals("en") ? "Error: 'en.yml' cannot be found" : "Error: Cannot load locale '" + locale + "', and 'en.yml' cannot be found"));
				return;
			} else {
				plugin.getLogger().severe("Error: Cannot load locale '" + locale + "'. Defaulting to en");
			}
		}

		// TODO: load default en from jar, then use those strings if new lang is missing translations

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		boolean needSave = false;
		for (int i = 0; i < messageTypes.length; ++i) {
			Class c = messageTypes[i];
			if (c.isEnum()) {
				String k = Str.titleCase(c.getSimpleName().replace('_', ' ')).replace(' ', '_');
				if (conf.contains(k)) {
					ConfigurationSection s = conf.getConfigurationSection(k);
					Object[] o = c.getEnumConstants();
					for (int j = 0; j < o.length; ++j) {
						String k2 = Str.titleCase(o[j].toString().replace('_', ' ')).replace(' ', '_');
						if (!s.contains(k2)) {
							plugin.getLogger().info(k + "." + k2 + " missing from " + lang.getName());
							s.set(k2, MISSING_STRING);
							needSave = true;
						}
						messages[i][j] = lastColorTag(convertColorChars(convertTags(s.getString(k2), (MessageType) o[j])));
					}
				} else {
					plugin.getLogger().info("Error: Section '" + k + "' is missing from " + lang.getName());
					conf.createSection(k);
					ConfigurationSection s = conf.getConfigurationSection(k);
					int j = 0;
					for (Object o : c.getEnumConstants()) {
						s.set(Str.titleCase(o.toString()), MISSING_STRING);
						messages[i][j++] = convertColorChars(MISSING_STRING);
					}
					needSave = true;
				}
			}
		}
		if (needSave) {
			try {
				conf.save(lang);
				plugin.getLogger().info("Lang File updated to reflect required updates");
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to Save Update Messages", ex);
			}
		}
	}
	final static Character shortColors[] = new Character[]{'B', 'N', 'G', 'Q', 'R', 'P', 'U', 'd', 'D', 'b', 'g', 'q', 'r', 'p', 'y', 'w', '~', 'r'};
	final static Character colors[] = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'r'};
	final static String colorTags[] = new String[]{"<black>", "<darkblue>", "<darkgreen>", "<darkaqua>", "<darkred>", "<darkpurple>",
		"<gold>", "<gray>", "<darkgray>", "<blue>", "<green>", "<aqua>", "<red>", "<lightpurple>", "<yellow>", "<white>", "<magic>", "<reset>"};

	public static String convertColorChars(String str) {
		if (str.contains("<")) {
			for (int i = 0; i < colorTags.length; ++i) {
				str = str.replace(colorTags[i], String.valueOf(ChatColor.COLOR_CHAR) + colors[i]);
			}
		}
		StringBuilder s = new StringBuilder();
		int i = 0;
		for (; i < str.length() - 1; ++i) {
			if (str.charAt(i) == '&') {
				if (str.charAt(i + 1) == '&') {
					s.append("&");
					++i;
					continue;
				} else {
					// test shorthand color tags
					char ch = str.charAt(i + 1);
					int chi = ArrayManip.indexOf(shortColors, (Character) ch);
					if (chi != -1) {
						ch = colors[chi];
					}
					ChatColor c = ChatColor.getByChar(ch);
					if (c != null) {
						s.append(c.toString());
						++i;
						continue;
					}
				}
			}
			s.append(str.charAt(i));
		}
		if (i < str.length()) {
			s.append(str.charAt(i));
		}
		return s.toString();
	}

	final static Character format[] = new Character[]{'l', 'm', 'o', 'n', 'r'};
	final static String formatTags[] = new String[]{"<bold>", "<strike>", "<underline>", "<italic>", "<reset>"};	
	final static String shortFormatTags[] = new String[]{"<b>", "<del>", "<u>", "<em>", "<r>"};

	private static String convertTags(String msg, MessageType message) {
		if (msg.contains("<")) {
			//String[] tags = message.getTags();
			for (int j = 0; j < message.getNumberOfTags(); ++j) {
				msg = msg.replace(message.getTag(j), "{" + j + "}");
			}
			msg = msg.replace("<newline>", "\n").replace("<br>", "\n").replace("&\\", "\n");
			// now for formatting tags
			for(int i = 0; i < formatTags.length; ++i) {
				msg = msg.replace(formatTags[i], String.valueOf(ChatColor.COLOR_CHAR) + format[i])
						.replace(shortFormatTags[i], String.valueOf(ChatColor.COLOR_CHAR) + format[i]);
			}
		}
		return msg;
	}
	
	private static String lastColorTag(String msg) {
		if(msg.contains("<endcolor>") || msg.contains("</>") || msg.contains("&/")) {
			//TODO
		}
		return msg;
	}

	public void SendMessage(CommandSender player, Enum message) {
		SendMessage(player, message, new Object[0]);
	}

	public void SendMessage(CommandSender player, Enum message, Object... params) {
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		Class c = message.getDeclaringClass();
		int i = ArrayManip.indexOf(messageTypes, c);
		if (i != -1) {
			String msg = messages[i][message.ordinal()];
			try {
				msg = MessageFormat.format(msg, params);
			} catch (Exception e) {
				plugin.getLogger().severe("Error fomatting message for " + c.getSimpleName() + "." + message.getClass().getSimpleName() + ": " + e.getMessage());
			}
			//System.out.println(msg);
			if (player == null) {
				plugin.getServer().getConsoleSender().sendMessage(msg);
			} else {
				player.sendMessage(msg);
			}
		} else {
			System.out.println("message type not found");
		}
	}
}

interface MessageType {

	public int getNumberOfTags();

	public String[] getTags();

	public String getTag(int i);
}
