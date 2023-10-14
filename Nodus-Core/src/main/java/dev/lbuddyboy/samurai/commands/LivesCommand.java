package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("lives")
public class LivesCommand extends BaseCommand {

    @Default
    public static void lives(CommandSender commandSender) {
        
        if (!(commandSender instanceof Player sender)) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        int shared = Samurai.getInstance().getFriendLivesMap().getLives(((Player) commandSender).getUniqueId());
        sender.sendMessage(ChatColor.GOLD + "Lives: " + ChatColor.WHITE + shared);
    }
}
