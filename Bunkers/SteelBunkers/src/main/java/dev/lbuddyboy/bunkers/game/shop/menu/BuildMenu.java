package dev.lbuddyboy.bunkers.game.shop.menu;

import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.shop.menu.buttons.BuyButton;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/03/2022 / 10:47 AM
 * SteelBunkers / com.steelpvp.bunkers.game.shop
 */

@AllArgsConstructor
public class BuildMenu extends Menu {

	private Shop shop;

	@Override
	public String getTitle(Player player) {
		return "Build Shop";
	}
	// helm: 150 chest: 225 legs 200 boots 125 700
	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 0;

		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Stone", 40, ItemBuilder.of(Material.STONE, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Cobblestone", 40, ItemBuilder.of(Material.COBBLESTONE, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Stone Bricks", 40, ItemBuilder.of(Material.STONE_BRICKS, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Stone Brick Slabs", 40, ItemBuilder.of(Material.STONE_BRICK_SLAB, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Chests", 75, ItemBuilder.of(Material.CHEST, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Ladder", 50, ItemBuilder.of(Material.LADDER, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Stone Buttons", 50, ItemBuilder.of(Material.STONE_BUTTON, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Fence Gates", 50, ItemBuilder.of(Material.OAK_FENCE_GATE, 16).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Pickaxe", 65, ItemBuilder.of(Material.DIAMOND_PICKAXE, 1).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Axe", 65, ItemBuilder.of(Material.DIAMOND_AXE, 1).build(), false));

		return buttons;
	}

	@Override
	public boolean autoFill() {
		return true;
	}
}
