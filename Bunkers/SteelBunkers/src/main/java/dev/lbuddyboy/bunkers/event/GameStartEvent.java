package dev.lbuddyboy.bunkers.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:07 PM
 * SteelBunkers / com.steelpvp.bunkers.event
 */
public class GameStartEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	@Getter @Setter private boolean cancelled;

	public HandlerList getHandlers() {
		return (handlers);
	}

	public static HandlerList getHandlerList() {
		return (handlers);
	}

}