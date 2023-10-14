package dev.lbuddyboy.bunkers.team.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.entity.Player;

@CommandAlias("whereami|wherethefuckami|location")
public class LocationCommand extends BaseCommand {

    @Default
    public static void whereami(Player sender) {
        for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
            if (team.getCuboid() != null) {
                if (team.getCuboid().contains(sender)) {
                    sender.sendMessage(CC.translate(Bunkers.PREFIX + "You are currently in " + team.getDisplay() + "'s &fclaim."));
                }
            }
        }
    }

}
