package dev.lbuddyboy.hub.games;

import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.FinalGame;
import dev.lbuddyboy.communicate.profile.Profile;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.ItemBuilder;
import dev.lbuddyboy.hub.util.TimeUtils;
import dev.lbuddyboy.hub.util.menu.Button;
import dev.lbuddyboy.hub.util.menu.paged.PagedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/04/2022 / 7:24 AM
 * Bunkers-Hub / services.xenlan.hub.games
 */
public class GameHistoryMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Game History";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(player.getUniqueId());
        for (FinalGame game : profile.getGameHistory()) {

            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));

            buttons.add(Button.fromItem(new ItemBuilder(Material.END_CRYSTAL)
                    .setLore(Arrays.asList(
                            " ",
                            " &fYour Team: &g" + game.getTeam(),
                            " &fWinner: &c" + game.getWinner(),
                            " ",
                            " &fKills: &g" + game.getKills(),
                            " &fDeaths: &g" + game.getDeaths(),
                            " ",
                            " &fStarted At: &g" + sdf.format(new Date(game.getStartedAt())),
                            " &fEnded At: &g" + TimeUtils.formatLongIntoMMSS((game.getEndedAt() - game.getStartedAt()) / 1000),
                            " "
                    ))
                    .create(), 0));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.getGameHistory().isEmpty()) {
            buttons.add(Button.fromItem(new ItemBuilder(Material.BARRIER).setDisplayName("&cNo games found...").create(), 5));
        }

        return buttons;
    }
}
