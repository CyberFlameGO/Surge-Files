package dev.lbuddyboy.samurai.map.kits;

import com.google.common.collect.Maps;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KitListener implements Listener {

    private static final Map<UUID, Long> REFILL_PLAYER_MAP = Maps.newHashMap();
    private static final long REFILL_DELAY = TimeUnit.MINUTES.toMillis(10L);

    private static final Map<UUID, Long> LAST_CLICKED = Maps.newHashMap();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        REFILL_PLAYER_MAP.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        REFILL_PLAYER_MAP.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END) return;

        for (Block block : new ArrayList<>(event.blockList())) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
                event.blockList().remove(block);
                continue;
            }
            if (DTRBitmask.ROAD.appliesAt((block.getLocation()))) {
                event.blockList().remove(block);
                continue;
            }
            if (Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().containsKey(block.getLocation())) {
                Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().remove(block.getLocation());
                continue;
            }

            Samurai.getInstance().getMapHandler().getKitManager().getBrokenBlocks().put(block.getLocation(), block.getBlockData());
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        for (Block block : new ArrayList<>(event.blockList())) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
                event.blockList().remove(block);
                continue;
            }
            if (DTRBitmask.ROAD.appliesAt((block.getLocation()))) {
                event.blockList().remove(block);
                continue;
            }
            if (Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().containsKey(block.getLocation())) {
                Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().remove(block.getLocation());
                continue;
            }
            Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().remove(block.getLocation());
            Samurai.getInstance().getMapHandler().getKitManager().getBrokenBlocks().put(block.getLocation(), block.getBlockData());
        }
    }

    @EventHandler
    public void onExplode(BlockIgniteEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler
    public void onExplode(BlockFormEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler
    public void onFromTo(BlockFromToEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().put(event.getToBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) return;

        Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;
        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) return;
        if (Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().containsKey(event.getBlock().getLocation())) {
            Samurai.getInstance().getMapHandler().getKitManager().getPlacedBlocks().remove(event.getBlock().getLocation());
            return;
        }

        Samurai.getInstance().getMapHandler().getKitManager().getBrokenBlocks().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Wolf) {
            ((Wolf) event.getRightClicked()).setSitting(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign sign)) {
            return;
        }

        // Potion refill sign
        if (sign.getLine(0).startsWith("- Refill")) {
            openRefillInventory(event.getClickedBlock().getLocation(), player);
            return;
        }

        if (!sign.getLine(0).startsWith("- Kit")) {
            return;
        }

        DefaultKit originalKit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(sign.getLine(1));
        if (originalKit != null) {
            Kit kit = Samurai.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit);
            if (kit != null) {
                attemptApplyKit(player, kit);
            } else {
                attemptApplyKit(player, originalKit);
            }
        }
    }

    private void openRefillInventory(Location signLocation, Player player) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(signLocation) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation(signLocation))) return; // prevent players from using signs inside spawn outside spawn

        if (!DTRBitmask.SAFE_ZONE.appliesAt(signLocation)) { // put them on a cooldown
            long diff = REFILL_PLAYER_MAP.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis();

            if (diff > 0) {
                player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtils.formatIntoDetailedString((int) (diff / 1000)) + " before using this again.");
                return;
            }

            REFILL_PLAYER_MAP.put(player.getUniqueId(), System.currentTimeMillis() + REFILL_DELAY);
            player.sendMessage(ChatColor.YELLOW + "You have been put on a Refill Sign cooldown for " + ChatColor.RED + TimeUtils.formatIntoDetailedString((int) (REFILL_DELAY / 1000)) + ChatColor.YELLOW + ".");
        }

        Inventory inventory = Bukkit.createInventory(player, 45, "Refill");

        ItemStack healItem = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta healItemItemMeta = (PotionMeta) healItem.getItemMeta();
        healItemItemMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));
        healItem.setItemMeta(healItemItemMeta);

        ItemStack speedPotion = new ItemStack(Material.POTION, 1);
        PotionMeta speedPotionItemMeta = (PotionMeta) speedPotion.getItemMeta();
        speedPotionItemMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
        speedPotion.setItemMeta(speedPotionItemMeta);

        ItemStack enderPeal = new ItemStack(Material.ENDER_PEARL, 16);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
        ItemStack goldSword = new ItemStack(Material.GOLDEN_SWORD, 1);

        inventory.setItem(0, speedPotion);
        inventory.setItem(1, speedPotion);
        inventory.setItem(9, speedPotion);
        inventory.setItem(10, speedPotion);
        inventory.setItem(18, speedPotion);
        inventory.setItem(19, speedPotion);

        inventory.setItem(27, enderPeal);
        inventory.setItem(28, steak);

        inventory.setItem(2, goldSword);
        inventory.setItem(11, goldSword);
        inventory.setItem(20, goldSword);
        inventory.setItem(29, goldSword);

        while (inventory.firstEmpty() != -1) {
            inventory.addItem(healItem);
        }

        player.openInventory(inventory);
    }

    public static void attemptApplyKit(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unknown kit!");
            return;
        }

        if (ModUtils.isModMode(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (LAST_CLICKED.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - LAST_CLICKED.get(player.getUniqueId()) < TimeUnit.SECONDS.toMillis(5))) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again.");
            return;
        }

        DefaultKit originalKit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(kit.getName());
        if (originalKit != null) {
            Kit otherKit = Samurai.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit);
            if (otherKit != null) {
                otherKit.apply(player);
            } else {
                originalKit.apply(player);
            }
        }

        LAST_CLICKED.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
