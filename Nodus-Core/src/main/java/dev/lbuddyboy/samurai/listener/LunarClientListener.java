package dev.lbuddyboy.samurai.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LunarClientListener implements Listener {

    private Samurai instance;
    private World world;

    private LCWaypoint spawnWaypoint;

    public static Map<UUID, Map<String, Double>> teamViewer = new HashMap<>();

    public LunarClientListener(Samurai instance) {
        this.instance = instance;

        this.world = instance.getServer().getWorld("world");
        this.spawnWaypoint = new LCWaypoint(ChatColor.GREEN + "Spawn" + ChatColor.WHITE, this.world.getBlockAt(0, 76, 0).getLocation(), java.awt.Color.GREEN.getRGB(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        this.sendTeamUpdatePacket(player, to);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (Feature.LUNAR_TEAMS.isDisabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        this.sendTeamUpdatePacket(player, to);
    }

    public void sendTeamUpdatePacket(Player player, Location to) {
        final Map<String, Double> coords = new HashMap<>();

        coords.put("x", to.getX());
        coords.put("y", to.getY() + 4);
        coords.put("z", to.getZ());

        if (teamViewer.containsKey(player.getUniqueId())) {
            teamViewer.replace(player.getUniqueId(), coords);
        } else {
            teamViewer.put(player.getUniqueId(), coords);
        }

        final Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return;
        }

        updateTeammates(player);

        team.getOnlineMembers().forEach(LunarClientListener::updateTeammates);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Team team = instance.getTeamHandler().getTeam(player);

        updateNametag(player);

        final Location playerLocation = player.getLocation();

        if (!Feature.LUNAR_TEAMS.isDisabled()) {
            final Map<String, Double> coords = new HashMap<>();

            coords.put("x", playerLocation.getX());
            coords.put("y", playerLocation.getY() + 4);
            coords.put("z", playerLocation.getZ());

            teamViewer.put(player.getUniqueId(), coords);
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            LunarClientAPI.getInstance().sendWaypoint(player, this.spawnWaypoint);

            if (team == null) {
                return;
            }

            if (!Feature.LUNAR_TEAMS.isDisabled()) {
                team.getOnlineMembers().forEach(LunarClientListener::updateTeammates);
            }

            if (team.getHQ() != null) {
                if (team.getHomeWaypoint() == null) {
                    team.setHomeWaypoint(new LCWaypoint(ChatColor.BLUE + "HQ" + ChatColor.WHITE, team.getHQ(), java.awt.Color.BLUE.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getHomeWaypoint());
            }

            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                final Location location = team.getFocusedTeam().getHQ();

                if (team.getFocusWaypoint() == null) {
                    team.setFocusWaypoint(new LCWaypoint(ChatColor.RED + team.getFocusedTeam().getName() + "'s HQ" + ChatColor.WHITE, location, Color.RED.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getFocusWaypoint());
            }
        }, 40L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        for (Player onlinePlayer : Samurai.getInstance().getServer().getOnlinePlayers()) {
            LunarClientAPI.getInstance().resetNametag(player, onlinePlayer);
            LunarClientAPI.getInstance().resetNametag(onlinePlayer, player);
        }

        teamViewer.remove(player.getUniqueId());

        LunarClientAPI.getInstance().removeWaypoint(player, this.spawnWaypoint);

        final Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team == null) {
            return;
        }

        if (team.getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getHomeWaypoint());
        }

        if (team.getTeamRally() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getRallyWaypoint());
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getFocusWaypoint());
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {

            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                if (team != null && team.getTeamRally() != null && team.getTeamRally().getWorld().getEnvironment() == World.Environment.NETHER) {
                    if (team.getRallyWaypoint() == null) {
                        team.setRallyWaypoint(new LCWaypoint(ChatColor.GOLD + "Rally" + ChatColor.WHITE, team.getTeamRally(), java.awt.Color.ORANGE.getRGB(), true));
                    }

                    LunarClientAPI.getInstance().sendWaypoint(player, team.getRallyWaypoint());
                }
            }

            if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
                return;
            }

            if (team != null && team.getTeamRally() != null && team.getTeamRally().getWorld().getEnvironment() == World.Environment.THE_END) {
                if (team.getRallyWaypoint() == null) {
                    team.setRallyWaypoint(new LCWaypoint(ChatColor.GOLD + "Rally" + ChatColor.WHITE, team.getTeamRally(), java.awt.Color.ORANGE.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getRallyWaypoint());
            }
        }, 40L);
    }

    public static void updateTeammates(Player viewer) {
        if (Feature.LUNAR_TEAMS.isDisabled()) {
            return;
        }

        if (!LunarClientAPI.getInstance().isRunningLunarClient(viewer)) {
            return;
        }

        final Team team = Samurai.getInstance().getTeamHandler().getTeam(viewer);

        final Map<UUID, Map<String, Double>> players = new HashMap<>();

        if (team == null) {
            players.put(viewer.getUniqueId(), teamViewer.get(viewer.getUniqueId()));
            LunarClientAPI.getInstance().sendTeammates(viewer, new LCPacketTeammates(viewer.getUniqueId(), 10, players));
            return;
        }


        for (Player it : team.getOnlineMembers()) {
            if (!it.getWorld().getName().equalsIgnoreCase(viewer.getWorld().getName())) {
                continue;
            }

            players.put(it.getUniqueId(), teamViewer.get(it.getUniqueId()));
        }

        LunarClientAPI.getInstance().sendTeammates(viewer, new LCPacketTeammates(viewer.getUniqueId(), 10, players));
    }

    public static void updateNametag(Player player) {
        if (!Feature.LUNAR_TEAMS.isDisabled()) {
            updateTeammates(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onRespawn(PlayerRespawnEvent event) {
        updateNametag(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(PlayerDeathEvent event) {
        updateNametag(event.getEntity());
    }

    /*
    TODO: Glowstone ETC
     */

}