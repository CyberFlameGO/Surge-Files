package dev.lbuddyboy.hub.games;

import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.BunkersGame;
import dev.lbuddyboy.communicate.GameState;
import dev.lbuddyboy.communicate.Team;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.ItemBuilder;
import dev.lbuddyboy.hub.util.TimeUtils;
import dev.lbuddyboy.hub.util.menu.Button;
import dev.lbuddyboy.hub.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/04/2022 / 4:53 PM
 * Bunkers-Hub / services.xenlan.hub.games
 */
public class GamesMenu extends PagedMenu {

    public String getFancyState(GameState state) {
        if (state == GameState.ENDING) {
            return CC.translate("&cEnding...");
        }
        if (state == GameState.COUNTING) {
            return CC.translate("&eStarting...");
        }
        if (state == GameState.WAITING) {
            return CC.translate("&bWaiting...");
        }
        if (state == GameState.ACTIVE) {
            return CC.translate("&aRunning...");
        }
        if (state == GameState.ENDED) {
            return CC.translate("&4Ended");
        }
        return state.name();
    }

    @Override
    public String getPageTitle(Player player) {
        return "Bunkers Games";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (BunkersGame game : BunkersCom.getInstance().getBunkersGames()) {
            buttons.add(new LiveBunkersButton(game));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        if (BunkersCom.getInstance().getBunkersGames().isEmpty()) {
            buttons.add(Button.fromItem(new ItemBuilder(Material.BARRIER)
                    .setDisplayName("&cNo games found...")
                    .create(), 5));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class LiveBunkersButton extends Button {

        private BunkersGame game;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>();

            if (game.getGameState() == GameState.ACTIVE || game.getGameState() == GameState.ENDED || game.getGameState() == GameState.ENDING) {
                long diff = (game.getStartedAt() + 300_000L) - System.currentTimeMillis();
                if (diff <= 0) {
                    lore = new ArrayList<>(Arrays.asList(
                            " ",
                            " &fꜱᴛᴀᴛᴇ: &g" + getFancyState(game.getGameState()),
                            " &fɢᴀᴍᴇ ᴛɪᴍᴇ: &g" + TimeUtils.formatLongIntoMMSS(((System.currentTimeMillis() - game.getStartedAt()) / 1000)),
                            " &fᴘʟᴀʏᴇʀꜱ: &g" + game.getPlayersLeft(),
                            " &fᴋᴏᴛʜ: &g" + TimeUtils.formatIntoMMSS(game.getKothTime()),
                            " "
                    ));

                    lore.add(" &g&lᴛᴇᴀᴍꜱ");
                    for (Team team : game.getTeams()) {
                        if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
                        lore.add("  " + team.getDisplay() + "&f: " + team.getColor() + team.getDTRFormatted());
                    }

                } else {
                    String form = TimeUtils.formatLongIntoMMSS(diff / 1000);

                    lore = new ArrayList<>(Arrays.asList(
                            " ",
                            " &fꜱᴛᴀᴛᴇ: &g" + getFancyState(game.getGameState()),
                            " &fɢᴀᴍᴇ ᴛɪᴍᴇ: &g" + TimeUtils.formatLongIntoMMSS(((System.currentTimeMillis() - game.getStartedAt()) / 1000)),
                            " &fᴘʟᴀʏᴇʀꜱ: &g" + game.getPlayersLeft(),
                            " &fᴋᴏᴛʜ ɪɴ: &g" + form,
                            " "
                    ));


                    lore.add(" &g&lᴛᴇᴀᴍꜱ");
                    for (Team team : game.getTeams()) {
                        if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
                        lore.add("  " + team.getDisplay() + "&f: " + team.getColor() + team.getDTRFormatted());
                    }

                }
                lore.add(" ");
                lore.add(" &7Click to spectate this game!");
                lore.add(" ");
            } else {
                lore = Arrays.asList(
                        " ",
                        " &fꜱᴛᴀᴛᴇ: &g" + getFancyState(game.getGameState()),
                        " &fᴘʟᴀʏᴇʀꜱ: &g" + game.getPlayersLeft(),
                        " ",
                        " &7Click to play this game!",
                        " "
                );
            }

            return new ItemBuilder(Material.END_CRYSTAL)
                    .setDisplayName("&g&l" + game.getName())
                    .setLore(lore)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            player.chat("/joinq " + game.getName());
        }
    }

}
