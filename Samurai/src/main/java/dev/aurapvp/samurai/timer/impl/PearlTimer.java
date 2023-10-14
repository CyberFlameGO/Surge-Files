package dev.aurapvp.samurai.timer.impl;

import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PearlTimer extends PlayerTimer {

    private final Cooldown cooldown = new Cooldown();

    @Override
    public String getName() {
        return "epearl";
    }

    @Override
    public String getDisplayName() {
        return CC.DARK_PURPLE + "Ender Pearl";
    }

    @Override
    public long getDuration(Player player) {
        return 16_000L;
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
    public void onPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();

        if (this.cooldown.onCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        this.cooldown.applyCooldownLong(player, getDuration(player));
    }

}
