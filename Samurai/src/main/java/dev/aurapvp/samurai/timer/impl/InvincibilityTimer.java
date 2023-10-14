package dev.aurapvp.samurai.timer.impl;

import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class InvincibilityTimer extends PlayerTimer {

    private final Cooldown cooldown;

    public InvincibilityTimer() {
        super();
        this.cooldown = new Cooldown();
    }

    @Override
    public String getName() {
        return "invincibility";
    }

    @Override
    public String getDisplayName() {
        return CC.GREEN + "Invincibility";
    }

    @Override
    public long getDuration(Player player) {
        return 60_000L * 30;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public void activate(Player player) {
        getCooldown().applyCooldown(player.getUniqueId(), getDuration(player) / 1000);
    }

    @Override
    public void deactivate(Player player) {

    }

    @EventHandler
    public void onDamageDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!this.cooldown.onCooldown(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageDamager(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (!this.cooldown.onCooldown(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageDamagerProj(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile)) return;
        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player player)) return;

        if (!this.cooldown.onCooldown(player.getUniqueId())) return;

        event.setCancelled(true);
    }

}
