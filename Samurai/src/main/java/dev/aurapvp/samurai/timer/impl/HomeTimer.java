package dev.aurapvp.samurai.timer.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeTimer extends PlayerTimer {

    private final Cooldown cooldown = new Cooldown();
    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getDisplayName() {
        return CC.BLUE + "Home";
    }

    @Override
    public long getDuration(Player player) {
        Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(player.getLocation());
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (factionAt == null) return 10_000L;
        if (factionAt.getLeader() == null && factionAt.getType() == FactionType.SPAWN) return 1L;
        if (faction != null && factionAt == faction) return 10_000L;

        return 20_000L;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public void activate(Player player) {
        long duration = getDuration(player);

        getCooldown().applyCooldownLong(player.getUniqueId(), duration);

        tasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;

                Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

                if (faction == null) {
                    return;
                }

                player.teleport(faction.getHome());
            }
        }.runTaskLater(Samurai.getInstance(), 20 * (duration / 1000)));
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
        player.sendMessage(CC.translate("&cYou moved, your home warp request has been cancelled!"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!cooldown.onCooldown(player.getUniqueId())) return;

        deactivate(player);
        player.sendMessage(CC.translate("&cYou were damaged, your home warp request has been cancelled!"));
    }

}
