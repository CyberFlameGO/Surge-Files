package dev.lbuddyboy.bunkers.game.shop.menu;

import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.shop.menu.buttons.EnchantButton;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
public class EnchantMenu extends Menu {

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

		buttons.add(new EnchantButton(player, shop.getSlots()[i++] + 1, "&aEnchant Hand &7(Protection 1)", 250, ItemBuilder.of(Material.ENCHANTED_BOOK).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(), Enchantment.PROTECTION_ENVIRONMENTAL, 1));
		buttons.add(new EnchantButton(player, shop.getSlots()[i++] + 1, "&aEnchant Hand &7(Unbreaking 3)", 150, ItemBuilder.of(Material.ENCHANTED_BOOK).enchant(Enchantment.DURABILITY, 3).build(), Enchantment.DURABILITY, 3));
		buttons.add(new EnchantButton(player, shop.getSlots()[i++] + 1, "&aEnchant Hand &7(Fire Aspect 1)", 2000, ItemBuilder.of(Material.ENCHANTED_BOOK).enchant(Enchantment.FIRE_ASPECT, 1).build(), Enchantment.FIRE_ASPECT, 1));
		buttons.add(new EnchantButton(player, shop.getSlots()[i++] + 1, "&aEnchant Hand &7(Sharpness 1)", 500, ItemBuilder.of(Material.ENCHANTED_BOOK).enchant(Enchantment.DAMAGE_ALL, 1).build(), Enchantment.DAMAGE_ALL, 1));
		buttons.add(new EnchantButton(player, shop.getSlots()[i++] + 1, "&aEnchant Hand &7(Feather Falling 4)", 750, ItemBuilder.of(Material.ENCHANTED_BOOK).enchant(Enchantment.PROTECTION_FALL, 4).build(), Enchantment.PROTECTION_FALL, 4));

		return buttons;
	}

	@Override
	public boolean autoFill() {
		return true;
	}

	@Override
	public int getSize(Player player) {
		return 27;
	}
}
