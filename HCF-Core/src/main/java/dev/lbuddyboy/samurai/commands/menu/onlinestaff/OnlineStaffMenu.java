package dev.lbuddyboy.samurai.commands.menu.onlinestaff;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/02/2022 / 12:04 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.menu.onlinestaff
 */
public class OnlineStaffMenu extends PaginatedMenu {
	@Override
	public String getPrePaginatedTitle(Player player) {
		return "Online Staff";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.hasPermission("foxtrot.staff")) {
				buttons.put(i++, new StaffButton(online));
			}
		}

		return buttons;
	}
}
