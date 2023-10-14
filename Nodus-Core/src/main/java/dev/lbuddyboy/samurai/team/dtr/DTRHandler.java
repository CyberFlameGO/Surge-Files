package dev.lbuddyboy.samurai.team.dtr;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.team.event.TeamRegenerateEvent;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DTRHandler extends BukkitRunnable {

    private static int[] MAX_DTR = {
            2, 3, 4, 4, 5, // 1 to 5
            6, 6, 6, 6, // 6 to 10
            6, 6, 6, 6, 7, // 11 to 15
            7, 7, 7, 7, 7, // 16 to 20

            7, 7, 7, 7, 7, // 21 to 25
            7, 7, 7, 7, 7, // 26 to 30
            7, 7, 7, 7, 7, // 31 to 35
            9, 9, 9, 9, 9
    }; // Padding

    private static Set<ObjectId> wasOnCooldown = new HashSet<>();

    public static int getMaxDTR(int teamsize) {
        if (Samurai.getInstance().getServerHandler().isStaffHCF()) {
            return (teamsize == 0 ? 100 : 5 * teamsize);
        }
        return (teamsize == 0 ? 100 : MAX_DTR[teamsize - 1]);
    }

    public static boolean isOnCooldown(Team team) {
        return (team.getDTRCooldown() > System.currentTimeMillis());
    }

    public static boolean isRegenerating(Team team) {
        return (!isOnCooldown(team) && team.getDTR() != team.getMaxDTR());
    }

    public static void markOnDTRCooldown(Team team) {
        wasOnCooldown.add(team.getUniqueId());

        for (Player p : team.getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
            });
        }
    }

    @Override
    public void run() {
        Map<Team, Integer> playerOnlineMap = new HashMap<>();

        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            if (ModUtils.isInvisible(player)) {
                continue;
            }

            Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

            if (playerTeam != null && playerTeam.getOwner() != null) {
                playerOnlineMap.put(playerTeam, playerOnlineMap.getOrDefault(playerTeam, 0) + 1);
            }
        }

        for (SpoofHandler.SpoofPlayer player : Flash.getInstance().getSpoofHandler().getSpoofPlayers().values()) {

            Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player.getUuid());

            if (playerTeam != null) {
                if (playerOnlineMap.containsKey(playerTeam)) {
                    playerOnlineMap.put(playerTeam, playerOnlineMap.get(playerTeam) + 1);
                } else {
                    playerOnlineMap.put(playerTeam, 1);
                }
            }
        }

        playerOnlineMap.forEach((team, onlineCount) -> {
            try {
                // make sure (I guess?)
                if (isOnCooldown(team)) {
                    markOnDTRCooldown(team);
                    return;
                }

                if (wasOnCooldown.remove(team.getUniqueId())) {
                    team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                }
                boolean wasRaidable = team.isRaidable();
                double oldDTR = team.getDTR();

                int incrementedDtr = team.getDTR() + team.getDTRIncrement();
                int maxDtr = team.getMaxDTR();
                int newDtr = Math.min(incrementedDtr, maxDtr);
                team.setDTR(newDtr);

                final TeamRegenerateEvent teamRegenerateEvent = new TeamRegenerateEvent(team, oldDTR, team.getDTR(), wasRaidable);
                Samurai.getInstance().getServer().getPluginManager().callEvent(teamRegenerateEvent);
            } catch (Exception ex) {
                Samurai.getInstance().getLogger().warning("Error regenerating DTR for team " + team.getName() + ".");
                ex.printStackTrace();
            }
        });
    }

}
