package dev.lbuddyboy.pcore.timer.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cooldown;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class TeleportTimer extends PlayerTimer {

    public final Cooldown cooldown = new Cooldown();
    public final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDisplayName() {
        return CC.BLUE + "Teleport";
    }

    @Override
    public long getDuration(Player player) {
        return 20_000L;
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
        tasks.get(player.getUniqueId()).cancel();
        tasks.remove(player.getUniqueId());
        this.cooldown.removeCooldown(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;
        if (!cooldown.onCooldown(player.getUniqueId())) return;

        deactivate(player);
        player.sendMessage(CC.translate("&cYou moved, your teleport request has been cancelled!"));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;
        if (!cooldown.onCooldown(player.getUniqueId())) return;

        deactivate(player);
        player.sendMessage(CC.translate("&cYou teleport away, your teleport request has been cancelled!"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!cooldown.onCooldown(player.getUniqueId())) return;

        deactivate(player);
        player.sendMessage(CC.translate("&cYou were damaged, your teleport request has been cancelled!"));
    }

}
