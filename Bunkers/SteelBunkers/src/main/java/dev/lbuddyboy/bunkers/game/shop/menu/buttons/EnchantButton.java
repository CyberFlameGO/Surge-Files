package dev.lbuddyboy.bunkers.game.shop.menu.buttons;

import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.ShopUtils;
import dev.lbuddyboy.bunkers.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class EnchantButton extends Button {

	private Player player;
	private int slot;
	private String id;
	private double price;
	private ItemStack content;
	private Enchantment enchantment;
	private int level;

	@Override
	public int getSlot() {
		return slot;
	}

	public String getName(Player player) {
		return CC.translate(this.id);
	}

	public List<String> getDescription(Player player) {
		return CC.translate(Arrays.asList(
				" ",
				" &7Click to purchase this enchantment for &a$" + this.price + "&7.",
				" "
		));
	}

	public Material getMaterial(Player player) {
		return content.getType();
	}

	@Override
	public ItemStack getItem() {
		ItemBuilder builder = ItemBuilder.copyOf(this.content.clone());
		builder.name(getName(player));
		builder.setLore(getDescription(player));
		builder.hideAttributes();
		return builder.build();
	}

	@Override
	public void action(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();

		ShopUtils.enchant(player, player.getInventory().getItemInMainHand(), this.enchantment, this.level, this.price);
	}
}