/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description:
 * methods for working with & formatting strings in minecraft chat
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
package me.jascotty2.libv2.bukkit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import me.jascotty2.libv2.util.ArrayManip;
import me.jascotty2.libv2.util.Rand;
import me.jascotty2.libv2.util.Str;
import org.bukkit.ChatColor;

public class MinecraftChatStr {

	public final static int MC_CHAT_WIDTH = 325, CONSOLE_WIDTH = 70;
	public final static String MC_CHAT_WIDTH_CHARS =
			" !\"#$%&'()*+,-./"
			+ "0123456789:;<=>?"
			+ "@ABCDEFGHIJKLMNO"
			+ "PQRSTUVWXYZ[\\]^_"
			+ "'abcdefghijklmno"
			+ "pqrstuvwxyz{|}~⌂"
			+ "ÇüéâäàåçêëèïîìÄÅ"
			+ "ÉæÆôöòûùÿÖÜø£Ø×ƒ"
			+ "áíóúñÑªº¿®¬½¼¡«»";
	public final static int[] MC_CHAR_WIDTHS = {
		4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6,
		6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6,
		7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6,
		6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6,
		3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
		6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6,
		6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6,
		6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6,
		6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6,
		// not sure what tkelly made these rows for..
		8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6,
		9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
		9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9,
		8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7,
		7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};
	// chat limmitation: repetitions of characters is limmited to 119 per line
	//      so: repeating !'s will not fill a line
	public static final ChatColor[] AllRainbowColors = new ChatColor[]{
		ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.YELLOW,
		ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA,
		ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE};
	public static final ChatColor[] BrightRainbowColors = new ChatColor[]{
		ChatColor.RED, ChatColor.YELLOW,
		ChatColor.GREEN, ChatColor.AQUA,
		ChatColor.BLUE, ChatColor.LIGHT_PURPLE};
	public static String CHATCOLOR_RAINBOW = new String(new char[]{ChatColor.COLOR_CHAR, 'z'});
	// adding rainbow custom char to ChatColor.STRIP_COLOR_PATTERN
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(ChatColor.COLOR_CHAR) + "[0-9A-FK-ORZ]");
	
	public static int getStringWidth(String s) {
		int len = 0;
		if (s != null) {
			for (char c : ChatColor.stripColor(s).toCharArray()) {
				len += getCharWidth(c);
			}
		}
		return len;
	}

	public static int getCharWidth(char c) {
		int k = MC_CHAT_WIDTH_CHARS.indexOf(c);
		if (c != '\247' && k >= 0) {
			return MC_CHAR_WIDTHS[k];
		}
		return 0;
	}

	public static int getCharWidth(char c, int defaultReturn) {
		int k = MC_CHAT_WIDTH_CHARS.indexOf(c);
		if (c != '\247' && k >= 0) {
			return MC_CHAR_WIDTHS[k];
		}
		return defaultReturn;
	}
	
	
    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(final String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

	public static String trim(String str, int length) {
		if (ChatColor.stripColor(str).length() > length) {
			StringBuilder ret = new StringBuilder();
			char chars[] = str.toCharArray();
			for (int i = 0; i < chars.length; ++i) {
				if (chars[i] == ChatColor.COLOR_CHAR) {
					if (i + 1 < chars.length) {
						ret.append(chars[i]).append(chars[i + 1]);
					} else {
						break;
					}
				} else {
					ret.append(chars[i]);
					if (--length <= 0) {
						break;
					}
				}
			}
			return ret.toString();
		}
		return str;
	}

	public static String trimChatWidth(String str) {
		if (getStringWidth(str) > MC_CHAT_WIDTH) {
			int length = MC_CHAT_WIDTH;
			StringBuilder ret = new StringBuilder();
			char chars[] = str.toCharArray();
			for (int i = 0; i < chars.length && length > 0; ++i) {
				if (chars[i] == ChatColor.COLOR_CHAR) {
					if (i + 1 < chars.length) {
						ret.append(chars[i]).append(chars[i + 1]);
					} else {
						break;
					}
				} else {
					int l = getCharWidth(chars[i]);
					if (length - l >= 0) {
						ret.append(chars[i]);
					}
					length -= l;
				}
			}
			return ret.toString();
		}
		return str;
	}

	public static String rainbow(String str) {
		return rainbow(str, null, false, true, BrightRainbowColors);
	}

	public static String rainbow(String str, ChatColor... useColors) {
		return rainbow(str, null, false, true, useColors);
	}

	public static String rainbow(String str, boolean random) {
		return rainbow(str, null, random, true, BrightRainbowColors);
	}

	public static String rainbow(String str, boolean random, ChatColor... useColors) {
		return rainbow(str, null, random, true, useColors);
	}

	public static String rainbow(String str, ChatColor start, boolean random, ChatColor... useColors) {
		return rainbow(str, start, random, true, useColors);
	}

	public static String rainbow(String str, boolean random, boolean allowRepeat, ChatColor... useColors) {
		return rainbow(str, null, random, allowRepeat, useColors);
	}

	public static String rainbow(String str, ChatColor start, boolean random, boolean allowRepeat, ChatColor... useColors) {
		int i = start == null
				? (random ? Rand.RandomInt(0, useColors.length - 1) : 0)
				: ArrayManip.indexOf(useColors, start);
		if (i == -1) {
			i = 0;
		}
		if (!allowRepeat && useColors.length <= 2) {
			random = false;
		}
		StringBuilder ret = new StringBuilder();
		boolean firstChar = false;
		for (char c : ChatColor.stripColor(str).toCharArray()) {
			if (!Character.isSpaceChar(c)) {
				if (!firstChar) {
					firstChar = true;
				} else if (random) {
					if (allowRepeat) {
						i = Rand.RandomInt(0, useColors.length - 1);
					} else {
						int last = i;
						while (last == i) {
							i = Rand.RandomInt(0, useColors.length - 1);
						}
					}
				} else if (++i >= useColors.length) {
					i = 0;
				}
				ret.append(useColors[i]);
			}
			ret.append(c);
		}
		return ret.toString();
	}

	public static String formatRainbow(String str) {
		return formatRainbow(str, true);
	}

	public static String formatRainbow(String str, boolean random) {
		if (str.contains(CHATCOLOR_RAINBOW)) {
			int st = str.indexOf(CHATCOLOR_RAINBOW),
					en = str.indexOf(ChatColor.COLOR_CHAR, st + 1);
			if (en == -1) {
				en = str.length();
			}
			str = str.substring(0, st) + rainbow(str.substring(st + 2, en), null, random, false, BrightRainbowColors) + str.substring(en);
		}
		return str;
	}

	/**
	 * pads str on the right with spaces (left-align)
	 *
	 * @param str string to format
	 * @param len spaces to pad
	 * @return str with padding appended
	 */
	public static String padRight(String str, int len) {
		return padRight(str, len, ' ');
	}

	/**
	 * pads str on the right with pad (left-align)
	 *
	 * @param str string to format
	 * @param len spaces to pad
	 * @param pad character to use when padding
	 * @return str with padding appended
	 */
	public static String padRight(String str, int len, char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		return str + Str.repeat(pad, len / getCharWidth(pad, 6));
	}

	/**
	 * pads str on the right to # of pixels
	 *
	 * @param str string to format
	 * @param pad character to use when padding
	 * @param abslen pixels to space out
	 * @return
	 */
	public static String padRight(String str, char pad, int abslen) {
		abslen -= getStringWidth(str);
		return str + Str.repeat(pad, abslen / getCharWidth(pad, 6));
	}

	public static String padRight(String str) {
		int width = MC_CHAT_WIDTH - getStringWidth(str);
		return str + Str.repeat(' ', width / getCharWidth(' ', 6));
	}

	public static String padRight(String str, char pad) {
		int width = MC_CHAT_WIDTH - getStringWidth(str);
		return str + Str.repeat(pad, width / getCharWidth(pad, 6));
	}

	/**
	 * pads str on the left with pad (right-align)
	 *
	 * @param str string to format
	 * @param len spaces to pad
	 * @param pad character to use when padding
	 * @return str with padding prepended
	 */
	public static String padLeft(String str, int len, char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		return Str.repeat(pad, len / getCharWidth(pad, 6)) + str;
	}

	/**
	 * pads str on the left to # of pixels
	 *
	 * @param str string to format
	 * @param pad character to use when padding
	 * @param abslen pixels to space out
	 * @return
	 */
	public static String padLeft(String str, char pad, int abslen) {
		abslen -= getStringWidth(str);
		return Str.repeat(pad, abslen / getCharWidth(pad, 6)).concat(str);
	}

	public static String padLeft(String str) {
		int width = MC_CHAT_WIDTH - getStringWidth(str);
		return Str.repeat(' ', width / getCharWidth(' ', 6)).concat(str);
	}

	public static String padLeft(String str, char pad) {
		int width = MC_CHAT_WIDTH - getStringWidth(str);
		return Str.repeat(pad, width / getCharWidth(pad, 6)).concat(str);
	}

	/**
	 * pads str on the left & right with pad (center-align)
	 *
	 * @param str string to format
	 * @param len spaces to pad
	 * @param pad character to use when padding
	 * @return str centered with pad
	 */
	public static String padCenter(String str, int len, char pad) {
		// for purposes of this function, assuming a normal char to be 6
		len *= 6;
		len -= getStringWidth(str);
		int padwid = getCharWidth(pad, 6);
		int prepad = (len / padwid) / 2;
		len -= prepad * padwid;
		return Str.repeat(pad, prepad) + str + Str.repeat(pad, len / padwid);
	}

	/**
	 * pads str on the left & right to # of pixels with pad (center-align)
	 *
	 * @param str string to format
	 * @param pad character to use when padding
	 * @param abslen pixels to make the result string
	 * @return
	 */
	public static String padCenter(String str, char pad, int abslen) {
		abslen -= getStringWidth(str);
		int padwid = getCharWidth(pad, 6);
		int prepad = (abslen / padwid) / 2;
		abslen -= prepad * padwid;
		return Str.repeat(pad, prepad) + str + Str.repeat(pad, abslen / padwid);
	}

	public static String padCenter(String str, char pad) {
		int width = MC_CHAT_WIDTH - getStringWidth(str);
		int padwid = getCharWidth(pad, 6);
		int prepad = (width / padwid) / 2;
		width -= prepad * padwid;
		return Str.repeat(pad, prepad) + str + Str.repeat(pad, width / padwid);
	}

	public static int strLen(String str) {
		return str == null ? -1 : ChatColor.stripColor(str).length();
	}

	public static String strWordWrap(String str) {
		return strWordWrap(str, 0, ' ');
	}

	public static String strWordWrap(String str, int tab) {
		return strWordWrap(str, tab, ' ');
	}

	public static String strWordWrap(String str, int tab, char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= MC_CHAT_WIDTH) {
				return (ret.length() > 0 ? ret + "\n" + ChatColor.getLastColors(ret) + Str.repeat(tabChar, tab) : "").concat(str);
			}
			String line1 = trimChatWidth(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			//ret += strPadRightChat((ret.length() > 0 ? unformattedStrRepeat(tabChar, tab) : "") + str.substring(0, lastPos));
			ret += (ret.length() > 0 ? "\n" + Str.repeat(tabChar, tab) + ChatColor.getLastColors(ret) : "") + str.substring(0, lastPos);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	public static String strWordWrapRight(String str, int tab) {
		return strWordWrapRight(str, tab, ' ');
	}

	/**
	 * right-aligns paragraphs
	 *
	 * @param str
	 * @param tab
	 * @param tabChar
	 * @return
	 */
	public static String strWordWrapRight(String str, int tab, char tabChar) {
		String ret = "";
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= MC_CHAT_WIDTH) {
				return (ret.length() > 0 ? ret + "\n" + ChatColor.getLastColors(ret) : "").concat(padLeft(str, tabChar));
			}
			String line1 = trimChatWidth(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			//ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" + ChatColor.getLastColors(ret) : "") + padLeft(str.substring(0, lastPos), tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	/**
	 * will left-align the start of the string until sepChar, then right-align
	 * the remaining paragraph
	 *
	 * @param str
	 * @param tab
	 * @param tabChar
	 * @param sepChar
	 * @return
	 */
	public static String strWordWrapRight(String str, int tab, char tabChar, char sepChar) {
		String ret = "";
		String line1 = trimChatWidth(str);
		// first run the first left & right align
		if (line1.contains("" + sepChar)) {
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			int sepPos = line1.indexOf(sepChar) + 1;
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			} else if (sepPos > lastPos) {
				lastPos = sepPos;
			}
			ret += str.substring(0, sepPos);
			ret += padLeft(str.substring(sepPos, lastPos), ' ', MC_CHAT_WIDTH - getStringWidth(ret));
			str = str.substring(lastPos + 1);
		}
		while (str.length() > 0) {
			// find last char of first line
			if (getStringWidth(str) <= MC_CHAT_WIDTH) {
				return (ret.length() > 0 ? ret + "\n" + ChatColor.getLastColors(ret) : "").concat(padLeft(str, tabChar));
			}
			line1 = trimChatWidth(str);
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			}
			//ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" + ChatColor.getLastColors(ret) : "") + padLeft(str.substring(0, lastPos), tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

	/**
	 * will left-align the start of the string until sepChar, then right-align
	 * the remaining paragraph
	 *
	 * @param str
	 * @param width
	 * @param tab
	 * @param tabChar
	 * @param sepChar
	 * @return
	 */
	public static String strWordWrapRight(String str, int width, int tab, char tabChar, char sepChar) {
		String ret = "";
		String line1 = trim(str, width);
		// first run the first left & right align
		if (line1.contains("" + sepChar)) {
			int lastPos = line1.length() - (ret.length() > 0 ? tab + 1 : 1);
			int sepPos = line1.indexOf(sepChar) + 1;
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 && line1.length() > tab + 1 ? tab + 1 : 1);
			} else if (sepPos > lastPos) {
				lastPos = sepPos;
			}
			ret += str.substring(0, sepPos);
			ret += padLeft(str.substring(sepPos, lastPos), ' ', width - strLen(ret));
			str = str.substring(lastPos + 1);
		}
		while (str.length() > 0) {
			// find last char of first line
			if (strLen(str) <= width) {
				return (ret.length() > 0 ? ret + "\n" + ChatColor.getLastColors(ret) : "").concat(Str.padLeft(str, width, tabChar));
			}
			line1 = trimChatWidth(str);
			int lastPos = line1.length() - (ret.length() > 0 && line1.length() > tab + 1 ? tab + 1 : 1);
			while (lastPos > 0 && line1.charAt(lastPos) != ' ') {
				--lastPos;
			}
			if (lastPos == 0) {
				lastPos = line1.length() - (ret.length() > 0 && line1.length() > tab + 1 ? tab + 1 : 1);
			}
			//ret += strPadLeftChat(str.substring(0, lastPos), tabChar);
			ret += (ret.length() > 0 ? "\n" + ChatColor.getLastColors(ret) : "") + Str.padLeft(str.substring(0, lastPos), width, tabChar);
			str = str.substring(lastPos + 1);
		}
		return ret;
	}

//	private static boolean containsAlignTag(String str, String tag) {
//		int pos = str.indexOf("<" + tag);
//		if (pos >= 0) {
//			int len = ("<" + tag).length();
//			return str.length() > pos + len + 1
//					&& (str.charAt(pos + len) == '>'
//					|| str.charAt(pos + len + 1) == '>');
//		}
//		return false;
//	}
//
//	private static boolean containsAlignTag(List<String> input, String tag) {
//		for (String l : input) {
//			if (containsAlignTag(l, tag)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	/**
//	 * UNTESTED: DON'T USE YET
//	 */
//	public static String alignTags(String input, boolean minecraftChatFormat) {
//		for (String fm : new String[]{"l", "r", "c"}) {
//			while (containsAlignTag(input, fm)) {
//				char repl = ' ';
//				if (input.matches("^.*<" + fm + ".>.*$")) {
//					repl = input.substring(input.indexOf("<" + fm) + 2).charAt(0);
//					input = input.replaceFirst("<" + fm + ".>", "<" + fm + ">");
//				}
//
//				if (fm.equals("l")) {
//					if (minecraftChatFormat) {
//						input = padRight(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					} else {
//						input = Str.padRight(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					}
//				} else if (fm.equals("c")) {
//					if (minecraftChatFormat) {
//						input = padCenter(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					} else {
//						input = Str.padCenter(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					}
//				} else {
//					if (minecraftChatFormat) {
//						input = padLeft(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					} else {
//						input = Str.padLeft(input.substring(0, input.indexOf("<" + fm + ">")), input.indexOf("<" + fm + ">"), repl) + input.substring(input.indexOf("<" + fm + ">") + 3);
//					}
//				}
//			}
//		}
//		return input;
//	}
//	public static List<String> alignTags(List<String> input, boolean minecraftChatFormat) {
//		if (input == null || input.isEmpty()) {
//			return input;
//		}
//		char[] repl = new char[input.size()];
//		for (String fm : new String[]{"l", "r", "c"}) {
//			while (containsAlignTag(input, fm)) {
//				for (int i = 0; i < input.size(); ++i) {
//					if (input.get(i).matches("^.*<" + fm + ".>.*$")) {// || input.get(1).matches("^.*<r.>.*$")) {
//						repl[i] = input.get(i).substring(input.get(i).indexOf("<" + fm) + 2).charAt(0); //, input.get(1).indexOf(">")
//						input.set(i, input.get(i).replaceFirst("<" + fm + ".>", "<" + fm + ">"));
//					} else {
//						repl[i] = ' ';
//					}
//				}
//				int maxPos = 0;
//				for (int i = 0; i < input.size(); ++i) {
//					if (input.get(i).indexOf("<" + fm + ">") > maxPos) {
//						maxPos = input.get(i).indexOf("<" + fm + ">");
//					}
//				}
//
//				LinkedList<String> newinput = new LinkedList<String>();
//				for (int i = 0; i < input.size(); ++i) {
//					String line = input.get(i);
//
//					if (line.indexOf("<" + fm + ">") != -1) {
//						if (fm.equals("l")) {
//							if (minecraftChatFormat) {
//								newinput.add(MinecraftChatStr.padRight(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							} else {
//								newinput.add(Str.padRight(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							}
//						} else if (fm.equals("c")) {
//							if (minecraftChatFormat) {
//								newinput.add(MinecraftChatStr.padCenter(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							} else {
//								newinput.add(Str.padCenter(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							}
//						} else {
//							if (minecraftChatFormat) {
//								newinput.add(MinecraftChatStr.padLeft(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							} else {
//								newinput.add(Str.padLeft(line.substring(0, line.indexOf("<" + fm + ">")), maxPos, repl[i]) + line.substring(line.indexOf("<" + fm + ">") + 3));
//							}
//						}
//					} else {
//						newinput.add(line);
//					}
//				}
//				input = newinput;
//			}
//		}
//		return input;
//	}
	private final static String[] tagStrings = new String[]{"<left>", "<right>", "<center>"};
	private final static SECTION_ALIGN[] tagAlignments = new SECTION_ALIGN[]{
		SECTION_ALIGN.LEFT, SECTION_ALIGN.RIGHT, SECTION_ALIGN.CENTER};

	public static String alignTags(String input, boolean minecraftChatFormat) {
		return alignTags(input, minecraftChatFormat, null);
	}

	private static String alignTags(String input, boolean minecraftChatFormat, ArrayList<SECTION_ALIGN> alignmentOrder) {
		if (input != null && !input.isEmpty()) {
			if (alignmentOrder == null) {
				alignmentOrder = getAlignOrder(input);
			}
			int sectionLength[] = new int[alignmentOrder.size() + 1];
			String block[] = new String[alignmentOrder.size() + 1];
			int i = 0;
			for (int oi = 0; oi < alignmentOrder.size(); ++oi) {
				int ti = ArrayManip.indexOf(tagAlignments, alignmentOrder.get(oi));
				int st = input.indexOf(tagStrings[ti], i);
				if (st == -1) {
					System.out.println("error for " + oi + " from " + i + ": " + alignmentOrder.get(oi) + " - " + tagStrings[ti]);
					continue;
				}
				block[oi] = i == st ? "" : input.substring(i, st);
				int len = minecraftChatFormat ? getStringWidth(block[oi]) : stripColor(block[oi]).length();//st - i;
				if (len > sectionLength[oi]) {
					sectionLength[oi] = len; // st - i;
				}
				i = st + tagStrings[ti].length();
			}
			block[alignmentOrder.size()] = input.substring(i, input.length());
			int len = minecraftChatFormat ? getStringWidth(block[alignmentOrder.size()]) : stripColor(block[alignmentOrder.size()]).length();//line.length() - i;
			if (len > sectionLength[alignmentOrder.size()]) {
				sectionLength[alignmentOrder.size()] = len;// line.length() - i;
			}

			int totalWidth = (int) ArrayManip.sum(sectionLength);
			//System.out.println("block lengths: " + Str.concatStr(sectionLength, ", ") + " total " + totalWidth);
			if (totalWidth < (minecraftChatFormat ? MC_CHAT_WIDTH : CONSOLE_WIDTH)) {
				int free = ((minecraftChatFormat ? MC_CHAT_WIDTH : CONSOLE_WIDTH) - totalWidth) / sectionLength.length;
				for (int oi = 0; oi < sectionLength.length; ++oi) {
					sectionLength[oi] += free;
				}
			}
			String line = "";
			line += minecraftChatFormat ? padRight(block[0], ' ', sectionLength[0]) : 
					Str.padRight(block[0], sectionLength[0] + (block[0].length() - stripColor(block[0]).length()), ' ');
			for (i = 1; i < block.length; ++i) {
				switch (alignmentOrder.get(i - 1)) {
					case LEFT:
						line += minecraftChatFormat ? padRight(block[i], ' ', sectionLength[i])
								: Str.padRight(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
						break;
					case RIGHT:
						line += minecraftChatFormat ? padLeft(block[i], ' ', sectionLength[i])
								: Str.padLeft(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
						break;
					case CENTER:
						line += minecraftChatFormat ? padCenter(block[i], ' ', sectionLength[i])
								: Str.padCenter(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
				}
			}
			return line;
		}
		return input;
	}
	
	public static List<String> alignTags(List<String> input, boolean minecraftChatFormat) {
		if (input != null && !input.isEmpty()) {
			// return value
			ArrayList<String> alignedLines = new ArrayList<String>();
			ArrayList<SECTION_ALIGN> alignmentOrder = getAlignOrder(input.get(0));
			// first check if all of the strings contain the same align tags
			//	- if not, each string is independent
			if (input.size() == 1 || !allSameFormat(input, alignmentOrder)) {
				for (int i = 0; i < input.size(); ++i) {
					alignedLines.add(alignTags(input.get(i), true, input.size() == 1 ? alignmentOrder : null));
				}
				return alignedLines;
			}

			// all strings are in the same block format
			int sectionLength[] = new int[alignmentOrder.size() + 1];
			ArrayList<String[]> lineBlocks = new ArrayList<String[]>();
			for (String line : input) {
				String block[] = new String[alignmentOrder.size() + 1];
				int i = 0;
				for (int oi = 0; oi < alignmentOrder.size(); ++oi) {
					int ti = ArrayManip.indexOf(tagAlignments, alignmentOrder.get(oi));
					int st = line.indexOf(tagStrings[ti], i);

					block[oi] = line.substring(i, st);
					int len = minecraftChatFormat ? getStringWidth(block[oi]) : stripColor(block[oi]).length();//st - i;
					if (len > sectionLength[oi]) {
						sectionLength[oi] = len; // st - i;
					}
					i = st + tagStrings[ti].length();
				}
				block[alignmentOrder.size()] = line.substring(i, line.length());
				int len = minecraftChatFormat ? getStringWidth(block[alignmentOrder.size()]) : stripColor(block[alignmentOrder.size()]).length();//line.length() - i;
				if (len > sectionLength[alignmentOrder.size()]) {
					sectionLength[alignmentOrder.size()] = len;// line.length() - i;
				}
				lineBlocks.add(block);
			}
			int totalWidth = (int) ArrayManip.sum(sectionLength);
			//System.out.println("block lengths: " + Str.concatStr(sectionLength, ", ") + " total " + totalWidth);
			if (totalWidth < (minecraftChatFormat ? MC_CHAT_WIDTH : CONSOLE_WIDTH)) {
				int free = ((minecraftChatFormat ? MC_CHAT_WIDTH : CONSOLE_WIDTH) - totalWidth) / sectionLength.length;
				for (int oi = 0; oi < sectionLength.length; ++oi) {
					sectionLength[oi] += free;
				}
			}
			//totalWidth = (int) ArrayManip.sum(sectionLength);
			//System.out.println("adj. block lengths: " + Str.concatStr(sectionLength, ", ") + " total " + totalWidth);
			//System.out.println("block aligns: " + Str.concatStr(alignmentOrder, ", "));
			//System.out.println("blocks[0]: " + Str.concatStr(lineBlocks.get(0), ", "));
			for (String[] block : lineBlocks) {
				String line = "";
				line += minecraftChatFormat ? padRight(block[0], ' ', sectionLength[0]) : 
						Str.padRight(block[0], sectionLength[0] + (block[0].length() - stripColor(block[0]).length()), ' ');
				for (int i = 1; i < block.length; ++i) {
					switch (alignmentOrder.get(i - 1)) {
						case LEFT:
							line += minecraftChatFormat ? padRight(block[i], ' ', sectionLength[i])
									: Str.padRight(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
							break;
						case RIGHT:
							line += minecraftChatFormat ? padLeft(block[i], ' ', sectionLength[i])
									: Str.padLeft(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
							break;
						case CENTER:
							line += minecraftChatFormat ? padCenter(block[i], ' ', sectionLength[i])
									: Str.padCenter(block[i], sectionLength[i] + (block[i].length() - stripColor(block[i]).length()), ' ');
					}
				}
				alignedLines.add(line);
			}
			return alignedLines;
		}
		return input;
	}

	private static ArrayList<SECTION_ALIGN> getAlignOrder(String line) {
		ArrayList<SECTION_ALIGN> order = new ArrayList<SECTION_ALIGN>();
		for (int i = 0; i < line.length(); ++i) {
			for (int j = 0; j < tagStrings.length; ++j) {
				if (strStart(line, i, tagStrings[j])) {
					order.add(tagAlignments[j]);
					i += tagStrings[j].length();
					break;
				} // else if (i == 0) { order.add(SECTION_ALIGN.LEFT); }
			}
		}
		return order;
	}

	private static boolean allSameFormat(List<String> input, ArrayList<SECTION_ALIGN> order) {
		int n = 0;
		if (order == null) {
			order = getAlignOrder(input.get(0));
			++n;
		}
		// now check that remainder follow this order
		for (; n < input.size(); ++n) {
			String line = input.get(n);
			int formatNum = 0;
			for (int i = 0; i < line.length(); ++i) {
				for (int j = 0; j < tagStrings.length; ++j) {
					if (strStart(line, i, tagStrings[j])) {
						if (formatNum >= order.size()
								|| tagAlignments[j] != order.get(formatNum)) {
							return false;
						}
						++formatNum;
						i += tagStrings[j].length();
						break;
					} // else if (i == 0) { ++formatNum; }
				}
			}
			if (formatNum < order.size()) {
				return false;
			}
		}
		return true;
	}

	private static boolean strStart(String s, int start, String str) {
		for (int i = start, j = 0; i < s.length() && j < str.length(); ++i, ++j) {
			if (s.charAt(i) != str.charAt(j)) {
				return false;
			} else if (j + 1 == str.length()) {
				return true;
			}
		}
		return false;
	}

	private enum SECTION_ALIGN {

		LEFT, RIGHT, CENTER
	}
}
