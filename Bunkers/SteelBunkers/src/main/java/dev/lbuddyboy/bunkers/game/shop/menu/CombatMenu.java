package dev.lbuddyboy.bunkers.game.shop.menu;

import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.shop.menu.buttons.BuyButton;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

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
public class CombatMenu extends Menu {

	private Shop shop;

	@Override
	public String getTitle(Player player) {
		return "Combat Shop";
	}
	// helm: 150 chest: 225 legs 200 boots 125 700
	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 0;

		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Helmet", 150, ItemBuilder.of(Material.DIAMOND_HELMET).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Speed II Potion", 25, ItemBuilder.of(Material.POTION).potion(PotionType.SPEED, false, true).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Fire Resistance I Potion", 75, ItemBuilder.of(Material.POTION).potion(PotionType.FIRE_RESISTANCE, true, false).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Invisibility I Potion", 125, ItemBuilder.of(Material.POTION).potion(PotionType.INVISIBILITY, false, false).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Sword", 85, ItemBuilder.of(Material.DIAMOND_SWORD).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Chestplate", 225, ItemBuilder.of(Material.DIAMOND_CHESTPLATE).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Full Diamond Set", 700, ItemBuilder.of(Material.DIAMOND).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Enderpearl", 15, ItemBuilder.of(Material.ENDER_PEARL).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Antidote", 200, ItemBuilder.of(Material.MILK_BUCKET).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Instant Health II Splash Potion", 10, ItemBuilder.of(Material.SPLASH_POTION).potion(PotionType.INSTANT_HEAL, false, true).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Poison I Splash Potion", 85, ItemBuilder.of(Material.SPLASH_POTION).potion(PotionType.POISON, false, false).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Leggings", 200, ItemBuilder.of(Material.DIAMOND_LEGGINGS).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Slowness I Splash Potion", 85, ItemBuilder.of(Material.SPLASH_POTION).potion(PotionType.SLOWNESS, false, false).build(), true));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a1x Diamond Boots", 125, ItemBuilder.of(Material.DIAMOND_BOOTS).build(), false));
		buttons.add(new BuyButton(player, shop.getSlots()[i++], "&a16x Steak", 50, ItemBuilder.of(Material.COOKED_BEEF, 16).build(), false));

		return buttons;
	}

	@Override
	public boolean autoFill() {
		return true;
	}

	@Override
	public int getSize(Player player) {
		return 54;
	}
}
