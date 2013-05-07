package me.jascotty2.bukkit.bettershop3;

import me.jascotty2.bukkit.bettershop3.database.PricelistDatabaseHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemValue {

	public int id;
	public short data;

	ItemValue() {
	}
	
	public ItemValue(ItemStack it) {
		if(it == null) {
			id = data = 0;
		} else {
			id = it.getTypeId();
			data = it.getData().getData();
		}
	}

	public ItemValue(int id) {
		if (id > PricelistDatabaseHandler.DATA_BYTES) {
			this.id = id >> PricelistDatabaseHandler.DATA_BYTE_LEN;
			this.data = (short) (id & PricelistDatabaseHandler.DATA_BYTES);
		} else {
			this.id = id;
		}
	}

	public ItemValue(int id, short data) {
		this.id = id;
		this.data = data;
	}

	public ItemValue(int id, int data) {
		this.id = id;
		this.data = (short) data;
	}

	public boolean isTool() {
		Material m = Material.getMaterial(id);
		return m != null && m.getMaxDurability() > 0;
	}

	public int toIDVal() {
		return (id << PricelistDatabaseHandler.DATA_BYTE_LEN) + data;
	}

	@Override
	public int hashCode() {
		return (id << PricelistDatabaseHandler.DATA_BYTE_LEN) + data;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ItemValue other = (ItemValue) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.data != other.data) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ItemValue{" + "id=" + id + ", data=" + data + '}';
	}
}
