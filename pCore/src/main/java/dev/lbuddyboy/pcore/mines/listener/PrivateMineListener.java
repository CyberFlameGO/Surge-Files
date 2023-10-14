package dev.lbuddyboy.pcore.mines.listener;

import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.pCore;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PrivateMineListener implements Listener {

/*    @EventHandler
    public void onBlockEvent(ExplosionTriggerEvent event) {
        PrivateMineCache cache = pCore.getInstance().getPrivateMineHandler().fetchCache(event.getPlayer().getUniqueId());
        PrivateMine mine = cache.getMine();

        if (mine == null) return;

        for (Block block : event.getBlocksAffected()) {
            mine.increaseProgress(-1);
        }
    }

    @EventHandler
    public void onNuke(NukeTriggerEvent event) {
        PrivateMineCache cache = pCore.getInstance().getPrivateMineHandler().fetchCache(event.getPlayer().getUniqueId());
        PrivateMine mine = cache.getMine();

        if (mine == null) return;

        for (Block block : event.getBlocksAffected()) {
            mine.increaseProgress(-1);
        }
    }

    @EventHandler
    public void onLayer(LayerTriggerEvent event) {
        PrivateMineCache cache = pCore.getInstance().getPrivateMineHandler().fetchCache(event.getPlayer().getUniqueId());
        PrivateMine mine = cache.getMine();

        if (mine == null) return;

        for (Block block : event.getBlocksAffected()) {
            mine.increaseProgress(-1);
        }
    }*/

   @EventHandler
   public void onBreak(BlockBreakEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       PrivateMine mineAt = pCore.getInstance().getPrivateMineHandler().fetchPrivateMineAt(event.getBlock().getLocation());

       if (mineAt == null) {
           System.out.println("Could not locate a mine");
           return;
       }

       if (mineAt.getMinePit().contains(event.getBlock())) {
           mineAt.setBlocksLeft(mineAt.getBlocksLeft() - 1);
           mineAt.increaseProgress(event.getPlayer().getUniqueId(), -1);
           if ((mineAt.isResettable())) mineAt.reset();
           return;
       }

       event.setCancelled(true);
   }

   @EventHandler
   public void onBreak(BlockPlaceEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent event) {
       if (!event.getPlayer().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
       event.getPlayer().setFoodLevel(20);
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }


   @EventHandler
   public void onFood(FoodLevelChangeEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
       event.setFoodLevel(20);
   }

   @EventHandler
   public void onDecay(LeavesDecayEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onExplode(BlockExplodeEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onFade(BlockFadeEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onSpawn(EntitySpawnEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;
       if (event.getEntity() instanceof ArmorStand) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
       if (!event.getEntity().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;
       if (event.getEntity().getItemStack().getType().name().endsWith("_PICKAXE")) return;

       event.setCancelled(true);
   }

   @EventHandler
   public void onForm(BlockFormEvent event) {
       if (!event.getBlock().getWorld().equals(pCore.getInstance().getPrivateMineHandler().getGrid().getWorld())) return;

       event.setCancelled(true);
   }

}
