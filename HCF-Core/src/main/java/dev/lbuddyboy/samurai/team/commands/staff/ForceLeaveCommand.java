package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("forceleave")
@CommandPermission("foxtrot.team.forceleave")
public class ForceLeaveCommand extends BaseCommand {


    @Default
    @Description("Force a player to leave a team")
    public static void forceLeave(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getUniqueId());
        Samurai.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");

        if (team.getMembers().size() == 0) {
            if (team.getName().endsWith("Road")) {
                team.setDTR(DTRBitmask.ROAD.getBitmask());
            } else if (team.getName().equals("Citadel")) {
                team.setDTR(DTRBitmask.CITADEL.getBitmask());
            } else if (team.getName().equals("Conquest")) {
                team.setDTR(DTRBitmask.CONQUEST.getBitmask());
            } else if (team.getName().equals("Vault")) {
                team.setDTR(DTRBitmask.KOTH.getBitmask());
            } else if (team.getName().equals("Glowstone")) {
                team.setDTR(DTRBitmask.ROAD.getBitmask());
            } else if (team.getName().equals("Cavern")) {
                team.setDTR(DTRBitmask.ROAD.getBitmask());
            }
        }
    }

}