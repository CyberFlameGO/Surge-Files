package dev.lbuddyboy.samurai.commands.menu.help;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/12/2021 / 3:39 AM
 * SteelHCF-main / com.steelpvp.hcf.command.menu.help
 */
public class UsefulCommandsHelpMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aCommands Help");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(13, new BackButton(new MainHelpMenu()));

		int i = 26;
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.CHEST)
				.displayName(CC.translate("&gReclaim"))
				.lore(CC.translate(Arrays.asList(
						"&fWant free rewards?", "&7 * &fYou can run the command &g/reclaim", "&f", "&fDoing this you receive rewards", "&7 * &fspecific to your rank on the server."
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
				.displayName(CC.translate("&dRedeem"))
				.lore(CC.translate(Arrays.asList(
						"&fWant free rewards, while supporting your favorite creator?", "&7 * &fYou can run the command &d/redeem", "&f", "&7 * &fDoing this you may select a partner on the server.", "&7 * &fYou will receive x3 &2Spring Keys&f from redeeming one of them."
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.OAK_SIGN)
				.displayName(CC.translate("&gShop"))
				.lore(CC.translate(Arrays.asList(
						"&fWant some extra items to help in your hardcore journey?", "&7 * &fYou can run the command &g/shop whilst in spawn or during SOTW", "&f", "&fDoing this you may select a partner on the server.", "&7 * &fYou will receive x3 &2Spring Keys&f from redeeming one of them."
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
				.displayName(CC.translate("&gGKits"))
				.lore(CC.translate(Arrays.asList(
						"&fWant some extra items to help in your hardcore journey?", "&7 * &fYou can run the command &g/gkit", "&f", "&fDoing this will pop up a menu that you can", "&7 * &7click any of the items, but you need a permission to access them.", "&7 * &fMake sure to check the lores to see if you have permission!"
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.BOOK)
				.displayName(CC.translate("&gBattle Pass"))
				.lore(CC.translate(Arrays.asList(
						"&fWant some extra items to help in your hardcore journey?", "&7 * &fYou can run the command &g/battlepass", "&f", "&fDoing this will pop up a menu that you can", "&7 * &7click any of the items to view what you need to do to complete the quests.", "&7 * &fThey are organized by the task the quest contains!"
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.ANVIL)
				.displayName(CC.translate("&gSettings"))
				.lore(CC.translate(Arrays.asList(
						"&fWant to toggle a feature something on/off?", "&7 * &fYou can run the command &g/settings", "&f", "&fDoing this will pop up a menu that you can", "&7 * &7click any of the options to toggle them on/off."
				)))
				.build()));

		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return 54;
	}
}
