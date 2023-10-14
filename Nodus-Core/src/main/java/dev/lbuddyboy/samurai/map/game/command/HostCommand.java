package dev.lbuddyboy.samurai.map.game.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.map.game.menu.HostMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("host")
public class HostCommand extends BaseCommand {

    @Default
    public static void execute(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't host an event while spawn-tagged!");
            return;
        }

        new HostMenu().openMenu(player);
    }

}
