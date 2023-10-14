package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.util.modsuite.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpawnTagListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        /* Only tag player damagers, and deny tagging self */
        if (damager != null && damager != event.getEntity()) {
            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
            SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onQuit(PlayerQuitEvent event) {
        List<String> names = SpawnTagHandler.getSpawnTags().keySet().stream().filter(s -> !SpawnTagHandler.isTagged(s)).toList();

        for (String name : names) {
            SpawnTagHandler.removeTag(name);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !SpawnTagHandler.isTagged(player) || Samurai.getInstance().getServerHandler().isPlaceBlocksInCombat()) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't place blocks whilst in combat.");
        event.setCancelled(true);
    }

}