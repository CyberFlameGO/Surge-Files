package dev.aurapvp.samurai.timer.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CrappleTimer extends PlayerTimer {

    private final Cooldown cooldown = new Cooldown();

    @Override
    public String getName() {
        return "crapple";
    }

    @Override
    public String getDisplayName() {
        return CC.YELLOW + "Crapple";
    }

    @Override
    public long getDuration(Player player) {
        return 15_000L;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public void activate(Player player) {
        long duration = getDuration(player);

        getCooldown().applyCooldownLong(player.getUniqueId(), duration);
    }

    @Override
    public void deactivate(Player player) {
        this.cooldown.removeCooldown(player);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (stack == null) return;
        if (stack.getType() != Material.GOLDEN_APPLE) return;
        if (stack.getDurability() != 0) return;

        if (this.cooldown.onCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        this.cooldown.applyCooldownLong(player, getDuration(player));
    }

}
