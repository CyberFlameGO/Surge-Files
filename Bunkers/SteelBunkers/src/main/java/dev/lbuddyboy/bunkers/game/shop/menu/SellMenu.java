package dev.lbuddyboy.bunkers.game.shop.menu;

import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.shop.menu.buttons.SellButton;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/03/2022 / 10:47 AM
 * SteelBunkers / com.steelpvp.bunkers.game.shop
 */

@AllArgsConstructor
public class SellMenu extends Menu {

	private Shop shop;

	@Override
	public String getTitle(Player player) {
		return "Sell Shop";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 0;

		buttons.add(new SellButton(player, shop.getSlots()[i++], "&aDiamond", 100, Material.DIAMOND));
		buttons.add(new SellButton(player, shop.getSlots()[i++], "&eGold Ingot", 45, Material.GOLD_INGOT));
		buttons.add(new SellButton(player, shop.getSlots()[i++], "&fIron Ingot", 20, Material.IRON_INGOT));
		buttons.add(new SellButton(player, shop.getSlots()[i++], "&8Coal", 10, Material.COAL));

		return buttons;
	}

	@Override
	public boolean autoUpdate() {
		return true;
	}
}
