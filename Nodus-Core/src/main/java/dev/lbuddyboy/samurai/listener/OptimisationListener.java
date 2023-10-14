package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OptimisationListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (SOTWCommand.isSOTWTimer()) {
            Team team = LandBoard.getInstance().getTeam(player.getLocation());
            if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }.runTaskLater(Samurai.getInstance(), 20 * 60);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Warden) return;

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

}
