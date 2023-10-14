package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("forceleader")
@CommandPermission("foxtrot.team.forceleader")
public class ForceLeaderCommand extends BaseCommand {


    @Default
    @Description("Force a player to be the leader of a team")
    public static void forceLeader(Player sender, @Name("target") UUID player) {
        Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (playerTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "That player is not on a team.");
            return;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player);

        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you leader of §b" + playerTeam.getName() + "§e.");
        }

        playerTeam.setOwner(player);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + playerTeam.getName() + ChatColor.YELLOW + ".");
    }

}