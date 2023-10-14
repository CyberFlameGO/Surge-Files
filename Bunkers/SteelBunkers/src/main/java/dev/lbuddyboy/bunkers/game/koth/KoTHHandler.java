package dev.lbuddyboy.bunkers.game.koth;

import dev.lbuddyboy.bunkers.Bunkers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/03/2022 / 11:50 AM
 * SteelBunkers / com.steelpvp.bunkers.game.koth
 */

@Getter
@Setter
public class KoTHHandler {

	private Player currentCapper;
	private Player winner;
	private int defSecs = 60 * 8;
	private int remaining = defSecs;

	public KoTHHandler() {
		new KoTHTask(this).runTaskTimerAsynchronously(Bunkers.getInstance(), 20, 20);
	}

	public boolean isFinished() {
		return remaining <= 0;
	}

}
