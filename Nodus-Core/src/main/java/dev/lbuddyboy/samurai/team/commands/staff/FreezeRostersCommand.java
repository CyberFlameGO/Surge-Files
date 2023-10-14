package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("freezerosters")
@CommandPermission("op")
public class FreezeRostersCommand extends BaseCommand {


    @Default
    @Description("Freeze Rosters")
    public static void freezeRosters(Player sender) {
        TeamHandler teamHandler = Samurai.getInstance().getTeamHandler();
        teamHandler.setRostersLocked(!teamHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Team rosters are now " + ChatColor.LIGHT_PURPLE + (teamHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}