package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.api.FoxConstants;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("tl|telllocation|tellloc")
public final class TellLocationCommand extends BaseCommand {

    @Default
    public static void tellLocation(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You're not in a faction!");
            return;
        }

        Location l = sender.getLocation();
        team.sendMessage(FoxConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
    }

}
