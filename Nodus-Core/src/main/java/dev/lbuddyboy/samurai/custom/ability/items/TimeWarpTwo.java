package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TimeWarpTwo extends AbilityItem implements Listener {

    public TimeWarpTwo() {
        super("TimewarpTwo");
        this.name = "timewarp-two";
    }

    private final Map<UUID, PearlLoc> timewarpLocs = new HashMap<>();

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!timewarpLocs.containsKey(player.getUniqueId()) || timewarpLocs.get(player.getUniqueId()).getRemaining() <= 0) {
            player.sendMessage(CC.translate("&cYou have not thrown an enderpearl in the past 15 seconds."));
            return false;
        }

        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        MessageConfiguration.TIME_WARP_CLICKER.sendListMessage(player
                , "%ability-name%", this.getName()
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!timewarpLocs.containsKey(player.getUniqueId())) return;

                if (!player.isOnline()) {
                    cancel();
                    timewarpLocs.remove(player.getUniqueId());
                    return;
                }

                player.teleport(timewarpLocs.get(player.getUniqueId()).getLocation());
                timewarpLocs.remove(player.getUniqueId());

                MessageConfiguration.TIME_WARP_CLICKER_TPED.sendListMessage(player
                        , "%ability-name%", getName()
                );
            }
        }.runTaskLater(Samurai.getInstance(), 70);

        return true;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player shooter) {
                this.timewarpLocs.put(shooter.getUniqueId(), new PearlLoc(shooter.getLocation(), System.currentTimeMillis()));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.timewarpLocs.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        this.timewarpLocs.remove(event.getEntity().getUniqueId());
    }

    @AllArgsConstructor
    @Data
    public static class PearlLoc {

        private Location location;
        private long usedAt;

        public long getRemaining() {
            return (this.usedAt + 16_000) - System.currentTimeMillis();
        }

    }

}
