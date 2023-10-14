package dev.aurapvp.samurai.timer.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.ActionBarAPI;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    @EventHandler
    public void onDamageDamager(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        this.cooldown.applyCooldownLong(attacker, getDuration(attacker));
        this.cooldown.applyCooldownLong(victim, getDuration(victim));
    }

    @EventHandler
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            Faction toClan = Samurai.getInstance().getFactionHandler().getFactionByLocation(to);

            if (toClan != null && toClan.isSystemFaction() && toClan.getType() == FactionType.SPAWN && cooldown.onCooldown(player.getUniqueId())) {
                event.setTo(event.getFrom());
                player.sendMessage(CC.translate("&cYou are currently spawn tagged and cannot enter safe zones!"));
            }
        }
    }

}
