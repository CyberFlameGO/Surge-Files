package dev.lbuddyboy.pcore.timer.impl;

import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CombatTagTimer extends PlayerTimer {

    private final Cooldown cooldown = new Cooldown();

    @Override
    public String getName() {
        return "combat_tag";
    }

    @Override
    public String getDisplayName() {
        return CC.RED + "Combat Tag";
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageDamager(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        this.cooldown.applyCooldownLong(attacker, getDuration(attacker));
        this.cooldown.applyCooldownLong(victim, getDuration(victim));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageDamagerProj(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Projectile)) return;
        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

        Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        Player victim = (Player) event.getEntity();
        if (attacker == victim) return;

        this.cooldown.applyCooldownLong(attacker, getDuration(attacker));
        this.cooldown.applyCooldownLong(victim, getDuration(victim));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        this.cooldown.removeCooldown(event.getEntity());
        this.cooldown.cleanUp();
    }

}
