package dev.lbuddyboy.samurai.map.game;

import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameHandlerConfig {

    private Location lobbySpawn;
    private final List<GameArena> arenas = new ArrayList<>();

    public GameArena getArenaByName(String name) {
        for (GameArena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void setLobbySpawnLocation(Location location) {
        this.lobbySpawn = location;
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void trackArena(GameArena arena) {
        arenas.add(arena);
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void forgetArena(GameArena arena) {
        arenas.remove(arena);
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

}
