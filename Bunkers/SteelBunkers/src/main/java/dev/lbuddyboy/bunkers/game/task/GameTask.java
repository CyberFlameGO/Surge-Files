package dev.lbuddyboy.bunkers.game.task;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.event.GameCountdownTickEvent;
import dev.lbuddyboy.bunkers.event.GameStartEvent;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:15 PM
 * SteelBunkers / com.steelpvp.bunkers.game.task
 */

@Getter
@Setter
public class GameTask extends BukkitRunnable {

	private final List<Integer> countdownIntervals = Arrays.asList(60, 30, 15, 10, 5, 3, 2, 1);

	private int countdown;
	private int remaining;

	public GameTask(int countdown) {
		this.countdown = countdown;
		this.remaining = countdown;
	}

	@Override
	public void run() {
		if (this.remaining <= 0) {

			GameStartEvent event = new GameStartEvent();
			Bukkit.getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				cancel();
				Bukkit.broadcastMessage(Bunkers.PREFIX + "Game Started...");
			} else {
				cancel();
				Bukkit.broadcastMessage(Bunkers.PREFIX + "Game cancelled...");
			}

			return;
		}

		GameCountdownTickEvent event = new GameCountdownTickEvent(this.remaining);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			this.remaining--;
			if (countdownIntervals.contains(this.remaining)) {
				Bukkit.broadcastMessage(CC.translate(Bunkers.PREFIX + "Starting in &x&1&0&7&e&c&1" + this.remaining + " second" + (this.remaining == 1 ? "" : "s") + "..."));
			}
		}

	}

	public void start() {
		this.runTaskTimer(Bunkers.getInstance(), 0, 20L);
	}

}
