package dev.lbuddyboy.samurai.custom.shop;

import dev.lbuddyboy.samurai.commands.staff.SamuraiCommand;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/12/2021 / 9:51 AM
 * SteelHCF-main / com.steelpvp.hcf.shop
 */

@AllArgsConstructor
@Getter
public enum ShopItem {

	// Clay

	PURPLE_TERRACOTTA(100, new ItemStack(Material.PURPLE_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	MAGENTA_TERRACOTTA(100, new ItemStack(Material.MAGENTA_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	PINK_TERRACOTTA(100, new ItemStack(Material.PINK_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	LIME_TERRACOTTA(100, new ItemStack(Material.LIME_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	ORANGE_TERRACOTTA(100, new ItemStack(Material.ORANGE_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	RED_TERRACOTTA(100, new ItemStack(Material.RED_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	YELLOW_TERRACOTTA(100, new ItemStack(Material.YELLOW_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	GREEN_TERRACOTTA(100, new ItemStack(Material.GREEN_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	BLACK_TERRACOTTA(100, new ItemStack(Material.BLACK_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	GRAY_TERRACOTTA(100, new ItemStack(Material.GRAY_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	BROWN_TERRACOTTA(100, new ItemStack(Material.BROWN_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	BLUE_TERRACOTTA(100, new ItemStack(Material.BLUE_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	LIGHT_BLUE_TERRACOTTA(100, new ItemStack(Material.LIGHT_BLUE_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	LIGHT_GRAY_TERRACOTTA(100, new ItemStack(Material.LIGHT_GRAY_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),
	WHITE_TERRACOTTA(100, new ItemStack(Material.WHITE_TERRACOTTA, 64), ShopCategory.CLAY_SHOP, 0),

	// Concrete

	PURPLE_CONCRETE(100, new ItemStack(Material.PURPLE_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	MAGENTA_CONCRETE(100, new ItemStack(Material.MAGENTA_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	PINK_CONCRETE(100, new ItemStack(Material.PINK_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	LIME_CONCRETE(100, new ItemStack(Material.LIME_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	ORANGE_CONCRETE(100, new ItemStack(Material.ORANGE_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	RED_CONCRETE(100, new ItemStack(Material.RED_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	YELLOW_CONCRETE(100, new ItemStack(Material.YELLOW_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	GREEN_CONCRETE(100, new ItemStack(Material.GREEN_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	BLACK_CONCRETE(100, new ItemStack(Material.BLACK_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	GRAY_CONCRETE(100, new ItemStack(Material.GRAY_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	BROWN_CONCRETE(100, new ItemStack(Material.BROWN_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	BLUE_CONCRETE(100, new ItemStack(Material.BLUE_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	LIGHT_BLUE_CONCRETE(100, new ItemStack(Material.LIGHT_BLUE_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	LIGHT_GRAY_CONCRETE(100, new ItemStack(Material.LIGHT_GRAY_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),
	WHITE_CONCRETE(100, new ItemStack(Material.WHITE_CONCRETE, 64), ShopCategory.CONCRETE_SHOP, 0),

	// GLASS

	PURPLE_STAINED_GLASS(100, new ItemStack(Material.PURPLE_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	MAGENTA_STAINED_GLASS(100, new ItemStack(Material.MAGENTA_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	PINK_STAINED_GLASS(100, new ItemStack(Material.PINK_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	LIME_STAINED_GLASS(100, new ItemStack(Material.LIME_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	ORANGE_STAINED_GLASS(100, new ItemStack(Material.ORANGE_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	RED_STAINED_GLASS(100, new ItemStack(Material.RED_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	YELLOW_STAINED_GLASS(100, new ItemStack(Material.YELLOW_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	GREEN_STAINED_GLASS(100, new ItemStack(Material.GREEN_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	BLACK_STAINED_GLASS(100, new ItemStack(Material.BLACK_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	GRAY_STAINED_GLASS(100, new ItemStack(Material.GRAY_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	BROWN_STAINED_GLASS(100, new ItemStack(Material.BROWN_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	BLUE_STAINED_GLASS(100, new ItemStack(Material.BLUE_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	LIGHT_BLUE_STAINED_GLASS(100, new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	LIGHT_GRAY_STAINED_GLASS(100, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),
	WHITE_STAINED_GLASS(100, new ItemStack(Material.WHITE_STAINED_GLASS, 64), ShopCategory.GLASS_SHOP, 0),

	// WOOL

	PURPLE_WOOL(100, new ItemStack(Material.PURPLE_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	MAGENTA_WOOL(100, new ItemStack(Material.MAGENTA_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	PINK_WOOL(100, new ItemStack(Material.PINK_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	LIME_WOOL(100, new ItemStack(Material.LIME_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	ORANGE_WOOL(100, new ItemStack(Material.ORANGE_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	RED_WOOL(100, new ItemStack(Material.RED_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	YELLOW_WOOL(100, new ItemStack(Material.YELLOW_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	GREEN_WOOL(100, new ItemStack(Material.GREEN_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	BLACK_WOOL(100, new ItemStack(Material.BLACK_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	GRAY_WOOL(100, new ItemStack(Material.GRAY_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	BROWN_WOOL(100, new ItemStack(Material.BROWN_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	BLUE_WOOL(100, new ItemStack(Material.BLUE_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	LIGHT_BLUE_WOOL(100, new ItemStack(Material.LIGHT_BLUE_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	LIGHT_GRAY_WOOL(100, new ItemStack(Material.LIGHT_GRAY_WOOL, 64), ShopCategory.WOOL_SHOP, 0),
	WHITE_WOOL(100, new ItemStack(Material.WHITE_WOOL, 64), ShopCategory.WOOL_SHOP, 0),

	// Decoration

	ACACIA_LEAVES(1000, new ItemStack(Material.ACACIA_LEAVES, 32), ShopCategory.DECORATION_SHOP, 0),
	BIRCH_LEAVES(1000, new ItemStack(Material.BIRCH_LEAVES, 32), ShopCategory.DECORATION_SHOP, 0),
	OAK_LEAVES(1000, new ItemStack(Material.OAK_LEAVES, 32), ShopCategory.DECORATION_SHOP, 0),
	DARK_OAK_LEAVES(1000, new ItemStack(Material.DARK_OAK_LEAVES, 32), ShopCategory.DECORATION_SHOP, 0),
	JUNGLE_LEAVES(1000, new ItemStack(Material.JUNGLE_LEAVES, 32), ShopCategory.DECORATION_SHOP, 0),
	ACACIA_LOG(1000, new ItemStack(Material.ACACIA_LOG, 32), ShopCategory.DECORATION_SHOP, 0),
	BIRCH_LOG(1000, new ItemStack(Material.BIRCH_LOG, 32), ShopCategory.DECORATION_SHOP, 0),
	OAK_LOG(1000, new ItemStack(Material.OAK_LOG, 32), ShopCategory.DECORATION_SHOP, 0),
	DARK_OAK_LOG(1000, new ItemStack(Material.DARK_OAK_LOG, 32), ShopCategory.DECORATION_SHOP, 0),
	JUNGLE_LOG(1000, new ItemStack(Material.JUNGLE_LOG, 32), ShopCategory.DECORATION_SHOP, 0),
	ITEM_FRAMES(100, new ItemStack(Material.ITEM_FRAME, 32), ShopCategory.DECORATION_SHOP, 0),
	END_ROD(100, new ItemStack(Material.END_ROD, 32), ShopCategory.DECORATION_SHOP, 0),
	ARMOR_STAND(100, new ItemStack(Material.ARMOR_STAND, 32), ShopCategory.DECORATION_SHOP, 0),
	BRICK(100, new ItemStack(Material.BRICKS, 32), ShopCategory.DECORATION_SHOP, 0),
	BRICK_SLAB(100, new ItemStack(Material.BRICK_SLAB, 32), ShopCategory.DECORATION_SHOP, 0),
	BRICK_STAIRS(100, new ItemStack(Material.BRICK_STAIRS, 32), ShopCategory.DECORATION_SHOP, 0),
	BRICK_WALL(100, new ItemStack(Material.BRICK_WALL, 32), ShopCategory.DECORATION_SHOP, 0),
	END_STONE(100, new ItemStack(Material.END_STONE, 32), ShopCategory.DECORATION_SHOP, 0),
	END_STONE_BRICK_SLAB(100, new ItemStack(Material.END_STONE_BRICK_SLAB, 32), ShopCategory.DECORATION_SHOP, 0),
	END_STONE_BRICK_WALL(100, new ItemStack(Material.END_STONE_BRICK_WALL, 32), ShopCategory.DECORATION_SHOP, 0),
	END_STONE_BRICK_STAIRS(100, new ItemStack(Material.END_STONE_BRICK_STAIRS, 32), ShopCategory.DECORATION_SHOP, 0),
	END_STONE_BRICKS(100, new ItemStack(Material.END_STONE_BRICKS, 32), ShopCategory.DECORATION_SHOP, 0),
	PURPUR_BLOCK(100, new ItemStack(Material.PURPUR_BLOCK, 32), ShopCategory.DECORATION_SHOP, 0),
	PURPUR_STAIRS(100, new ItemStack(Material.PURPUR_STAIRS, 32), ShopCategory.DECORATION_SHOP, 0),
	PURPUR_PILLAR(100, new ItemStack(Material.PURPUR_PILLAR, 32), ShopCategory.DECORATION_SHOP, 0),
	PURPUR_SLAB(100, new ItemStack(Material.PURPUR_SLAB, 32), ShopCategory.DECORATION_SHOP, 0),
	CHAIN(100, new ItemStack(Material.CHAIN, 32), ShopCategory.DECORATION_SHOP, 0),
	BLACKSTONE(100, new ItemStack(Material.BLACKSTONE, 32), ShopCategory.DECORATION_SHOP, 0),
	BLACKSTONE_SLAB(100, new ItemStack(Material.BLACKSTONE_SLAB, 32), ShopCategory.DECORATION_SHOP, 0),
	BLACKSTONE_STAIRS(100, new ItemStack(Material.BLACKSTONE_STAIRS, 32), ShopCategory.DECORATION_SHOP, 0),
	GILDED_BLACKSTONE(100, new ItemStack(Material.GILDED_BLACKSTONE, 32), ShopCategory.DECORATION_SHOP, 0),
	POLISHED_BLACKSTONE(100, new ItemStack(Material.POLISHED_BLACKSTONE, 32), ShopCategory.DECORATION_SHOP, 0),
	POLISHED_BLACKSTONE_SLAB(100, new ItemStack(Material.POLISHED_BLACKSTONE_SLAB, 32), ShopCategory.DECORATION_SHOP, 0),
	POLISHED_BLACKSTONE_STAIRS(100, new ItemStack(Material.POLISHED_BLACKSTONE_STAIRS, 32), ShopCategory.DECORATION_SHOP, 0),
	POLISHED_BLACKSTONE_BRICKS(100, new ItemStack(Material.POLISHED_BLACKSTONE_BRICKS, 32), ShopCategory.DECORATION_SHOP, 0),
	CHISELED_POLISHED_BLACKSTONE(100, new ItemStack(Material.CHISELED_POLISHED_BLACKSTONE, 32), ShopCategory.DECORATION_SHOP, 0),
	BELL(100, new ItemStack(Material.BELL, 32), ShopCategory.DECORATION_SHOP, 0),
	BONE_BLOCK(100, new ItemStack(Material.BONE_BLOCK, 32), ShopCategory.DECORATION_SHOP, 0),
	BRAIN_CORAL(100, new ItemStack(Material.BRAIN_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	BUBBLE_CORAL(100, new ItemStack(Material.BUBBLE_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	DEAD_BRAIN_CORAL(100, new ItemStack(Material.DEAD_BRAIN_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	DEAD_BUBBLE_CORAL(100, new ItemStack(Material.DEAD_BUBBLE_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	DEAD_HORN_CORAL(100, new ItemStack(Material.DEAD_HORN_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	DEAD_FIRE_CORAL(100, new ItemStack(Material.DEAD_FIRE_CORAL, 32), ShopCategory.DECORATION_SHOP, 0),
	CAMPFIRE(100, new ItemStack(Material.CAMPFIRE, 32), ShopCategory.DECORATION_SHOP, 0),
	SOUL_CAMPFIRE(100, new ItemStack(Material.SOUL_CAMPFIRE, 32), ShopCategory.DECORATION_SHOP, 0),
	SOUL_TORCH(100, new ItemStack(Material.SOUL_TORCH, 32), ShopCategory.DECORATION_SHOP, 0),
	SOUL_LANTERN(100, new ItemStack(Material.SOUL_LANTERN, 32), ShopCategory.DECORATION_SHOP, 0),
	BLACK_BANNER(100, new ItemStack(Material.BLACK_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	BLUE_BANNER(100, new ItemStack(Material.BLUE_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	BROWN_BANNER(100, new ItemStack(Material.BROWN_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	CYAN_BANNER(100, new ItemStack(Material.CYAN_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	GRAY_BANNER(100, new ItemStack(Material.GRAY_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	GREEN_BANNER(100, new ItemStack(Material.GREEN_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	ORANGE_BANNER(100, new ItemStack(Material.ORANGE_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	YELLOW_BANNER(100, new ItemStack(Material.YELLOW_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	MAGENTA_BANNER(100, new ItemStack(Material.MAGENTA_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),
	PINK_BANNER(100, new ItemStack(Material.PINK_BANNER, 32), ShopCategory.DECORATION_SHOP, 0),

	// Buy

	COW_EGGS(500, new ItemStack(Material.COW_SPAWN_EGG, 16), ShopCategory.BUY, 0),
	POTATO(350, new ItemStack(Material.POTATO, 16), ShopCategory.BUY, 0),
	CARROT(350, new ItemStack(Material.CARROT, 16), ShopCategory.BUY, 0),
	MELON(350, new ItemStack(Material.MELON_SEEDS, 16), ShopCategory.BUY, 0),
	END_PORTAL_FRAME(18000, SamuraiCommand.endportalsummoner, ShopCategory.BUY, 0),
	GLISTERING_MELON(750, new ItemStack(Material.GLISTERING_MELON_SLICE, 16), ShopCategory.BUY, 0),
	NETHER_WART(750, new ItemStack(Material.NETHER_WART, 16), ShopCategory.BUY, 0),
	GHAST_TEAR(350, new ItemStack(Material.GHAST_TEAR, 16), ShopCategory.BUY, 0),
	MAGMA_CREAM(350, new ItemStack(Material.MAGMA_CREAM, 16), ShopCategory.BUY, 0),
	SLIME_BALL(350, new ItemStack(Material.SLIME_BALL, 16), ShopCategory.BUY, 0),
	FEATHER(350, new ItemStack(Material.FEATHER, 16), ShopCategory.BUY, 0),
	BLAZE_RODS(800, new ItemStack(Material.BLAZE_ROD, 16), ShopCategory.BUY, 0),
	SUGAR_CANE(800, new ItemStack(Material.SUGAR_CANE, 16), ShopCategory.BUY, 0),
	CROWBAR(10000, InventoryUtils.CROWBAR, ShopCategory.BUY, 0),
	SPAWNER_SKELETON(30000, InventoryUtils.SKELETON_SPAWNER, ShopCategory.BUY, 0),
	SPAWNER_ZOMBIE(25000, InventoryUtils.ZOMBIE_SPAWNER, ShopCategory.BUY, 0),
	SPAWNER_SPIDER(25000, InventoryUtils.SPIDER_SPAWNER, ShopCategory.BUY, 0),
	CAVE_SPAWNER_SPIDER(25000, InventoryUtils.CAVE_SPIDER_SPAWNER, ShopCategory.BUY, 0),

	// Sell

	NETHERITE_BLOCKS(3000, new ItemStack(Material.NETHERITE_BLOCK, 16), ShopCategory.SELL, 188),
	EMERALD_BLOCKS(2500, new ItemStack(Material.EMERALD_BLOCK, 16), ShopCategory.SELL, 156),
	DIAMOND_BLOCKS(2000, new ItemStack(Material.DIAMOND_BLOCK, 16), ShopCategory.SELL, 125),
	GOLD_BLOCKS(1750, new ItemStack(Material.GOLD_BLOCK, 16), ShopCategory.SELL, 109),
	LAPIS_BLOCKS(1700, new ItemStack(Material.LAPIS_BLOCK, 16), ShopCategory.SELL, 105),
	REDSTONE_BLOCKS(1650, new ItemStack(Material.REDSTONE_BLOCK, 16), ShopCategory.SELL, 103),
	IRON_BLOCKS(1500, new ItemStack(Material.IRON_BLOCK, 16), ShopCategory.SELL, 94);

	private int price;
	private ItemStack item;
	private ShopCategory category;
	private int pricePer;

}
