package dev.lbuddyboy.samurai.custom.vaults.menu;

import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.menu.LootTableMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 11:23 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops.menu
 */
public class VaultMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Vault Loot";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(4, new BackButton(new LootTableMenu()));

		int i = 8;
		for (ItemStack stack : Samurai.getInstance().getVaultHandler().getLoottable()) {
			buttons.put(++i, new Button() {
				@Override
				public String getName(Player var1) {
					return null;
				}

				@Override
				public List<String> getDescription(Player var1) {
					return null;
				}

				@Override
				public Material getMaterial(Player var1) {
					return null;
				}

				@Override
				public ItemStack getButtonItem(Player player) {
					return ItemBuilder.copyOf(stack).addToLore(" ").build();
				}
			});
		}

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
}
