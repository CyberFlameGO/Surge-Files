package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
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

                Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);
                if (team != null) {
                    if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
                        sender.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                        return;
                    }
                }

                sender.sendMessage(CC.translate("&cYou cannot do this at the moment. You need to be in your claim."));

                return;
            }

            String serverName = Samurai.getInstance().getServerHandler().getServerName();

            sender.sendMessage(ChatColor.RED + serverName + " does not have a spawn command! You must walk there!");
            sender.sendMessage(ChatColor.RED + "Spawn is located at 0,0.");
        }
    }

}
