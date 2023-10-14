package dev.lbuddyboy.gkits.command.menu;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import dev.lbuddyboy.gkits.enchanter.menu.CombinedEnchanterMenu;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import dev.lbuddyboy.gkits.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/07/2021 / 9:48 PM
 * GKits / me.lbuddyboy.gkits.command.menu
 */

@AllArgsConstructor
@NoArgsConstructor
public class EnchantsMenu extends Menu {

	private boolean back = false;

	@Override
	public String getTitle(Player player) {
		return "Enchants";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		buttons.add(new ArmorButton(Material.DIAMOND_HELMET, "Helmets", 12));
		buttons.add(new ArmorButton(Material.DIAMOND_CHESTPLATE, "Chestplates", 13));
		buttons.add(new ArmorButton(Material.DIAMOND_LEGGINGS, "Leggings", 14));
		buttons.add(new ArmorButton(Material.DIAMOND_BOOTS, "Boots", 15));
		buttons.add(new ArmorButton(Material.DIAMOND_SWORD, "Swords", 16));

		buttons.add(new ArmorButton(Material.DIAMOND_PICKAXE, "Pickaxe", 22));
		buttons.add(new ArmorButton(Material.DIAMOND_AXE, "Axe", 23));
		buttons.add(new ArmorButton(Material.SHEARS, "Shears", 24));

		if (back) {
			buttons.add(new BackButton(36, new CombinedEnchanterMenu()));
		}

		return buttons;
	}

	@Override
	public int getSize(Player player) {
		return 36;
	}

	@Override
	public boolean autoFill() {
		return true;
	}

	@AllArgsConstructor
	public class ArmorButton extends Button {

		private Material material;
		private String name;
		private int slot;

		@Override
		public ItemStack getItem() {
			return new ItemBuilder(this.material)
					.setName(CC.translate("&g" + this.name + " Enchantments"))
					.setLore(CC.translate("&fClick to see all the enchantments regarding " + name.toLowerCase() + "."))
					.create();
		}

		@Override
		public int getSlot() {
			return slot;
		}

		@Override
		public void action(InventoryClickEvent event) {
			if (!(event.getWhoClicked() instanceof Player player)) return;

			new EnchantsItemMenu(name.toUpperCase()).openMenu(player);
		}

	}

}
