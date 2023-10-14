package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("toggledeathmessages|toggledeathmsgs|tdm")
public class ToggleDeathMessagesCommand extends BaseCommand {

    @Default
    public static void toggledeathmessages(Player sender) {
        boolean val = !Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Death Messages!");
        Samurai.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(sender.getUniqueId(), val);
    }

}
