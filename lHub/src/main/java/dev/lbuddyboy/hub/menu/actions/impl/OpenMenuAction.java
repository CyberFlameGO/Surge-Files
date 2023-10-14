package dev.lbuddyboy.hub.menu.actions.impl;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.menu.CustomMenu;
import dev.lbuddyboy.hub.menu.actions.IAction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.actions
 */

@Getter
public class OpenMenuAction extends IAction<Player, String> {

	@Override
	public String getName() {
		return "OPEN_MENU";
	}

	@Override
	public void perform(Player player, String value) {
		CustomMenu menu = lHub.getInstance().getCustomMenuHandler().getMenu(value);

		if (menu == null) {
			Bukkit.getLogger().warning("Error #2056 - Found a menu with the incorrect syntax.");
			return;
		}

		menu.createMenu().openMenu(player);
	}

}
