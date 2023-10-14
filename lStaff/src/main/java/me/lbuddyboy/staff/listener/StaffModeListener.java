package me.lbuddyboy.staff.listener;


import me.lbuddyboy.staff.editor.EditItem;
import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class StaffModeListener implements Listener {

    private final lStaff pl;

    public StaffModeListener(lStaff plugin) {
        this.pl = plugin;
    }

    @EventHandler
    public void onInteractVanishTool(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (pl.getStaffModeHandler().inStaffMode(p)) {
            if (item == null)
                return;
            if (item == null)
                return;
            if (item.getItemMeta() == null) return;
            if (item.getItemMeta().getDisplayName() == null) return;

            if (pl.getStaffModeHandler().getBetterView().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendTitle(CC.chat("&g&lSpectator Mode"), CC.chat("&fHit a player to view their POV. Run the staff mode command to leave this."));
            } else if (pl.getStaffModeHandler().getVanishon().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                p.setItemInHand(pl.getStaffModeHandler().getVanishoff());
                pl.getStaffModeHandler().unloadVanish(p);
            } else if (pl.getStaffModeHandler().getVanishoff().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                p.setItemInHand(pl.getStaffModeHandler().getVanishon());
                pl.getStaffModeHandler().loadVanish(p);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (pl.getStaffModeHandler().inStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractRTPTool(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (pl.getStaffModeHandler().inStaffMode(p)) {
            if (item == null)
                return;
            if (item.getItemMeta() == null) return;
            if (item.getItemMeta().getDisplayName() == null) return;
            if (pl.getStaffModeHandler().getRandomTP().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                ArrayList<Player> pList = new ArrayList<>(Bukkit.getOnlinePlayers());

                int random = new Random().nextInt(pList.size());
                Player randomPlayer = pList.get(random);

                if (randomPlayer == p)
                    return;
                if (pl.getStaffModeHandler().inStaffMode(randomPlayer))
                    return;

                if (randomPlayer.getWorld() != p.getWorld()) {
                    p.teleport(randomPlayer.getWorld().getSpawnLocation());
                    Bukkit.getScheduler().runTask(lStaff.getInstance(), () -> p.teleport(randomPlayer));
                } else {
                    p.teleport(randomPlayer);
                }
            }
        }
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        if (pl.getStaffModeHandler().inStaffMode(event.getPlayer())) {
            Bukkit.getScheduler().runTask(lStaff.getInstance(), () -> pl.getStaffModeHandler().loadStaffMode(event.getPlayer()));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (pl.getStaffModeHandler().inStaffMode(event.getPlayer())) {
            if (event.getPlayer().hasPermission("lstaff.admin"))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (pl.getStaffModeHandler().inStaffMode(event.getPlayer())) {
            if (event.getPlayer().hasPermission("lstaff.admin"))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractChest(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();
        if (pl.getStaffModeHandler().inStaffMode(player)) {
            if (event.getAction().name().equalsIgnoreCase("RIGHT_CLICK_BLOCK")) {
                if (clicked == null)
                    return;
                if (clicked.getState() instanceof Container) {
                    event.setCancelled(true);
                    player.closeInventory();
                    Inventory inventory = ((Container) clicked.getState()).getInventory();

                    if (clicked.getState() instanceof Chest) {
                        ((Chest) clicked.getState()).close();
                    }

                    player.openInventory(inventory);
                    player.sendMessage(CC.chat("&7&oOpening chest clicked silently"));
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("modmode")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoss(FoodLevelChangeEvent event) {
        if (event.getEntity().hasMetadata("modmode")) {
            event.setCancelled(true);
            event.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!event.getPlayer().hasPermission("lstaff.staff")) {
                if (lStaff.getInstance().getStaffModeHandler().isVanished(online)) {
                    lStaff.getInstance().getStaffModeHandler().loadVanish(online);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(lStaff.getInstance(), () -> {
            if (player.hasPermission("lstaff.staff")) {
                EditItem editItem = EditItem.byUUID(player.getUniqueId());
                if (editItem.isStaffModeOnJoin()) {
                    pl.getStaffModeHandler().loadStaffMode(player);
                }
            }
        }, 10);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ostaff.staff")) {
            if (pl.getStaffModeHandler().inStaffMode(player)) {
                pl.getStaffModeHandler().unloadStaffMode(player);
            }
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (pl.getStaffModeHandler().inStaffMode(player)) {
                pl.getStaffModeHandler().unloadStaffMode(player);
            }
        }
    }

    public Map<Player, Long> lastCLicked = new ConcurrentHashMap<>();

    @EventHandler
    public void onInteractEntityFreeze(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (pl.getStaffModeHandler().inStaffMode(player)) {
            Entity e = event.getRightClicked();
            if (e instanceof Player) {
                Player rightClicked = (Player) e;
                ItemStack item = player.getItemInHand();
                if (item == null) return;
                if (item.getItemMeta() == null) return;
                if (item.getItemMeta().getDisplayName() == null) return;
                if (pl.getStaffModeHandler().getFreezer().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                    if (pl.getStaffModeHandler().inStaffMode(rightClicked))
                        return;

                    if (lastCLicked.containsKey(player) && lastCLicked.get(player) > System.currentTimeMillis()) return;

                    player.chat("/freeze " + rightClicked.getName());
                    lastCLicked.put(player, System.currentTimeMillis() + 50);

                } else if (pl.getStaffModeHandler().getInspector().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                    player.closeInventory();
                    player.chat("/invsee " + rightClicked.getName());
                }
            }
        }
    }

    @EventHandler
    public void onDamageInStaff(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            if (pl.getStaffModeHandler().inStaffMode(damager)) {
                if (damager.isOp()) return;

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropInStaff(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (pl.getStaffModeHandler().inStaffMode(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!pl.getStaffModeHandler().inStaffMode(player)) return;

        event.setCancelled(!player.hasPermission("lstaff.admin"));
    }

}
