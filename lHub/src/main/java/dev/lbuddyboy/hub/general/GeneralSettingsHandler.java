package dev.lbuddyboy.hub.general;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.queue.QueueImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 1:37 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.settings
 */

@Getter
public class GeneralSettingsHandler implements lModule {

    private final List<Player> buildModes;
    private final List<Placeholder> placeholders;
    private Location spawnLocation;
    private boolean placeholderAPI;
    private boolean antiVoid;
    private int antiVoidHeight;
    private boolean doubleJump;
    private double doubleJumpMultiplier;
    private Sound doubleJumpSound;
    private boolean enderButtRide;
    private double enderButtMultiplier;
    private Sound enderButtSound;
    private String online, offline, whitelisted;

    public GeneralSettingsHandler() {
        this.buildModes = new ArrayList<>();
        this.placeholders = new ArrayList<>();
    }

    @Override
    public void load(lHub plugin) {
        reload();

        Bukkit.getScheduler().runTaskTimerAsynchronously(lHub.getInstance(), () -> {
            QueueImpl queueImpl = lHub.getInstance().getQueueHandler().getQueueImpl();

            for (Placeholder placeholder : this.placeholders) {
                queueImpl.update(placeholder);

            }
        }, 10, 20 * 5);
    }

    @Override
    public void unload(lHub plugin) {

    }

    @Override
    public void reload() {
        this.buildModes.clear();
        this.placeholders.clear();
        this.placeholderAPI = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getBoolean("papi-integration");
        this.loadSpawn();
        this.loadAntiVoid();
        this.loadDoubleJump();
        this.loadEnderButt();
        this.loadServer();
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.world", this.spawnLocation.getWorld().getName());
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.x", this.spawnLocation.getX());
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.y", this.spawnLocation.getY());
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.z", this.spawnLocation.getZ());
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.yaw", this.spawnLocation.getYaw());
        lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().set("spawn.pitch", this.spawnLocation.getPitch());
        try {
            lHub.getInstance().getDocHandler().getSettingsDoc().getDoc().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSpawn() {
        this.spawnLocation = new Location(
                Bukkit.getWorld(lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("spawn.world")),
                lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("spawn.x"),
                lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("spawn.y"),
                lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("spawn.z"),
                (float) lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("spawn.yaw"),
                (float) lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("spawn.pitch")
        );
    }

    private void loadAntiVoid() {
        this.antiVoid = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getBoolean("anti-void.enabled");
        this.antiVoidHeight = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getInt("anti-void.level");
    }

    private void loadDoubleJump() {
        this.doubleJump = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getBoolean("double-jump.enabled");
        this.doubleJumpMultiplier = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("double-jump.multiplier");
        try {
            this.doubleJumpSound = Sound.valueOf(lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("double-jump.sound"));
        } catch (Exception ignored) {
        }
    }

    private void loadEnderButt() {
        this.enderButtRide = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getBoolean("ender-butt.ride");
        this.enderButtMultiplier = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getDouble("ender-butt.multiplier");
        try {
            this.enderButtSound = Sound.valueOf(lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("ender-butt.sound"));
        } catch (Exception ignored) {
        }
    }

    private void loadServer() {
        this.online = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("server-status.online");
        this.offline = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("server-status.offline");
        this.whitelisted = lHub.getInstance().getDocHandler().getSettingsDoc().getConfig().getString("server-status.whitelisted");
    }

}
