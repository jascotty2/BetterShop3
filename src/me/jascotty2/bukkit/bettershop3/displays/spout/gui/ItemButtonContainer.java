/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description:
 * generic container with a button and itemWidget
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
package me.jascotty2.bukkit.bettershop3.displays.spout.gui;

import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;

public abstract class ItemButtonContainer extends GenericContainer {

	final GenericButton marketButton = new GenericButton();
	final GenericItemWidget picItem = new GenericItemWidget();
	final int itemId;
	final short itemData;
	final String itemName;
	boolean displayImage;

	public ItemButtonContainer(int id, short data, String name, boolean displayImage) {
		this.displayImage = displayImage;
		itemId = id;
		itemData = data;
		itemName = name;

		if (displayImage) {
			picItem.setTypeId(id).setData(data).setVisible(true);
		} else {
			picItem.setTypeId(1).setVisible(false);
		}
		picItem.setDirty(true);
		marketButton.setPriority(RenderPriority.High);

		this.children.add(marketButton);
		this.children.add(picItem);
//		for (Widget child : children) {
//			child.setContainer(this);
//		}
	}

	public GenericButton getButton() {
		return marketButton;
	}

	public int getID() {
		return itemId;
	}

	public short getData() {
		return itemData;
	}

	public Widget setEnabled(boolean enable) {
		if (marketButton.isVisible() != enable) {
			marketButton.setEnabled(enable).setDirty(true);
			setVisible(enable);
		}
		return this;
	}

	@Override
	public Container setVisible(boolean enable) {
		//if(this.isVisible() != enable) {
		marketButton.setVisible(enable).setDirty(true);
		if (displayImage) {
			picItem.setVisible(enable).setDirty(true);
		}
		//}
		return this;
	}
} // end class ItemButton