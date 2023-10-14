package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class TimeWarp extends AbilityItem {

    public TimeWarp() {
        super("Timewarp");
    }

    private final Map<UUID, PearlLoc> timewarpLocs = new HashMap<>();
    private final Map<UUID, BukkitTask> runnables = new ConcurrentHashMap<>();

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : TimeUnit.MINUTES.toSeconds(1);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.END_ROD)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click this within 20 seconds of",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fthrowing a pearl to be teleported to the.",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fpearl thrown location.",
                        " "
                ).modelData(7).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lTimeWarp");
    }

    @Override
    public int getAmount() {
        return 3;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!timewarpLocs.containsKey(player.getUniqueId()) || timewarpLocs.get(player.getUniqueId()).getRemaining() <= 0) {
            player.sendMessage(CC.translate("&cYou have not thrown an enderpearl in the past 15 seconds."));
            return false;
        }

        setGlobalCooldown(player);
        setCooldown(player);

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "You will be teleported in " + CC.MAIN + "3 seconds" + CC.WHITE + "."
                }, null, null);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    timewarpLocs.remove(player.getUniqueId());
                    return;
                }

                player.teleport(timewarpLocs.get(player.getUniqueId()).getLocation());
                timewarpLocs.remove(player.getUniqueId());

                sendActivationMessages(player,
                        new String[]{
                                "You have activated " + getName() + CC.WHITE + "!",
                                "You have been teleported."
                        }, null, null);
            }
        }.runTaskLater(Samurai.getInstance(), 70);

        return true;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player) {
                this.timewarpLocs.put(((Player) event.getEntity().getShooter()).getUniqueId(), new PearlLoc(event.getLocation(), System.currentTimeMillis()));
            }
        }
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
