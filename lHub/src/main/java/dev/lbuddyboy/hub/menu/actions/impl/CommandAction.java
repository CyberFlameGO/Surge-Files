package dev.lbuddyboy.hub.menu.actions.impl;

import dev.lbuddyboy.hub.menu.actions.IAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.actions
 */

@Getter
public class CommandAction extends IAction<Player, String> {

	@Override
	public String getName() {
		return "COMMAND";
	}

	@Override
	public void perform(Player player, String value) {
		player.chat("/" + value);
	}
}
