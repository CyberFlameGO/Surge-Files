package dev.aurapvp.samurai.events.koth;

import dev.aurapvp.samurai.events.Event;
import dev.aurapvp.samurai.events.EventType;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.util.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@Getter
public class KoTH extends BukkitRunnable implements Event {

    private Config config;
    private String name, displayName;
    private EventType type;
    private boolean active = false;
    @Setter private Location center = null;
    @Setter private Cuboid capZone = null;
    @Setter private int defaultCapSeconds = 300;

    private transient long startedAt;
    private transient long startedCappingAt;
    private transient Player capper;

    public KoTH(String name) {
        this.name = name;
        this.displayName = name;
        this.config = new Config(Samurai.getInstance(), this.name, Samurai.getInstance().getEventHandler().getKothsFolder());
        this.type = EventType.KOTH;
        this.config.set("name", this.name);
    }

    public KoTH(Config config) {
        this.config = config;
        this.name = config.getString("name");
        this.displayName = config.getString("displayName");
        this.type = EventType.valueOf(config.getString("type"));
        this.active = config.getBoolean("active");
        if (config.contains("center")) this.center = LocationUtils.deserializeString(config.getString("center"));
        if (config.contains("capZone")) this.capZone = LocationUtils.deserializeStringCuboid(config.getString("capZone"));
        if (config.contains("defaultCapSeconds")) this.defaultCapSeconds = config.getInt("defaultCapSeconds");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public EventType getEventType() {
        return this.type;
    }

    @Override
    public FactionType getFactionType() {
        return FactionType.KOTH;
    }

    @Override
    public Material getEditorMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void end() {
        setActive(false);

        Bukkit.broadcastMessage(CC.translate(Samurai.getInstance().getEventHandler().getEventsConfig().getString("koth.ended"),
                "%koth%", this.name
        ));
    }

    @Override
    public void start() {
        if (capZone == null) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&9[Event Handler] &fThe " + this.name + " KoTH cannot start due to the capzone not being setup."));
            return;
        }
        if (center == null) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&9[Event Handler] &fThe " + this.name + " KoTH cannot start due to the center point not being setup."));
            return;
        }

        setActive(true);
        this.startedAt = System.currentTimeMillis();

        Bukkit.broadcastMessage(CC.translate(Samurai.getInstance().getEventHandler().getEventsConfig().getString("koth.started"),
                "%koth%", this.name
        ));
        runTaskTimerAsynchronously(Samurai.getInstance(), 10, 10);
    }

    @Override
    public void win() {
        setActive(false);
        Bukkit.broadcastMessage(CC.translate(Samurai.getInstance().getEventHandler().getEventsConfig().getString("koth.captured"),
                "%koth%", this.name,
                "%player%", this.capper.getName()
        ));
    }

    @Override
    public void run() {
        if (capper != null) {
            if (capper.getPlayer() != null && capZone.contains(capper.getPlayer().getLocation())) {
                if (getTimeLeft() <= 0) {
                    // finish cap here
                    return;
                }
                return;
            }

            startedCappingAt = -1;
            capper = null;
        }

        for (Player player : capZone.getWorld().getPlayers()) {
            if (!capZone.contains(player.getLocation())) continue;

            capper = player;
            startedCappingAt = System.currentTimeMillis() + (this.defaultCapSeconds * 1000L);
            break;
        }

    }

    public void won() {
        setActive(false);
    }

    public String getTimeLeftString() {
        return TimeUtils.formatIntoHHMMSS(getTimeLeft());
    }

    public int getTimeLeft() {
        if (this.startedCappingAt == -1) return this.defaultCapSeconds;

        return (int) (((this.startedCappingAt + this.defaultCapSeconds) - System.currentTimeMillis()) / 1000);
    }

    public void reload() {
        this.config = new Config(Samurai.getInstance(), this.name, Samurai.getInstance().getEventHandler().getKothsFolder());
        this.name = config.getString("name");
        this.displayName = config.getString("displayName");
        this.type = EventType.valueOf(config.getString("type"));
        this.active = config.getBoolean("active");
        this.capZone = LocationUtils.deserializeStringCuboid(config.getString("capZone"));
        if (config.contains("defaultCapSeconds")) this.defaultCapSeconds = config.getInt("defaultCapSeconds");
    }

    public void save() {
        if (this.config.contains("name") && !this.config.getString("name").equals(this.name)) {
            this.config.getFile().renameTo(new File(this.config.getFile().getParent(), this.name + ".yml"));
            this.config = new Config(Samurai.getInstance(), this.name + ".yml", Samurai.getInstance().getEventHandler().getKothsFolder());
            this.config.set("name", this.name);
        }
        this.config.set("displayName", this.displayName);
        this.config.set("type", this.type.name());
        this.config.set("active", this.active);
        this.config.set("defaultCapSeconds", this.defaultCapSeconds);
        if (this.center != null) this.config.set("center", LocationUtils.serializeString(this.center));
        if (this.capZone != null) this.config.set("capZone", LocationUtils.serializeStringCuboid(this.capZone));
        this.config.save();
    }

    public void delete() {
        if (this.config.getFile().exists()) this.config.getFile().delete();
        Samurai.getInstance().getEventHandler().getEvents().remove(this.name);
    }


}
