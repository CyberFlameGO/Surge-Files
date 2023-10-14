package dev.lbuddyboy.pcore.essential.plot.listener;

import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.essential.plot.PrivatePlot;
import dev.lbuddyboy.pcore.pCore;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlotListener implements Listener {

   @EventHandler
   public void onBreak(BlockBreakEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       PrivatePlot mineAt = pCore.getInstance().getPlotHandler().fetchPrivateMineAt(event.getBlock().getLocation());

       if (mineAt == null) {
           if (event.getPlayer().isOp()) return;

           event.setCancelled(true);
           return;
       }
   }

   @EventHandler
   public void onBreak(BlockPlaceEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       PrivatePlot mineAt = pCore.getInstance().getPlotHandler().fetchPrivateMineAt(event.getBlock().getLocation());

       if (mineAt == null) {
           if (event.getPlayer().isOp()) return;

           event.setCancelled(true);
           return;
       }
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
       event.getPlayer().setFoodLevel(20);
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onFood(FoodLevelChangeEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
       event.setFoodLevel(20);
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
       Player player = event.getPlayer();
       if (!player.getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;
       PrivatePlot plot = pCore.getInstance().getPlotHandler().fetchPrivateMineAt(event.getTo());

       if (plot == null) {
           plot = pCore.getInstance().getPlotHandler().fetchCache(player.getUniqueId());
           if (plot == null) {
               if (player.isOp()) return;

               player.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
               return;
           }

           if (plot.getBounds().contains(player.getLocation())) return;
       }

       event.setCancelled(true);
       player.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
   }

   @EventHandler
   public void onDecay(LeavesDecayEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onExplode(BlockExplodeEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onFade(BlockFadeEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onSpawn(EntitySpawnEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;
       if (event.getEntity().getType() == EntityType.ARMOR_STAND) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onForm(BlockFormEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

}
