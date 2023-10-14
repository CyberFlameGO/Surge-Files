package dev.lbuddyboy.bunkers.game.shop.menu;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 30/03/2022 / 9:53 PM
 * SteelBunkers / com.steelpvp.bunkers.game.shop.menu
 */
public class RepairMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Repair Items";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());
		
		int[] armorSlots = {
				10,18,19,20,28,37
		};
		
		ItemStack[] items = new ItemStack[]{
				player.getInventory().getArmorContents()[0],
				player.getInventory().getArmorContents()[1],
				player.getInventory().getArmorContents()[2],
				player.getInventory().getArmorContents()[3],
				player.getInventory().getItemInOffHand(),
				player.getInventory().getItemInMainHand()
		};
		Collections.reverse(Arrays.asList(items));
		
		int i = 0;
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getArmorContents()[3], user));
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getItemInOffHand(), user));
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getArmorContents()[2], user));
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getItemInMainHand(), user));
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getArmorContents()[1], user));
		buttons.add(new RepairIndividualButton(player, armorSlots[i++] + 1, player.getInventory().getArmorContents()[0], user));

		buttons.add(new ConfirmButton(player, user, items));
		
		return buttons;
	}

	public static double calculatePrice(ItemStack[] stacks) {
		double price = 0;

		for (ItemStack stack : stacks) {
			price += calculatePrice(stack);
		}
		
		return price;
	}

	public static double calculatePrice(ItemStack stack) {
		if (stack == null) return 0;
		if (stack.getType() == Material.AIR) return 0;
		
		int durability = stack.getDurability();
		return durability * pricePerDura;
	}
	
	private static final double pricePerDura = 0.5;

	@AllArgsConstructor
	private static class ConfirmButton extends Button {

		private Player player;
		private GameUser user;
		private ItemStack[] items;

		public String getName(Player player) {
			return CC.translate("&a&lREPAIR ALL");
		}

		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"",
					"&7Click to repair all of your items for: &2$" + calculatePrice(items),
					""
			));
		}

		public Material getMaterial(Player player) {
			if (user.hasBalance(calculatePrice(items))) {
				return Material.GREEN_WOOL;
			}
			return Material.RED_WOOL;
		}

		@Override
		public int getSlot() {
			return 23;
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

			double price = calculatePrice(items);
			if (!user.hasBalance(price)) {
				player.sendMessage(CC.translate("&cInsufficient funds..."));
				return;
			}
			user.takeBalance(price);
			for (ItemStack item : items) {
				if (item == null) continue;
				if (item.getType() == Material.AIR) continue;
				item.setDurability((short) 0);
			}
		}

	}
	
	@AllArgsConstructor
	private static class RepairIndividualButton extends Button {

		private Player player;
		private int slot;
		private ItemStack stack;
		private GameUser user;
		
		public String getName(Player player) {
			if (this.stack == null || this.stack.getType() == Material.AIR) {
				return CC.translate("&cNo item found...");
			}
			return CC.translate("&a&lREPAIR");
		}

		public List<String> getDescription(Player player) {
			if (this.stack == null || this.stack.getType() == Material.AIR) {
				return Collections.singletonList(" ");
			}
			double price = calculatePrice(stack);
			
			return CC.translate(Arrays.asList(
					"",
					"&7Click to repair all of your items for: &2$" + price,
					""
			));
		}

		public Material getMaterial(Player player) {
			if (this.stack == null || this.stack.getType() == Material.AIR) {
				return Material.RED_STAINED_GLASS_PANE;
			}
			return stack.getType();
		}
		
		@Override
		public int getSlot() {
			return slot;
		}

		@Override
		public ItemStack getItem() {
			if (this.stack == null || this.stack.getType() == Material.AIR) {
				return ItemBuilder.of(getMaterial(player))
						.name(getName(player))
						.setLore(getDescription(player))
						.build();
			}
			ItemBuilder builder = ItemBuilder.copyOf(this.stack.clone());
			builder.setLore(getDescription(player));
			builder.name(getName(player));
			return builder.build();
		}

		@Override
		public void action(InventoryClickEvent event) {
			if (!(event.getWhoClicked() instanceof Player)) return;
			Player player = (Player) event.getWhoClicked();
			
			if (this.stack == null || this.stack.getType() == Material.AIR) {
				return;
			}
			double price = calculatePrice(stack);
			if (!user.hasBalance(price)) {
				player.sendMessage(CC.translate("&cInsufficient funds..."));
				return;
			}
			user.takeBalance(price);
			this.stack.setDurability((short) 0);
		}
	}
	
}
