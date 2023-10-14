package dev.lbuddyboy.bunkers.event;

import dev.lbuddyboy.bunkers.Bunkers;
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
public class GameCountdownTickEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	@Getter @Setter private boolean cancelled;
	@Getter private int newSeconds;

	public GameCountdownTickEvent(int newSeconds) {
		this.newSeconds = newSeconds;
	}

	public void setNewSeconds(int newSeconds) {
		this.newSeconds = newSeconds;
		Bunkers.getInstance().getGameHandler().getTask().setCountdown(newSeconds);
		Bunkers.getInstance().getGameHandler().getTask().setRemaining(newSeconds);
	}

	@Override
	public HandlerList getHandlers() {
		return (handlers);
	}

	public static HandlerList getHandlerList() {
		return (handlers);
	}

}