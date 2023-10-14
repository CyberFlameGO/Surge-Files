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
public class BuyButton extends Button {

	private Player player;
	private int slot;
	private String id;
	private double price;
	private ItemStack content;
	private boolean rightClickForMore;

	public String getName(Player player) {
		return CC.translate(this.id);
	}

	public List<String> getDescription(Player player) {
		if (rightClickForMore) {
			if (this.content.getType().name().contains("_POTION")) {
				return CC.translate(Arrays.asList(
						" ",
						" &7Left click to purchase this item for &a$" + this.price + "&7.",
						" &7Right click to fill your inventory of this item for &a$" + this.price + "&7.",
						" "
				));
			}
			return CC.translate(Arrays.asList(
					" ",
					" &7Left click to purchase this item for &a$" + this.price + "&7.",
					" &7Right click to purchase x16 of this item for &a$" + this.price + "&7.",
					" "
			));
		}
		return CC.translate(Arrays.asList(
				" ",
				" &7Left click to purchase this item for &a$" + this.price + "&7.",
				" "
		));
	}

	public Material getMaterial(Player player) {
		return content.getType();
	}


	@Override
	public int getSlot() {
		return slot;
	}

	@Override
	public ItemStack getItem() {
		return ItemBuilder.copyOf(this.content)
				.name(getName(player))
				.setLore(getDescription(player))
				.build();
	}

	@Override
	public void action(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		ClickType clickType = event.getClick();

		if (clickType == ClickType.RIGHT) {
			ShopUtils.buyInvFull(player, this.content, this.price);
			return;
		}
		if (this.content.getType() == Material.DIAMOND) {
			ShopUtils.buy(player, new ItemStack(Material.DIAMOND_HELMET), 150);
			ShopUtils.buy(player, new ItemStack(Material.DIAMOND_CHESTPLATE), 225);
			ShopUtils.buy(player, new ItemStack(Material.DIAMOND_LEGGINGS), 200);
			ShopUtils.buy(player, new ItemStack(Material.DIAMOND_BOOTS), 125);
			return;
		}
		ShopUtils.buy(player, this.content, this.price);
	}
}