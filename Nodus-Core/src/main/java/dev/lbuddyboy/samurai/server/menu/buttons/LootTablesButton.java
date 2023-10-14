package dev.lbuddyboy.samurai.server.menu.buttons;

import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/02/2022 / 2:37 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.server.menu.buttons
 */

@AllArgsConstructor
public class LootTablesButton extends Button {

	private Menu menu;
	private ItemStack stack;

	@Override
	public String getName(Player player) {
		return null;
	}

	@Override
	public List<String> getDescription(Player player) {
		return null;
	}

	@Override
	public Material getMaterial(Player player) {
		return null;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemStack stack = this.stack.clone();
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

		ItemMeta meta = stack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		menu.openMenu(player);
	}
}
