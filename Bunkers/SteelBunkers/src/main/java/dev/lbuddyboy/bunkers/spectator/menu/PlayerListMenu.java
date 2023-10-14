package dev.lbuddyboy.bunkers.spectator.menu;

import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 20/03/2022 / 8:52 PM
 * SteelBunkers / com.steelpvp.bunkers.spectator.menu
 */
public class PlayerListMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Player List";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 1;
		for (Player online : Bukkit.getOnlinePlayers()) {
			buttons.add(new PlayerListButton(online, i++));
		}

		return buttons;
	}

}
