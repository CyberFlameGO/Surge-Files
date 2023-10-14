package dev.lbuddyboy.samurai.pvpclasses;

import dev.lbuddyboy.samurai.pvpclasses.event.BardRestoreEvent;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.*;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;

import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PvPClassHandler extends BukkitRunnable implements Listener {

    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<>();
    @Getter private static Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
    @Getter List<PvPClass> pvpClasses = new ArrayList<>();

    public PvPClassHandler() {
        pvpClasses.add(new MinerClass());
//        pvpClasses.add(new HunterClass());
        pvpClasses.add(new HunterClass());

        if (Samurai.getInstance().getConfig().getBoolean("pvpClasses.archer")) {
            pvpClasses.add(new ArcherClass());
        }

        if (Samurai.getInstance().getConfig().getBoolean("pvpClasses.bard")) {
            pvpClasses.add(new BardClass());
        }

        if (Samurai.getInstance().getConfig().getBoolean("pvpClasses.rogue")) {
            pvpClasses.add(new RogueClass());
        }

        if (Samurai.getInstance().getConfig().getBoolean("pvpClasses.waverider", false)) {
            pvpClasses.add(new WaveRiderClass());
        }

        if (Samurai.getInstance().getConfig().getBoolean("pvpClasses.ranger", false)) {
            pvpClasses.add(new RangerClass());
        }

        for (PvPClass pvpClass : pvpClasses) {
            Samurai.getInstance().getServer().getPluginManager().registerEvents(pvpClass, Samurai.getInstance());
        }

        runTaskTimer(Samurai.getInstance(), 10, 10);
        Samurai.getInstance().getServer().getPluginManager().registerEvents(this, Samurai.getInstance());
    }

    @Override
    public void run() {
        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            // Remove kit if player took off armor, otherwise .tick();
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());

                if (!equippedPvPClass.qualifies(player.getInventory()) || !Samurai.getInstance().getCitadelHandler().canUsePvPClass(player)) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + equippedPvPClass.getName() + ChatColor.RED + " (Disabled)");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.removePotionEffect(PotionEffectType.JUMP);
                } else if (!player.hasMetadata("frozen")) {
                    equippedPvPClass.tick(player);
                }
            } else {
                // Start kit warmup
                for (PvPClass pvpClass : pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata("frozen") && Samurai.getInstance().getCitadelHandler().canUsePvPClass(player)) {
                        pvpClass.apply(player);
                        PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);

                        player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY + ChatColor.GREEN + " (Enabled)");
                        break;
                    }
                }
            }
        }
        checkSavedPotions();
    }

    public void checkSavedPotions() {
        Iterator<Map.Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();
        while (idIterator.hasNext()) {
            Map.Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
            Player player = Bukkit.getPlayer(id.getKey());
            if (player != null && player.isOnline()) {
                Bukkit.getPluginManager().callEvent(new BardRestoreEvent(player, id.getValue()));
                if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
                    if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
                        player.getActivePotionEffects().forEach(potion -> {
                            PotionEffect restore = id.getValue().getPotionEffect();
                            if (potion.getType() == restore.getType() && potion.getDuration() < restore.getDuration() && potion.getAmplifier() <= restore.getAmplifier()) {
                                player.removePotionEffect(restore.getType());
                            }
                        });
                    }
                    
                    if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
                        Bukkit.getLogger().info(id.getValue().getPotionEffect().getType() + ", " + id.getValue().getPotionEffect().getDuration() + ", " + id.getValue().getPotionEffect().getAmplifier());
                        idIterator.remove();
                    }
                }
            } else {
                idIterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        for (PvPClass pvPClass : pvpClasses) {
            if (hasKitOn(event.getPlayer(), pvPClass) && pvPClass.getConsumables() != null && pvPClass.getConsumables().contains(event.getItem().getType())) {
                if (event.getItem() == null) return;
                if (pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
                    InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), event.getItem(), 1);
                }
            }
        }
    }

    public static PvPClass getPvPClass(Player player) {
        return (equippedKits.getOrDefault(player.getName(), null));
    }

    public static boolean hasKitOn(Player player, PvPClass pvpClass) {
        return (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }

        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getDuration() == PotionEffect.INFINITE_DURATION) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }
        }
    }

}