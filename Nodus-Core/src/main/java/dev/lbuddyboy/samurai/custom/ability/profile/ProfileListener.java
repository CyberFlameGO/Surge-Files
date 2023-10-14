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

        // EOTW Anti-Clean/Ally
/*        if (EOTWCommand.realFFAStarted()) {
            UUID lastHit = attackerProfile.getEotwLastHitName();
            long lastDamaged = attackerProfile.getEotwLastDamaged() + 60_000L;
            long difference = lastDamaged - System.currentTimeMillis();
            if (lastHit != null && difference > 0 && lastHit == damaged.getUniqueId()) {
                Player player = Bukkit.getPlayer(lastHit);
                if (player == null || !player.isOnline()) {
                    attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                    attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                    damagedProfile.setEotwLastHitName(event.getDamager().getUniqueId());
                    damagedProfile.setEotwLastDamaged(System.currentTimeMillis());

                    damager.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));
                    damaged.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasMetadata("modmode") || p.hasMetadata("invisible")) continue;
                        if (Foxtrot.getInstance().getServerHandler().getSpectateManager().isSpectator(p.getUniqueId())) continue;
                        if (p == damager) continue;
                        if (p == damaged) continue;

                        p.hidePlayer(Foxtrot.getInstance(), damaged);
                        p.hidePlayer(Foxtrot.getInstance(), damager);
                    }

                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showPlayer(Foxtrot.getInstance(), damaged);
                                p.showPlayer(Foxtrot.getInstance(), damager);
                            }
                        }
                    }.runTaskLater(Foxtrot.getInstance(), 20 * 60);

                    if (attackerProfile.getEotwTagTask() != null) {
                        attackerProfile.getEotwTagTask().cancel();
                    }
                    if (damagedProfile.getEotwTagTask() != null) {
                        damagedProfile.getEotwTagTask().cancel();
                    }

                    attackerProfile.setEotwTagTask(task);
                    damagedProfile.setEotwTagTask(task);
                    return;
                }
                attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                damagedProfile.setEotwLastHitName(event.getDamager().getUniqueId());
                damagedProfile.setEotwLastDamaged(System.currentTimeMillis());
                return;
            }
            if (lastHit == null || difference < 0) {
                attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                damagedProfile.setEotwLastHitName(event.getDamager().getUniqueId());
                damagedProfile.setEotwLastDamaged(System.currentTimeMillis());

                damager.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));
                damaged.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasMetadata("modmode") || p.hasMetadata("invisible")) continue;
                    if (Foxtrot.getInstance().getServerHandler().getSpectateManager().isSpectator(p.getUniqueId())) continue;
                    if (p == damager) continue;
                    if (p == damaged) continue;

                    p.hidePlayer(Foxtrot.getInstance(), damaged);
                    p.hidePlayer(Foxtrot.getInstance(), damager);
                }

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.showPlayer(Foxtrot.getInstance(), damaged);
                            p.showPlayer(Foxtrot.getInstance(), damager);
                        }
                    }
                }.runTaskLater(Foxtrot.getInstance(), 20 * 60);

                if (attackerProfile.getEotwTagTask() != null) {
                    attackerProfile.getEotwTagTask().cancel();
                }
                if (damagedProfile.getEotwTagTask() != null) {
                    damagedProfile.getEotwTagTask().cancel();
                }

                attackerProfile.setEotwTagTask(task);
                damagedProfile.setEotwTagTask(task);
                return;
            }
            event.setCancelled(true);
            damager.sendMessage(CC.translate("&aYou are bound to the player that is glowing"));
            return;
        }*/

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

        if (EOTWCommand.realFFAStarted()) {
            UUID lastHit = attackerProfile.getEotwLastHitName();
            long lastDamaged = attackerProfile.getEotwLastDamaged() + 60_000L;
            long difference = lastDamaged - System.currentTimeMillis();
            if (lastHit != null && difference > 0 && lastHit == damaged.getUniqueId()) {
                Player player = Bukkit.getPlayer(lastHit);
                if (player == null || !player.isOnline()) {
                    attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                    attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                    damagedProfile.setEotwLastHitName(shooter.getUniqueId());
                    damagedProfile.setEotwLastDamaged(System.currentTimeMillis());

                    shooter.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));
                    damaged.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasMetadata("modmode") || p.hasMetadata("invisible")) continue;
                        if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(p.getUniqueId())) continue;
                        if (p == shooter) continue;
                        if (p == damaged) continue;

                        p.hidePlayer(Samurai.getInstance(), damaged);
                        p.hidePlayer(Samurai.getInstance(), shooter);
                    }

                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showPlayer(Samurai.getInstance(), damaged);
                                p.showPlayer(Samurai.getInstance(), shooter);
                            }
                        }
                    }.runTaskLater(Samurai.getInstance(), 20 * 60);


                    if (attackerProfile.getEotwTagTask() != null) {
                        attackerProfile.getEotwTagTask().cancel();
                    }
                    if (damagedProfile.getEotwTagTask() != null) {
                        damagedProfile.getEotwTagTask().cancel();
                    }

                    attackerProfile.setEotwTagTask(task);
                    damagedProfile.setEotwTagTask(task);
                    return;
                }
                attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                damagedProfile.setEotwLastHitName(shooter.getUniqueId());
                damagedProfile.setEotwLastDamaged(System.currentTimeMillis());
                return;
            }

            if (lastHit == null || difference < 0) {
                attackerProfile.setEotwLastHitName(event.getEntity().getUniqueId());
                attackerProfile.setEotwLastDamaged(System.currentTimeMillis());

                damagedProfile.setEotwLastHitName(shooter.getUniqueId());
                damagedProfile.setEotwLastDamaged(System.currentTimeMillis());

                shooter.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));
                damaged.sendMessage(CC.translate("&aYou are now bound to the only player you can see."));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasMetadata("modmode") || p.hasMetadata("invisible")) continue;
                    if (Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(p.getUniqueId())) continue;
                    if (p == shooter) continue;
                    if (p == damaged) continue;

                    p.hidePlayer(Samurai.getInstance(), damaged);
                    p.hidePlayer(Samurai.getInstance(), shooter);
                }

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.showPlayer(Samurai.getInstance(), damaged);
                            p.showPlayer(Samurai.getInstance(), shooter);
                        }
                    }
                }.runTaskLater(Samurai.getInstance(), 20 * 60);

                if (attackerProfile.getEotwTagTask() != null) {
                    attackerProfile.getEotwTagTask().cancel();
                }
                if (damagedProfile.getEotwTagTask() != null) {
                    damagedProfile.getEotwTagTask().cancel();
                }

                attackerProfile.setEotwTagTask(task);
                damagedProfile.setEotwTagTask(task);
                return;
            }
            event.setCancelled(true);
            shooter.sendMessage(CC.translate("&aYou are bound to the player that is glowing"));
            return;
        }

        attackerProfile.setLastHitName(event.getEntity().getName());

        damagedProfile.setLastDamagerName(shooter.getName());
        damagedProfile.setLastDamagedMillis(System.currentTimeMillis());

    }

}
