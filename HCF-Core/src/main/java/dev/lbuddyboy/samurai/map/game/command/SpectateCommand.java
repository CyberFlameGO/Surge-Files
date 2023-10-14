package dev.lbuddyboy.samurai.map.game.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("spectate|spec")
public class SpectateCommand extends BaseCommand {

    @Default
    public static void execute(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        if (ModUtils.isModMode(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
            return;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return;
        }

        ongoingGame.addSpectator(player);
    }

}
