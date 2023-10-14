package dev.lbuddyboy.samurai.custom.ability.menu;

import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AbilityItemsMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Ability Items";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		AbilityItemHandler handler = Samurai.getInstance().getAbilityItemHandler();
		int slot = 0;

		buttons.put(13, new Button() {
			@Override
			public String getName(Player player) {
				return CC.translate("&g&lView Recipes");
			}

			@Override
			public List<String> getDescription(Player player) {
				return CC.translate(Arrays.asList(" ", "&7Click to view all of the ability item recipes", " "));
			}

			@Override
			public Material getMaterial(Player player) {
				return Material.BOOK;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				new AbilityItemsRecipesMenu().openMenu(player);
			}
		});

		for (int i = 0; i < handler.getAbilityItems().size(); i++) {
			AbilityItem partnerPackage = handler.getAbilityItems().get(i);
			buttons.put(27 + slot++, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					ItemStack stack = partnerPackage.getPartnerItem();
					stack.setAmount(1);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName(meta.getDisplayName() + " ");

					List<String> lore = meta.getLore();

					if (player.isOp()) {
						lore.add("Times Used this Map:");
						int total = 0;

						for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
							total += partnerPackage.getStatistic(offline.getUniqueId());
						}

						lore.add("- " + total);
					}

					meta.setLore(lore);
					stack.setItemMeta(meta);
					return stack;
				}

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
				public void clicked(Player player, int slot, ClickType clickType) {
					if (clickType.isRightClick() && player.isOp()) {
						InventoryUtils.addAmountToInventory(player.getInventory(), partnerPackage.getPartnerItem(), 6);
					}
				}
			});
		}
		return buttons;
	}

}
