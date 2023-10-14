package dev.lbuddyboy.pcore.essential.warp;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.timer.impl.CombatTagTimer;
import dev.lbuddyboy.pcore.timer.impl.TeleportTimer;
import dev.lbuddyboy.pcore.util.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Getter
@Setter
public class Warp extends TeleportTimer {

    private Config config;
    private String name, permission;
    private long warmup;
    private Location location;
    private ItemStack displayItem;

    public Warp(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "");
        this.permission = config.getString("permission");
        this.warmup = config.getLong("warmup") * 1000;
        this.location = LocationUtils.deserializeString(config.getString("location"));
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
    }

    public Warp(String name, Location location) {
        this.config = new Config(pCore.getInstance(), name, pCore.getInstance().getWarpHandler().getWarpDirectory());
        this.name = name;
        this.warmup = 10_000L;
        this.permission = "";
        this.location = location;
        this.displayItem = new ItemBuilder(Material.ENDER_PEARL)
                .setName("&6&l" + name + " Warp")
                .setLore(Collections.singletonList(
                        "&7Click to teleport to this warp."
                ))
                .addEnchant(Enchantment.DURABILITY, 10)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS).create();

        pCore.getInstance().getWarpHandler().getWarps().put(name, this);
    }

    public void save() {
        if (!Objects.equals(this.name, config.getFileName().replaceAll(".yml", ""))) {
            pCore.getInstance().getWarpHandler().getWarps().remove(config.getFileName().replaceAll(".yml", ""));

            this.config.getFile().renameTo(new File(this.config.getFile().getParent(), name + ".yml"));
            this.config = new Config(pCore.getInstance(), name + ".yml", pCore.getInstance().getLootBagHandler().getLootBagsDirectory());

            pCore.getInstance().getWarpHandler().getWarps().put(name, this);
        }
        this.config.set("warmup", this.warmup / 1000);
        this.config.set("permission", this.permission);
        this.config.set("location", LocationUtils.serializeString(location));
        ItemUtils.itemStackToConfigSect(this.displayItem, -1, "display-item", config);
        this.config.save();
    }

    public void delete() {
        if (this.config.getFile().exists()) this.config.getFile().delete();
        pCore.getInstance().getWarpHandler().getWarps().remove(this.name);
    }

    public void startWarping(Player player) {
        for (PlayerTimer timer : pCore.getInstance().getTimerHandler().getPlayerTimers(player)) {
            if (timer instanceof CombatTagTimer) {
                if (timer.getCooldown().onCooldown(player.getUniqueId())) {
                    player.sendMessage(CC.translate("&cYou cannot do this whilst combat tagged."));
                    return;
                }
            }
        }
        pCore.getInstance().getWarpHandler().getWarps().values().forEach(w -> {
            if (w.getCooldown().onCooldown(player)) w.deactivate(player);
        });

        activate(player);
        player.sendMessage(CC.translate(pCore.getInstance().getWarpHandler().getConfig().getString("warping", "&aYou will be warped to the %warp% warp in %warmup% seconds!"),
                "%warp%", this.name,
                "%warmup%", (getDuration(player) / 1000)
        ));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return CC.DARK_GREEN + this.name;
    }

    @Override
    public long getDuration(Player player) {
        if (player.hasPermission("warp.teleport.bypass")) return 0;

        return this.warmup;
    }

    @Override
    public void activate(Player player) {
        getCooldown().applyCooldownLong(player.getUniqueId(), getDuration(player));

        tasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;

                getCooldown().removeCooldown(player);
                player.teleport(location);
            }
        }.runTaskLater(pCore.getInstance(), 20 * (getDuration(player) / 1000)));
    }

}
