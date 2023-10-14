package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.playtime.menu.PlaytimeRewardMenu;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.persist.maps.LastJoinMap;
import dev.lbuddyboy.samurai.persist.maps.PlaytimeMap;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("lastonline|lastseen")
public class LastOnlineCommand extends BaseCommand {

    @Default
    public static void playtime(Player sender, @Name("player") @Optional UUID player) {
        if (player == null) player = sender.getUniqueId();

        Player onlinePlayer = Bukkit.getPlayer(player);
        if (onlinePlayer != null) {
            if (onlinePlayer.hasPermission("foxtrot.staff") && onlinePlayer.hasMetadata("invisible")) {
                long difference = System.currentTimeMillis() - onlinePlayer.getLastPlayed();

                sender.sendMessage(CC.translate(CC.GOLD + onlinePlayer.getName() + "&e was last seen " + TimeUtils.formatIntoDetailedString((int) (difference / 1000))) + " ago.");
                return;
            }
            sender.sendMessage(CC.translate(Bukkit.getPlayer(player).getDisplayName() + "&e is currently &aonline&e."));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer == null) {
            sender.sendMessage(CC.translate("&cNo existing player found."));
            return;
        }

        long difference = System.currentTimeMillis() - offlinePlayer.getLastPlayed();

        sender.sendMessage(CC.translate(CC.GOLD + offlinePlayer.getName() + "&e was last seen " + TimeUtils.formatIntoDetailedString((int) (difference / 1000))) + " ago.");
    }

}