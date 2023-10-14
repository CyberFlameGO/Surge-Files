package dev.lbuddyboy.bunkers.game.task;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:15 PM
 * SteelBunkers / com.steelpvp.bunkers.game.task
 */

@AllArgsConstructor
public class BalanceTask extends BukkitRunnable {

	private double BALANCE_INCREMENT_AMOUNT;

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (Bunkers.getInstance().getSpectatorHandler().isSpectator(player)) continue;

			GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());
			user.addBalance(BALANCE_INCREMENT_AMOUNT);
		}
	}

}
