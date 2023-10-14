package dev.lbuddyboy.hub.menu.actions.impl;

import dev.lbuddyboy.hub.listener.BungeeListener;
import dev.lbuddyboy.hub.menu.actions.IAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.actions
 */

@Getter
public class SendPlayerAction extends IAction<Player, String> {

	@Override
	public String getName() {
		return "SEND_PLAYER";
	}

	@Override
	public void perform(Player player, String value) {
		BungeeListener.sendPlayerToServer(player, value);
	}

}
