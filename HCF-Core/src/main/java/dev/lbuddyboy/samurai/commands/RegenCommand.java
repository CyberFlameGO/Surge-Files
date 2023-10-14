package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("regen|dtr")
public class RegenCommand extends BaseCommand {

    @Default
    @CommandCompletion("@team")
    public static void regen(Player sender, @Name("team") @Optional Team team) {
        if (!sender.isOp()) {
            team = Samurai.getInstance().getTeamHandler().getTeam(sender);
        }

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMaxDTR() == team.getDTR()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is currently at max DTR, which is " + ChatColor.LIGHT_PURPLE + team.getMaxDTR() + ChatColor.YELLOW + ".");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your team has a max DTR of " + ChatColor.LIGHT_PURPLE + team.getMaxDTR() + ChatColor.YELLOW + ".");
        sender.sendMessage(ChatColor.YELLOW + "You are regaining DTR at a rate of " + ChatColor.LIGHT_PURPLE + team.getDTRIncrement() * 60 + "/hour" + ChatColor.YELLOW + ".");
        sender.sendMessage(ChatColor.YELLOW + "At this rate, it will take you " + ChatColor.LIGHT_PURPLE + (hrsToRegain(team) == -1 ? "Infinity" : hrsToRegain(team)) + ChatColor.YELLOW + " hours to fully gain all DTR.");

        if (team.getDTRCooldown() > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is on DTR cooldown for " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString((int) (team.getDTRCooldown() - System.currentTimeMillis()) / 1000) + ChatColor.YELLOW + ".");
        }
    }

    private static double hrsToRegain(Team team) {
        int diff = team.getMaxDTR() - team.getDTR();
        int dtrIncrement = team.getDTRIncrement();

        if (dtrIncrement == 0D) {
            return (-1);
        }

        int required = diff / dtrIncrement;
        double h = required / 60D;

        return (Math.round(10.0 * h) / 10.0);
    }

}