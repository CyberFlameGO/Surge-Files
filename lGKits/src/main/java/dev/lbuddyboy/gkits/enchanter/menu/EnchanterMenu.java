package dev.lbuddyboy.gkits.enchanter.menu;

import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.ItemUtils;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 11:10 PM
 * GKits / me.lbuddyboy.gkits.enchanter.menu
 */
public class EnchanterMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Enchanter";
	}

	@Override
	public int getSize(Player player) {
		return 27;
	}

	@Override
	public List<Button> getButtons(Player p) {
		List<Button> buttons = new ArrayList<>();

		buttons.add(new CEButton());

		return buttons;
	}

	@Override
	public boolean autoFill() {
		return true;
	}

	public class CEButton extends Button {

		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.NETHER_STAR).setName(CC.translate("&gEnchantment Crystal &7(Click)"))
					.setLore(CC.translate(Arrays.asList(
							"",
							"&fClick to purchase a random &gcustom enchantment &fcrystal.",
							"&7\u25b6 &fPrice: &g15 EXP Lvls",
							""
					)))
					.create();
		}

		@Override
		public int getSlot() {
			return 14;
		}

		@Override
		public void action(InventoryClickEvent event) {
			if (!(event.getWhoClicked() instanceof Player player)) return;

			if (event.getClick() != ClickType.LEFT) {
				event.setCancelled(true);
				return;
			}

			if (player.getLevel() < 15) {
				player.sendMessage(CC.translate("&cYou do not have sufficient exp to purchase an enchantment crystal."));
				return;
			}

			player.setLevel(player.getLevel() - 15);
			ItemUtils.tryFit(player, lGKits.getInstance().getEnchanterManager().getItem());
		}

	}

}
