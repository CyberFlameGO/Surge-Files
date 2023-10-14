package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.menu.button.DTRButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DTRMenu extends Menu {

	Team team;


	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < 9; i++) {
			if (i == 2) {
				buttons.put(i, new DTRButton(team, false, false, false, true));
			} else if (i == 3) {
				buttons.put(i, new DTRButton(team, false, false, false, false));
			} else if (i == 4) {
				buttons.put(i, new DTRButton(team, false, true, false, false));

			} else if (i == 5) {
				buttons.put(i, new DTRButton(team, false, false, true, false));

			} else if (i == 6) {
				buttons.put(i, new DTRButton(team, true, false, false, false));


			} else {
				buttons.put(i, Button.placeholder(Material.LEGACY_STAINED_GLASS_PANE, (byte) 14));

			}
		}

		if (buttons.size() <= 9) {
			buttons.put(9, new BackButton(new TeamManageMenu(team)));
		} else if (buttons.size() <= 18) {
			buttons.put(18, new BackButton(new TeamManageMenu(team)));
		} else if (buttons.size() <= 27) {
			buttons.put(27, new BackButton(new TeamManageMenu(team)));
		}

		return buttons;
	}


	@Override
	public String getTitle(Player player) {
		return "Manage DTR";
	}
}