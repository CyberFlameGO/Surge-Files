package dev.lbuddyboy.samurai.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketStaffModState;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.lunarclient.bukkitapi.object.StaffModule;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.listener.EndListener;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.impl.FoxtrotNametagProvider;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

/**
 * Created by vape on 10/31/2020 at 3:36 PM.
 */
public class LunarListener implements Listener {

    private final Location spawn;

    public LunarListener() {
        this.spawn = Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 0, 0.5);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        updateWaypoints(event.getPlayer());
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
                }
            }.runTaskLater(Samurai.getInstance(), 1);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.LUNAR_WAYPOINTS)) {
            return;
        }
        Player player = event.getPlayer();

        removeWaypoints(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Toggle server rules
        updateWaypoints(player);

        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.LUNAR_NAMETAGS)) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(other -> {
            FoxtrotNametagProvider.updateLunarTag(player, other);
            FoxtrotNametagProvider.updateLunarTag(other, player);
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("/h") || event.getMessage().equals("/modmode") || event.getMessage().equals("/mod") || event.getMessage().equals("/staff") || event.getMessage().equals("/staffmode")) {
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(player);
                FrozenNametagHandler.reloadOthersFor(player);
            }, 10);
        }
    }

    public static void removeWaypoints(Player player) {
        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive()) continue;
            Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getName());
            if (team == null) return;
            if (team.hasDTRBitmask(DTRBitmask.CITADEL)) {
                if (team.getHQ() == null) continue;
                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.MAGENTA.hashCode(), false, true);
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            } else if (team.hasDTRBitmask(DTRBitmask.CONQUEST)) {
                if (team.getHQ() == null) continue;

                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.RED.hashCode(), false, true);
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            } else if (team.hasDTRBitmask(DTRBitmask.KOTH)) {
                if (team.getHQ() == null) continue;

                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.YELLOW.hashCode(), false, true);
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            }
        }

        LunarClientAPI.getInstance().sendPacket(player, new LCPacketServerRule(ServerRule.SERVER_HANDLES_WAYPOINTS, true));

        // Send spawn waypoint
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            Team glowstone = Samurai.getInstance().getTeamHandler().getTeam("Glowstone");
            if (glowstone != null) {
                LCWaypoint waypoint = new LCWaypoint("Glowstone Mountain", glowstone.getHQ(), Color.ORANGE.hashCode(), false, true);
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            }
            Team cavern = Samurai.getInstance().getTeamHandler().getTeam("Cavern");
            if (cavern != null) {
                LCWaypoint waypoint = new LCWaypoint("Cavern", cavern.getHQ(), Color.cyan.hashCode(), false, true);
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            }
        }

        if (EndListener.getEndExit() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint("End Exit", EndListener.getEndExit(), Color.DARK_GRAY.hashCode(), false, true));
        }

        if (EndListener.getCreepers() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint("Creeper Spawners", EndListener.getCreepers(), Color.green.hashCode(), false, true));
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team != null && team.getHQ() != null) {
            LCWaypoint waypoint = new LCWaypoint("HQ", team.getHQ(), Color.BLUE.hashCode(), false, true);
            LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
        }
    }
    public static void updateWaypoints(Player player) {

        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.LUNAR_WAYPOINTS)) {
            return;
        }

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive()) continue;
            Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getName());
            if (team == null) return;
            if (team.hasDTRBitmask(DTRBitmask.CITADEL)) {
                if (team.getHQ() == null) continue;
                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.MAGENTA.hashCode(), false, true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            } else if (team.hasDTRBitmask(DTRBitmask.CONQUEST)) {
                if (team.getHQ() == null) continue;

                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.RED.hashCode(), false, true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            } else if (team.hasDTRBitmask(DTRBitmask.KOTH)) {
                if (team.getHQ() == null) continue;

                LCWaypoint waypoint = new LCWaypoint(team.getName(), team.getHQ(), Color.YELLOW.hashCode(), false, true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            }
        }

        LunarClientAPI.getInstance().sendPacket(player, new LCPacketServerRule(ServerRule.SERVER_HANDLES_WAYPOINTS, true));

        // Send spawn waypoint

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            Team glowstone = Samurai.getInstance().getTeamHandler().getTeam("Glowstone");
            if (glowstone != null) {
                LCWaypoint waypoint = new LCWaypoint("Glowstone Mountain", glowstone.getHQ(), Color.ORANGE.hashCode(), false, true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            }
            Team cavern = Samurai.getInstance().getTeamHandler().getTeam("Cavern");
            if (cavern != null) {
                LCWaypoint waypoint = new LCWaypoint("Cavern", cavern.getHQ(), Color.cyan.hashCode(), false, true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            }
        }

        if (EndListener.getEndExit() != null) {
            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("End Exit", EndListener.getEndExit(), Color.DARK_GRAY.hashCode(), false, true));
        }

        if (EndListener.getCreepers() != null) {
            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Creeper Spawners", EndListener.getCreepers(), Color.green.hashCode(), false, true));
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team != null && team.getHQ() != null) {
            LCWaypoint waypoint = new LCWaypoint("HQ", team.getHQ(), Color.BLUE.hashCode(), false, true);
            LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
        }

    }

}