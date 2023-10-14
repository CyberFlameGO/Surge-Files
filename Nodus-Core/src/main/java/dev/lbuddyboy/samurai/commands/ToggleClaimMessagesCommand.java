package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("toggleclaimmessages|tcm")
public class ToggleClaimMessagesCommand extends BaseCommand {

    @Default
    public static void toggleClaims(Player sender) {
        boolean val = !Samurai.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Claim Messages!");
        Samurai.getInstance().getToggleClaimMessageMap().setClaimMessagesEnabled(sender.getUniqueId(), val);
    }
}