package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("gopple|opple|oppletime|goppletime|goppletimer|oppletimer")
public class GoppleCommand extends BaseCommand {

    @Default
    public static void gopple(Player sender) {
        if (Samurai.getInstance().getOppleMap().isOnCooldown(sender.getUniqueId())) {
            long millisLeft = Samurai.getInstance().getOppleMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "God Apple Cooldown: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
        } else {
            sender.sendMessage(ChatColor.RED + "You are not on gopple cooldown!");
        }
    }

}