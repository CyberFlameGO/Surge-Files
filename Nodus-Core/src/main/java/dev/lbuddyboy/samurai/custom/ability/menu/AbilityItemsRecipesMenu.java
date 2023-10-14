package dev.lbuddyboy.samurai.custom.ability.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class AbilityItemsRecipesMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Ability Item Recipes";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		AbilityItemHandler handler = Samurai.getInstance().getAbilityItemHandler();
		int slot = 0;

		buttons.put(13, new Button() {
			@Override
			public String getName(Player player) {
				return CC.translate("&g&lView Ability Items");
			}

			@Override
			public List<String> getDescription(Player player) {
				return CC.translate(Arrays.asList(" ", "&7Click to view all of the ability items", " "));
			}

			@Override
			public Material getMaterial(Player player) {
				return Material.DIAMOND;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				new AbilityItemsMenu().openMenu(player);
			}
		});

		for (int i = 0; i < handler.getAbilityItems().size(); i++) {
			AbilityItem partnerPackage = handler.getAbilityItems().get(i);
			if (partnerPackage.getRecipe() == null) continue;
			buttons.put(27 + slot++, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					ItemStack stack = partnerPackage.getPartnerItem();
					stack.setAmount(1);
					ItemMeta meta = stack.getItemMeta();
					List<String> lore = new ArrayList<>();

					lore.add(" ");
					lore.add(CC.translate("&7Click to view the " + partnerPackage.getName() + "&7 recipe."));

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
					new AbilityItemRecipeMenu(partnerPackage).openMenu(player);
				}
			});
		}
		return buttons;
	}

}
