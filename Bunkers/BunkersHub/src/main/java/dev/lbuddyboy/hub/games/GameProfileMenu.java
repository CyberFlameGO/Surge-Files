package dev.lbuddyboy.hub.games;

import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.FinalGame;
import dev.lbuddyboy.communicate.profile.Profile;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.ItemBuilder;
import dev.lbuddyboy.hub.util.TimeUtils;
import dev.lbuddyboy.hub.util.menu.Button;
import dev.lbuddyboy.hub.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/04/2022 / 7:24 AM
 * Bunkers-Hub / services.xenlan.hub.games
 */
public class GameProfileMenu extends Menu {

	@Override
	public String getTitle(Player var1) {
		return "Game Profile";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(player.getUniqueId());

		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));

		buttons.add(Button.fromItem(new ItemBuilder(Material.NETHERITE_CHESTPLATE)
						.setDisplayName("&g&lYour Bunkers Statistics")
						.setLore(Arrays.asList(
								" ",
								" &fKills: &g" + profile.getKills(),
								" &fDeaths: &g" + profile.getDeaths(),
								" ",
								" &fWins: &g" + profile.getWins(),
								" &fLosses: &g" + (profile.getGameHistory().size() - profile.getWins()),
								" &fOres Mined: &g" + profile.getOresMined(),
								" &fKoTH Captures: &g" + profile.getKothCaptures(),
								" &fGames Played: &g" + profile.getGameHistory().size(),
								" "
						))
				.create(), 5));

		return buttons;
	}

	public int getLoses(Profile profile) {
		int i = 0;
		for (FinalGame game : profile.getGameHistory()) {
			if (!game.getWinner().contains(game.getTeam())) {
				++i;
			}
		}
		return i;
	}

}
