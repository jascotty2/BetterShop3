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
package me.jascotty2.bukkit.bettershop3.enums;

public enum ExtendedMaterials {

	OAK_WOOD(5, 0),
	SPRUCE_WOOD(5, 1),
	BIRCH_WOOD(5, 2),
	JUNGLE_WOOD(5, 3),
	OAK_SAPLING(6, 0),
	SPRUCE_SAPLING(6, 1),
	BIRCH_SAPLING(6, 2),
	JUNGLE_SAPLING(6, 2),
	OAK_LOG(17, 0),
	SPRUCE_LOG(17, 1),
	BIRCH_LOG(17, 2),
	JUNGLE_LOG(17, 2),
	OAK_LEAVES(18, 0),
	SPRUCE_LEAVES(18, 1),
	BIRCH_LEAVES(18, 2),
	JUNGLE_LEAVES(18, 2),
	SANDSTONE(24, 0),
	CHISELED_SANDSTONE(23, 1),
	SMOOTH_SANDSTONE(23, 2),
	DEAD_SHRUB(31, 0),
	TALL_GRASS(31, 1),
	FERN(31, 2),
	WHITE_WOOL(35, 0),
	ORANGE_WOOL(35, 1),
	MAGENTA_WOOL(35, 2),
	LIGHT_BLUE_WOOL(35, 3),
	YELLOW_WOOL(35, 4),
	LIME_WOOL(35, 5),
	PINK_WOOL(35, 6),
	GRAY_WOOL(35, 7),
	LIGHT_GRAY_WOOL(35, 8),
	CYAN_WOOL(35, 9),
	PURPLE_WOOL(35, 10),
	BLUE_WOOL(35, 11),
	BROWN_WOOL(35, 12),
	GREEN_WOOL(35, 13),
	RED_WOOL(35, 14),
	BLACK_WOOL(35, 15),
	STONE_DOUBLE_SLAB(43, 0),
	SANDSTONE_DOUBLE_SLAB(43, 1),
	WOODEN_DOUBLE_SLAB(43, 2),
	COBBLESTONE_DOUBLE_SLAB(43, 3),
	BRICK_DOUBLE_SLAB(43, 4),
	STONE_BRICK_DOUBLE_SLAB(43, 5),
	SMOOTH_STONE_DOUBLE_SLAB(43, 6),
	STONE_SLAB(44, 0),
	SANDSTONE_SLAB(44, 1),
	WOODEN_SLAB(44, 2),
	COBBLESTONE_SLAB(44, 3),
	BRICK_SLAB(44, 4),
	STONE_BRICK_SLAB(44, 5),
	SMOOTH_STONE_SLAB(44, 6),
	STONE_MONSTER_BLOCK(97, 0),
	COBBLESTONE_MONSTER_BLOCK(97, 1),
	STONE_BRICK_MONSTER_BLOCK(97, 2),
	STONE_BRICK(98, 0),
	MOSSY_STONE_BRICK(98, 1),
	CRACKED_STONE_BRICK(98, 2),
	CHISELED_STONE_BRICK(98, 3),
	HUGE_BROWN_MUSHROOM_BLOCK(99, 0),
	// not bothering with all top pieces...
	HUGE_BROWN_MUSHROOM_STEM(99, 14),
	HUGE_BROWN_MUSHROOM_CAP(99, 15),
	// not bothering with all top pieces...
	HUGE_RED_MUSHROOM_STEM(100, 14),
	HUGE_RED_MUSHROOM_CAP(100, 15),
	OAK_DOUBLE_SLAB(125, 0),
	SPRUCE_DOUBLE_SLAB(125, 1),
	BIRCH_DOUBLE_SLAB(125, 2),
	JUNGLE_DOUBLE_SLAB(125, 3),
	OAK_SLAB(126, 0),
	SPRUCE_SLAB(126, 1),
	BIRCH_SLAB(126, 2),
	JUNGLE_SLAB(126, 3),
	// Flower Pot (140) and Head Block (144) technically use data, 
	//		but is not a 'legal' item for a player to hold
	COAL(263, 0),
	CHARCOAL(263, 1),
	INK_SAC(351, 0),
	ROSE_RED(351, 1),
	CACTUS_GREEN(351, 2),
	COCOA_BEANS(351, 3),
	LAPIS_LAZULI(351, 4),
	PURPLE_DYE(351, 5),
	CYAN_DYE(351, 6),
	LIGHT_GRAY_DYE(351, 7),
	GRAY_DYE(351, 8),
	PINK_DYE(351, 9),
	LIME_DYE(351, 10),
	DANDELION_YELLOW(351, 11),
	LIGHT_BLUE_DYE(351, 12),
	MAGENTA_DYE(351, 13),
	ORANGE_DYE(351, 14),
	BONE_MEAL(351, 15),
	// potions technically require data, but too many values to type out by hand
	POTION(373, 0),
	CREEPER_SPAWN_EGG(383, 50),
	SKELETON_SPAWN_EGG(383, 51),
	SPIDER_SPAWN_EGG(383, 52),
	GIANT_SPAWN_EGG(383, 53),
	ZOMBIE_SPAWN_EGG(383, 54),
	SLIME_SPAWN_EGG(383, 55),
	GHAST_SPAWN_EGG(383, 56),
	PIG_ZOMBIE_SPAWN_EGG(383, 57),
	ENDERMAN_SPAWN_EGG(383, 58),
	CAVE_SPIDER_SPAWN_EGG(383, 59),
	SILVERFISH_SPAWN_EGG(383, 60),
	BLAZE_SPAWN_EGG(383, 61),
	LAVA_SLIME_SPAWN_EGG(383, 62),
	ENDER_DRAGON_SPAWN_EGG(383, 63),
	WITHER_SPAWN_EGG(383, 64),
	BAT_SPAWN_EGG(383, 65),
	WITCH_SPAWN_EGG(383, 66),
	// passive mobs
	PIG_SPAWN_EGG(383, 90),
	SHEEP_SPAWN_EGG(383, 91),
	COW_SPAWN_EGG(383, 92),
	CHICKEN_SPAWN_EGG(383, 93),
	SQUID_SPAWN_EGG(383, 94),
	WOLF_SPAWN_EGG(383, 95),
	MUSHROOM_COW_SPAWN_EGG(383, 96),
	SNOW_GOLEM_SPAWN_EGG(383, 97),
	OCELOT_SPAWN_EGG(383, 98),
	IRON_GOLEM_SPAWN_EGG(383, 99),
	VILLAGER_SPAWN_EGG(383, 120),
	SKELETON_HEAD(397, 0),
	WITHER_SKELETON_HEAD(397, 1),
	ZOMBIE_HEAD(397, 2),
	HUMAN_HEAD(397, 3),
	CREEPER_HEAD(397, 4);
	
	public final static int[] idList = new int[]{
		5, 6, 17, 18, 24, 31, 35, 43, 44, 
		/*78,*/ 97, 98, 99, 100, 125, 126,
		263, 351, 373, 383, 397};
	private int id, data;

	ExtendedMaterials(int id, int data) {
		this.id = id;
		this.data = data;
	}

	public int getData() {
		return data;
	}

	public int getId() {
		return id;
	}
	
	public static boolean usesData(int id) {
		for (int i = 0; i < idList.length; ++i) {
			if (id == idList[i]) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean validData(int id, int data) {
		for(ExtendedMaterials m : values()) {
			if(m.id == id && m.data == data) {
				return true;
			}
		}
		return false;
	}
}
