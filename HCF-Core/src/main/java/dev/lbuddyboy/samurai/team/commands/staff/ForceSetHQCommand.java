package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("forcesethq")
@CommandPermission("foxtrot.team.forcesethq")
public class ForceSetHQCommand extends BaseCommand {

    @Default
    @Description("Force disbands a team!")
    @CommandCompletion("@team")
    public static void forceDisband(Player sender, @Name("team") Team team) {
        team.setHQ(sender.getLocation());
        sender.sendMessage(ChatColor.YELLOW + "Force set hq for the team " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}