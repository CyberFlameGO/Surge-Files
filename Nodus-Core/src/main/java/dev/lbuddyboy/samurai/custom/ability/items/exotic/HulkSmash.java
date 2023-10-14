package dev.lbuddyboy.samurai.custom.ability.items.exotic;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public final class HulkSmash extends AbilityItem implements Listener {

    public HulkSmash() {
        super("HulkSmash");

        this.name = "hulk-smash";
    }

    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private final Cooldown smash = new Cooldown();
    private double multiplier;

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        BukkitRunnable runnable = new BukkitRunnable() {

            private long last;
            private int timesExecuted = 0;

            @Override
            public void run() {
                if (timesExecuted >= 3) {
                    cancel();
                    MessageConfiguration.HULK_SMASH_EXPIRED.sendListMessage(player, "%ability-name%", getName());
                    return;
                }

                if (smash.onCooldown(player.getUniqueId())) {
                    if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                        smash.removeCooldown(player);
                    } else {
                        return;
                    }
                }

                if (last + 5_000L > System.currentTimeMillis()) return;

                last = System.currentTimeMillis();
                smash.applyCooldown(player.getUniqueId(), 5);
                player.setVelocity(new Vector(
                        0,
                        player.getLocation().getDirection().getY() * 2.15,
                        0));
                timesExecuted++;
            }

        };

        tasks.put(player.getUniqueId(), runnable.runTaskTimer(Samurai.getInstance(), 2, 2));

        MessageConfiguration.HULK_SMASH_CLICKER.sendListMessage(player, "%ability-name%", this.getName());
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldown.cleanUp();
        cooldown.removeCooldown(event.getPlayer());
        if (tasks.containsKey(event.getPlayer().getUniqueId())) {
            tasks.get(event.getPlayer().getUniqueId()).cancel();
            tasks.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (tasks.containsKey(event.getEntity().getUniqueId())) {
            tasks.get(event.getEntity().getUniqueId()).cancel();
            tasks.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() != Material.AIR) return;
        if (smash.onCooldown(player.getUniqueId())) {
            event.setDamage(event.getDamage() * this.multiplier);
        }
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        this.multiplier = this.config.getDouble("multiplier");
    }
}
