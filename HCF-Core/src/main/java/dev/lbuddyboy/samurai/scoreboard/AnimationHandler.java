/*
package dev.lbuddyboy.samurai.scoreboard;

import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Foxtrot;
import dev.lbuddyboy.samurai.commands.customtimer.CustomTimer;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Bukkit;

import java.util.List;

*/
/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/02/2022 / 4:38 PM
 * Mars / rip.orbit.mars.util
 *//*


@Getter
public class AnimationHandler {

	private String title = "<G:048FFB>&lREFORGED</G:41ACFF> &7(%online%)", footer = "&7&osteelpvp.com";

	@Setter private CustomTimer currentTimer;

	private int currentTitle = 0;
	private int currentFooter = 0;
	private int currentCustomTimer = 0;

	public AnimationHandler() {

		if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
			title = "&g&lKitMap &7" + SymbolUtil.UNICODE_VERTICAL_BAR + " &fSeason I";
		} else {
			title = "&g&lReforged &7" + SymbolUtil.UNICODE_VERTICAL_BAR + " &fMap 27";
		}

		if (Foxtrot.getInstance().getServerHandler().isStaffHCF()) {
			title = "&g&lStaff HCF &7" + SymbolUtil.UNICODE_VERTICAL_BAR + " &fMap I";
		}

//		List<String> footers = Arrays.asList(
//				"&7&osteelpvp.com"
//		);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
			List<CustomTimer> timers = CustomTimer.customTimers;

//			if (currentFooter == footers.size()) currentFooter = 0;
//
//			footer = footers.get(currentFooter++);

			if (timers.size() == 0) {
				setCurrentTimer(null);
				return;
			}

			if (currentCustomTimer >= timers.size()) currentCustomTimer = 0;

			currentTimer = timers.get(currentCustomTimer++);

		}, 0L, 5 * 20L);

	}

}
*/
