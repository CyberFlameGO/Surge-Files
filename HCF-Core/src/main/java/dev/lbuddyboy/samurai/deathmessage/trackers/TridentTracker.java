package dev.lbuddyboy.samurai.deathmessage.trackers;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.deathmessage.event.CustomPlayerDamageEvent;
import dev.lbuddyboy.samurai.deathmessage.objects.Damage;
import dev.lbuddyboy.samurai.deathmessage.objects.MobDamage;
import dev.lbuddyboy.samurai.deathmessage.objects.PlayerDamage;
import dev.lbuddyboy.samurai.util.EntityUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class TridentTracker implements Listener {

    @EventHandler
    public void onEntityShootBow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident trident) {
            trident.setMetadata("ShotFromDistance", new FixedMetadataValue(Samurai.getInstance(), trident.getLocation()));
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();

            if (entityDamageByEntityEvent.getDamager() instanceof Trident trident) {

                if (trident.getShooter() instanceof Player shooter) {
                    /*LOGIC TEST
                    Location l = arrow.getLocation();
                    Location l2 = event.getPlayer().getLocation();
                    if( l.getBlockX() != l2.getBlockX() || l.getBlockZ() != l2.getBlockZ() ) {
                        entityDamageByEntityEvent.setCancelled(true);
                        System.out.println("Cancelled arrow hit! Hit through a wall!");
                        return;
                    }
                    LOGIC TEST - FAILED WILL TODO: INVESTIGATE FURTHER */

                    for (MetadataValue value : trident.getMetadata("ShotFromDistance")) {
                        Location shotFrom = (Location) value.value();
                        double distance = shotFrom.distance(event.getPlayer().getLocation());
                        event.setTrackerDamage(new TridentDamageByPlayer(event.getPlayer().getName(), event.getDamage(), shooter.getName(), shotFrom, distance));
                    }
                } else if (trident.getShooter() instanceof Entity) {
                    event.setTrackerDamage(new TridentDamageByMob(event.getPlayer().getName(), event.getDamage(), (Entity) trident.getShooter()));
                } else {
                    event.setTrackerDamage(new TridentDamage(event.getPlayer().getName(), event.getDamage()));
                }
            }
        }
    }

    public static class TridentDamage extends Damage {

        public TridentDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was pummeled by a trident.");
        }

    }

    public static class TridentDamageByPlayer extends PlayerDamage {

        @Getter private Location shotFrom;
        @Getter private double distance;

        public TridentDamageByPlayer(String damaged, double damage, String damager, Location shotFrom, double distance) {
            super(damaged, damage, damager);
            this.shotFrom = shotFrom;
            this.distance = distance;
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was shot by " + wrapName(getDamager()) + " with a trident from " + ChatColor.BLUE + (int) distance + " blocks" + ChatColor.YELLOW + ".");
        }

    }

    public static class TridentDamageByMob extends MobDamage {

        public TridentDamageByMob(String damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was shot by a " + ChatColor.RED + EntityUtils.getName(getMobType()) + ChatColor.YELLOW + " with a trident.");
        }

    }

}