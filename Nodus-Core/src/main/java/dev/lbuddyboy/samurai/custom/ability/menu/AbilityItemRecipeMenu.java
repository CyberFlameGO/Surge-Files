package dev.lbuddyboy.samurai.custom.ability.menu;

import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class AbilityItemRecipeMenu extends Menu {

	private AbilityItem partnerPackage;

	@Override
	public String getTitle(Player player) {
		String packageName = ChatColor.stripColor(partnerPackage.getName());
		return "Recipe: " + packageName;
	}

	private static int[] RECIPE_SLOTS = {
			3, 4, 5,
			12, 13, 14,
			21, 22, 23
	};

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < 27; i++) {
			buttons.put(i, Button.placeholder(Material.CYAN_STAINED_GLASS_PANE));
		}

		for (int i = 0; i < 9; i++) {
			buttons.put(RECIPE_SLOTS[i], Button.placeholder(partnerPackage.getRecipeDisplay().get(i)));
		}

		buttons.put(16, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				ItemStack stack = partnerPackage.getPartnerItem();
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(meta.getDisplayName() + " ");
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

		});

		buttons.put(0, new BackButton(new AbilityItemsRecipesMenu()));

		return buttons;
	}

}
