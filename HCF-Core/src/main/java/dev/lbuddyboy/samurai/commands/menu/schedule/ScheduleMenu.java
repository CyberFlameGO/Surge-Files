package dev.lbuddyboy.samurai.commands.menu.schedule;

import dev.lbuddyboy.samurai.commands.menu.schedule.buttons.EventScheduleButton;
import dev.lbuddyboy.samurai.commands.menu.schedule.buttons.KoTHScheduleButton;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 12:28 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.menu.schedule
 */
public class ScheduleMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Server Schedule";
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < 27; i++) {
			buttons.put(i, Button.placeholder(i % 2 == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.CYAN_STAINED_GLASS_PANE));
		}

		buttons.put(12, new EventScheduleButton());
		buttons.put(14, new KoTHScheduleButton());

		return buttons;
	}
}
