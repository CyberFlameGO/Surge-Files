package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public static void spawn(Player sender) {
        if (sender.hasPermission("foxtrot.spawn") || DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
        } else {

            if (SOTWCommand.isSOTWTimer()) {
                if (!SOTWCommand.hasSOTWEnabled(sender.getUniqueId())) {
                    sender.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                    sender.sendMessage(CC.translate("&aYou have been teleported to spawn, due to the fact it is SOTW."));
                    return;
                }
            }

            if (Samurai.getInstance().getMapHandler().isKitMap()) {
                if (SpawnTagHandler.isTagged(sender)) {
                    sender.sendMessage(ChatColor.RED + "You cannot /spawn: You are combat tagged!");
                    return;
                }

                Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);
                if (team != null) {
                    Team teamAt = LandBoard.getInstance().getTeam(sender.getLocation());

                    if (teamAt != null && teamAt == team) {
                        sender.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                        return;
                    }
                }

                sender.sendMessage(CC.translate("&cYou cannot do this at the moment. You need to be in your claim."));
                return;
            }

            sender.sendMessage(ChatColor.RED + "Spawn is located at 0,0.");
        }
    }

}
