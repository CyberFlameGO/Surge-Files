package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.commands.menu.playtime.menu.PlaytimeRewardMenu;
import dev.lbuddyboy.samurai.persist.maps.AFKMap;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.persist.maps.PlaytimeMap;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("ptime|playtime|pt")
public class PlaytimeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void playtime(Player sender, @Name("player") @Optional UUID player) {
        if (player == null) player = sender.getUniqueId();

        PlaytimeMap playtime = Samurai.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(player);
        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        if (Flash.getInstance().getSpoofHandler().getSpoofPlayers().containsKey(UUIDUtils.name(player))) {
            playtimeTime += playtime.getCurrentSession(player) / 1000;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.YELLOW + ".");
        if (sender.hasPermission("op")) {
            AFKMap afk = Samurai.getInstance().getAfkMap();
            int afkTime = (int) afk.getPlaytime(player);

            if (bukkitPlayer != null) {
                afkTime += afk.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
            }

            sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + "'s total afk time is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(afkTime) + ChatColor.YELLOW + ".");

        }
    }

    @Subcommand("settime")
    @CommandCompletion("@players")
    public static void playtime(Player sender, @Name("player") @Optional UUID player, @Name("time") String time) {
        if (player == null) player = sender.getUniqueId();

        PlaytimeMap playtime = Samurai.getInstance().getPlaytimeMap();
        playtime.setPlaytime(player, JavaUtils.parse(time));
    }

    @Subcommand("rewards|claim|claimrewards")
    public static void ptRewards(Player player) {
        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.PLAYTIME_REWARDS)) {
            player.sendMessage(CC.translate("&cThis feature is currently disabled."));
            return;
        }
        new PlaytimeRewardMenu().openMenu(player);
    }
}