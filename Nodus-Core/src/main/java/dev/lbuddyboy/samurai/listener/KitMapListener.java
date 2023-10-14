package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class KitMapListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();

        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;

        // 1. killer should not be null
        // 2. victim should not be equal to killer
        // 3. victim should not be naked
        Player killer = victim.getKiller();
        if (killer != null && !victim.getUniqueId().equals(killer.getUniqueId()) && !InventoryUtils.isNaked(victim)) {

            String killerName = killer.getName();
            FrozenEconomyHandler.deposit(killer.getUniqueId(), 100 + getAdditional(killer));
            killer.sendMessage(ChatColor.RED + "You received a reward for killing " + ChatColor.GREEN + victim.getName() + ChatColor.RED + ".");

            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                int kills = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim.getKiller()).getKills();
                if (kills % 5 == 0) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate give Surge " + killerName + " 1");
                    victim.getKiller().sendMessage(ChatColor.GREEN + "You received a Surge Key for 5 kills!");
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate give Kill " + killerName + " 1");
            });
        }
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        if (event.getPlayer().hasMetadata("modmode") || event.getPlayer().hasMetadata("invisible")) return;

        if (event.getFrom().getEnvironment() == Environment.THE_END && event.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        } else if (event.getFrom().getEnvironment() == Environment.THE_END && event.getPlayer().getWorld().getEnvironment() == Environment.NETHER) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        }
    }

    private int getAdditional(Player killer) {
        if (killer.hasPermission("hcteams.killreward.ghoul")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.poltergeist")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.sorcerer")) {
            return 10;
        } else if (killer.hasPermission("hcteams.killreward.suprive")) {
            return 25;
        } else if (killer.hasPermission("hcteams.killreward.juggernaut")) {
            return 50;
        } else if (killer.hasPermission("hcteams.killreward.myth")) {
            return 75;
        } else if (killer.hasPermission("hcteams.killreward.sapphire")) {
            return 100;
        } else if (killer.hasPermission("hcteams.killreward.pearl")) {
            return 125;
        } else if (killer.hasPermission("hcteams.killreward.ruby")) {
            return 150;
        } else if (killer.hasPermission("hcteams.killreward.velt")) {
            return 175;
        } else if (killer.hasPermission("hcteams.killreward.velt-plus")) {
            return 200;
        } else {
            return 0;
        }
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onProjectileHit(ProjectileHitEvent event) {
//        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), event.getEntity()::remove, 1L);
//    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
        if (team != null && event.getEntity() instanceof Arrow && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getItemDrop().getLocation())) return;

        event.getItemDrop().remove();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() != TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() != Environment.NETHER) {
            return;
        }

        event.setTo(event.getTo().getWorld().getSpawnLocation().clone());
    }

}
