package dev.lbuddyboy.bunkers.game.shop.menu.buttons;

import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.ShopUtils;
import dev.lbuddyboy.bunkers.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class SellButton extends Button {

	private Player player;
	private int slot;
	private String id;
	private double price;
	private Material material;

	public String getName(Player player) {
		return CC.translate(this.id);
	}

	public List<String> getDescription(Player player) {
		return CC.translate(Arrays.asList(
				" ",
				" &7Left click to sell x1 of this item for &a$" + this.price + "&7.",
				" &7Right click to sell all of this item.",
				" "
		));
	}

	public Material getMaterial(Player player) {
		return material;
	}

	@Override
	public int getSlot() {
		return slot;
	}

	@Override
	public ItemStack getItem() {
		return ItemBuilder.of(getMaterial(player))
				.name(getName(player))
				.setLore(getDescription(player))
				.build();
	}

	@Override
	public void action(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();

		ShopUtils.sell(player, event.getClick(), material, this.price);
	}
}