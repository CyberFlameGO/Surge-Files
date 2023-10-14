package dev.lbuddyboy.samurai.custom.ability.profile;

import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/07/2021 / 2:08 AM
 * HCTeams / rip.orbit.hcteams.profile
 */
public class ProfileListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (AbilityProfile.profileMap.containsKey(event.getPlayer().getUniqueId()))
            return;

        new AbilityProfile(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (EOTWCommand.realFFAStarted()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                AbilityProfile profile = AbilityProfile.byUUID(player.getUniqueId());

                if (profile.getEotwLastHitName() == null) continue;

                if (profile.getEotwLastHitName() == player.getUniqueId()) {
                    profile.setEotwLastHitName(null);
                    profile.setEotwLastDamaged(0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player damaged)) return;

        AbilityProfile attackerProfile = AbilityProfile.byUUID(event.getDamager().getUniqueId());
        AbilityProfile damagedProfile = AbilityProfile.byUUID(event.getEntity().getUniqueId());

        attackerProfile.setLastHitName(event.getEntity().getName());

        damagedProfile.setLastDamagerName(event.getDamager().getName());
        damagedProfile.setLastDamagedMillis(System.currentTimeMillis());

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHitProj(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Player shooter)) return;
        if (!(event.getEntity() instanceof Player damaged)) return;

        AbilityProfile attackerProfile = AbilityProfile.byUUID(shooter.getUniqueId());
        AbilityProfile damagedProfile = AbilityProfile.byUUID(event.getEntity().getUniqueId());

        attackerProfile.setLastHitName(event.getEntity().getName());

        damagedProfile.setLastDamagerName(shooter.getName());
        damagedProfile.setLastDamagedMillis(System.currentTimeMillis());

    }

}
