package dev.lbuddyboy.hub.menu.actions;

import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:15 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item
 */
public abstract class IAction<T, V> {

	public abstract String getName();
	public abstract void perform(Player player, String value);

}
