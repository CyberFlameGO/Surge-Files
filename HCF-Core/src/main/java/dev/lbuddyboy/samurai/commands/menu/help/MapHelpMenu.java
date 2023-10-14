package dev.lbuddyboy.samurai.commands.menu.help;

import dev.lbuddyboy.samurai.commands.menu.help.map.PotionLimitMenu;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/12/2021 / 3:39 AM
 * SteelHCF-main / com.steelpvp.hcf.command.menu.help
 */
public class MapHelpMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aMap Help");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(13, new BackButton(new MainHelpMenu()));

		int i = 26;
		buttons.put(++i, new Button() {
			@Override
			public String getName(Player var1) {
				return CC.translate("&2Potion Limit");
			}

			@Override
			public List<String> getDescription(Player var1) {
				return CC.translate(Arrays.asList(
						"&fWhat is a Potion Limit?",
						"&7 * &fThis declares what potions you can or cannot use.",
						"",
						"&7&oClick to view the potions you can or cannot use."
				));
			}

			@Override
			public Material getMaterial(Player var1) {
				return Material.SPLASH_POTION;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				new PotionLimitMenu().openMenu(player);
			}
		});

		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.TNT).displayName(CC.translate("&4TNT")).lore(CC.translate(Arrays.asList("&fIs &4TNT&f enabled?", "&7 * &4TNT&f is not enabled at any time on &bHCF&f."))).build()));

		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		int size = super.size(buttons) + 9;
		return (Math.min(size, 54));
	}
}
