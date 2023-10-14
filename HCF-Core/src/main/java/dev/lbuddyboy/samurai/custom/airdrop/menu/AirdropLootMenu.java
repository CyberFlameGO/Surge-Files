package dev.lbuddyboy.samurai.custom.airdrop.menu;

import dev.lbuddyboy.samurai.custom.airdrop.AirDropReward;
import dev.lbuddyboy.samurai.custom.airdrop.menu.buttons.AirdropButton;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.menu.LootTableMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 11:23 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops.menu
 */
public class AirdropLootMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Airdrop Loot";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(4, new BackButton(new LootTableMenu()));

		int i = 8;
		for (AirDropReward reward : Samurai.getInstance().getAirdropHandler().getLootTable()) {
			buttons.put(++i, new AirdropButton(reward));
		}

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
}
