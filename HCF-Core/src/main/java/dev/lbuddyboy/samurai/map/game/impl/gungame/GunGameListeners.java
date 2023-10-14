package dev.lbuddyboy.samurai.map.game.impl.gungame;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GunGameListeners implements Listener {

    private final GameHandler gameHandler = Samurai.getInstance().getMapHandler().getGameHandler();
    private final Cooldown rifleCooldown, shotgunCooldown, smgCooldown;
    
    public GunGameListeners() {
        this.rifleCooldown = new Cooldown();
        this.shotgunCooldown = new Cooldown();
        this.smgCooldown = new Cooldown();
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof GunGameGame ongoingGame) {
            if (!ongoingGame.isPlaying(player.getUniqueId())) {
                return;
            }

            ongoingGame.eliminatePlayer(player, player.getKiller());

            event.setKeepLevel(true);
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamageShooter(EntityDamageByEntityEvent event) {
        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof GunGameGame ongoingGame)) {
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player damager)) return;

        if (ongoingGame.getBlueTeam().contains(victim.getUniqueId()) && ongoingGame.getBlueTeam().contains(damager.getUniqueId())) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "You may not damage your teammate!");
            return;
        }

        if (ongoingGame.getRedTeam().contains(damager.getUniqueId()) && ongoingGame.getRedTeam().contains(victim.getUniqueId())) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "You may not damage your teammate!");
            return;
        }

        if (snowball.hasMetadata("shotgun")) {
            victim.damage(ThreadLocalRandom.current().nextDouble(5.35, 7.45), damager);
            victim.setVelocity(snowball.getVelocity().add(new Vector(0, 0.5, 0)));
        } else if (snowball.hasMetadata("rifle")) {
            victim.damage(ThreadLocalRandom.current().nextDouble(3.35, 4.85), damager);
            victim.setVelocity(snowball.getVelocity().add(new Vector(0, 0.5, 0)));
        } else if (snowball.hasMetadata("smg")) {
            victim.damage(ThreadLocalRandom.current().nextDouble(2.05, 2.85), damager);
            victim.setVelocity(snowball.getVelocity().add(new Vector(0, 0.5, 0)));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getDamager() instanceof Player damage)) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof GunGameGame ongoingGame)) {
            return;
        }
        
        if (ongoingGame.isPlaying(player.getUniqueId()) || ongoingGame.isPlaying(damage.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;
        
        if (item.isSimilar(GunGameGame.getShotGun())) {
            if (shotgunCooldown.onCooldown(player.getUniqueId())) {
                player.sendMessage(CC.translate("&aReloading..."));
                return;
            }
            
            Location location = player.getLocation();
            List<Location> locations = Arrays.asList(
                    location.clone().add(0.5, 0, 0.5),
                    location.clone().add(0.5, 0, 0),
                    location.clone().add(0, 0, 0.5),
                    location.clone().add(-0.5, 0, 0),
                    location.clone().add(0, 0, -1),
                    location.clone().add(-1, 0, -1),
                    
                    location.clone().add(0, 0.5, 0.5),
                    location.clone().add(0.5, 0.5, 0.5),
                    location.clone().add(0.5, 0.5, 0),
                    location.clone().add(-0.5, 0.5, 0),
                    location.clone().add(-0.5, 0.5, -0.5),
                    location.clone().add(0.5, 0.5, -0.5)
            );

            for (Location shotty : locations) {
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.teleport(shotty.add(0, 1, 0));
                snowball.setMetadata("shotgun", new FixedMetadataValue(Samurai.getInstance(), true));
                snowball.setItem(new ItemBuilder(Material.SNOWBALL).modelData(99).build());
            }
            
            shotgunCooldown.applyCooldown(player.getUniqueId(), 3);
            event.setCancelled(true);
        } else if (item.isSimilar(GunGameGame.getRifle())) {
            if (rifleCooldown.onCooldown(player.getUniqueId())) {
                player.sendMessage(CC.translate("&aReloading..."));
                return;
            }
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setMetadata("rifle", new FixedMetadataValue(Samurai.getInstance(), true));
            snowball.setItem(new ItemBuilder(Material.SNOWBALL).modelData(99).build());

            rifleCooldown.applyCooldownLong(player.getUniqueId(), 250);
            event.setCancelled(true);
        } else if (item.isSimilar(GunGameGame.getSmg())) {
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setMetadata("smg", new FixedMetadataValue(Samurai.getInstance(), true));
            snowball.setItem(new ItemBuilder(Material.SNOWBALL).modelData(99).build());

            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof GunGameGame ongoingGame)) {
            return;
        }

        if (ongoingGame.isPlaying(player.getUniqueId()) && ongoingGame.getState() != GameState.RUNNING) {
            event.setCancelled(true);
        }
    }

}
