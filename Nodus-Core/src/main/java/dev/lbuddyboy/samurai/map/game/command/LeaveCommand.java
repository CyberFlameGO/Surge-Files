package dev.lbuddyboy.samurai.map.game.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("leave")
public class LeaveCommand extends BaseCommand {

    @Default
    public static void execute(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (!ongoingGame.isPlaying(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in the ongoing event!");
            return;
        }

        ongoingGame.removePlayer(player);
    }

}
