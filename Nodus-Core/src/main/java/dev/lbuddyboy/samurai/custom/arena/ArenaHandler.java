package dev.lbuddyboy.samurai.custom.arena;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.arena.command.DeathbanArenaCommand;
import dev.lbuddyboy.samurai.custom.arena.listener.ArenaListener;
import dev.lbuddyboy.samurai.custom.arena.thread.ArenaThread;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.Cuboid;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class ArenaHandler {

    private Cuboid safeZone;
    private Location spawn;
    private List<UUID> uuids;

    public ArenaHandler() {
        FileConfiguration configuration = Samurai.getInstance().getConfig();

        if (configuration.contains("deathban-safezone.spawn")) {
            this.spawn = LocationSerializer.deserializeString(Objects.requireNonNull(configuration.getString("deathban-safezone.spawn")));
        }

        if (configuration.contains("deathban-safezone.a") && configuration.contains("deathban-safezone.b")) {
            Location locA = LocationSerializer.deserializeString(Objects.requireNonNull(configuration.getString("deathban-safezone.a")));
            Location locB = LocationSerializer.deserializeString(Objects.requireNonNull(configuration.getString("deathban-safezone.b")));

            this.safeZone = new Cuboid(locA, locB);
        }

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ArenaListener(), Samurai.getInstance());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new DeathbanArenaCommand());

        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            this.uuids = new ArrayList<>(Samurai.getInstance().getDeathbanMap().getDeathbannedPlayers());

            new ArenaThread().start();
        });
    }

    public void save() {
        if (this.spawn != null) Samurai.getInstance().getConfig().set("deathban-safezone.spawn", LocationSerializer.serializeString(this.spawn));

        Samurai.getInstance().getConfig().set("deathban-safezone.a", LocationSerializer.serializeString(this.safeZone.getLowerNE()));
        Samurai.getInstance().getConfig().set("deathban-safezone.b", LocationSerializer.serializeString(this.safeZone.getUpperSW()));
        Samurai.getInstance().saveConfig();
    }

    public boolean isDeathbanned(UUID uuid) {
        return Samurai.getInstance().getDeathbanMap().isDeathbanned(uuid);
    }

    public boolean wasDeathbanned(UUID uuid) {
        return this.uuids.contains(uuid);
    }

    public Team getArenaTeam() {
        return Samurai.getInstance().getTeamHandler().getTeam("Deathban");
    }

}
