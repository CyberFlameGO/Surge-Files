package dev.lbuddyboy.samurai.server.spectate;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectateListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Samurai.getInstance().getServerHandler().getSpectateManager().removeSpectator(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getDamager().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getItemInHand();

        if (hand == null)
            return;

        if (hand.getItemMeta() == null)
            return;

        if (hand.getItemMeta().getDisplayName() == null)
            return;

        if (!Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(event.getPlayer().getUniqueId())) {
            return;
        }

         if (hand.isSimilar(SpectateManager.TELEPORTER)) {
             new PaginatedMenu() {

                 @Override
                 public String getPrePaginatedTitle(Player player) {
                     return "Player List";
                 }

                 @Override
                 public Map<Integer, Button> getAllPagesButtons(Player player) {
                     Map<Integer, Button> buttons = new HashMap<>();

                     int i = 0;
                     for (Player other : Bukkit.getOnlinePlayers()) {
                         if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(other.getUniqueId())) continue;
                         if (other.hasMetadata("modmode")) continue;

                         buttons.put(i++, new Button() {
                             @Override
                             public String getName(Player player) {
                                 return CC.translate(other.getDisplayName());
                             }

                             @Override
                             public List<String> getDescription(Player player) {
                                 return CC.translate(Collections.singletonList("&7Click to teleport to " + other.getDisplayName() + "&7."));
                             }

                             @Override
                             public Material getMaterial(Player player) {
                                 return Material.PLAYER_HEAD;
                             }

                             @Override
                             public ItemStack getButtonItem(Player player) {
                                 ItemBuilder builder = new ItemBuilder(super.getButtonItem(player));
                                 builder.skullOwner(other.getName());
                                 return builder.build();
                             }

                             @Override
                             public void clicked(Player player, int slot, ClickType clickType) {
                                 player.teleport(other);
                             }
                         });
                     }

                     return buttons;
                 }
             }.openMenu(player);
        }
    }


}
