/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: large button menu, with the icon on the left & button on right
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.jascotty2.bukkit.bettershop3.displays.spout.gui;

public class LargeMarketMenuItem extends ItemButtonContainer {

	public static int DEF_WIDTH = 125, DEF_HEIGHT = 17;

	public LargeMarketMenuItem(int id, short dat, String name, boolean displayImage) {
		super(id, dat, name, displayImage);
		this.setWidth(DEF_WIDTH).setHeight(DEF_HEIGHT);//.setFixed(true).setAnchor(WidgetAnchor.CENTER_LEFT);
		marketButton.setHeight(height).setWidth(width - 19);
		marketButton.setText(itemName).setX(19).setY(0);
		picItem.setWidth(8).setHeight(8).setY(1);
	}

} // end class LargeMenuMarketItem
