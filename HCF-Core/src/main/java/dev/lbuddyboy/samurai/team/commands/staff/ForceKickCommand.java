package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("forcekick")
@CommandPermission("foxtrot.team.forcekick")
public class ForceKickCommand extends BaseCommand {

    @Default
    @Description("Force a player to leave your team")
    public static void forceKick(Player sender, @Name("target") UUID player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                        team.getFocusedTeam().getName() + "'s HQ",
                        team.getFocusedTeam().getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }

            if (team.getTeamRally() != null) {
                LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                ));
            }

            if (team.getHQ() != null) {
                LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                        "HQ",
                        team.getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }
        }

        team.removeMember(player);
        Samurai.getInstance().getTeamHandler().setTeam(player, null);

        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.RED + "You were kicked from your team by a staff member.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " from their team, " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}