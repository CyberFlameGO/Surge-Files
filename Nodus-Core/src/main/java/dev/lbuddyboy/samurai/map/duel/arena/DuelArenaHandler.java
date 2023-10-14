package dev.lbuddyboy.samurai.map.duel.arena;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DuelArenaHandler {

    @Getter private List<DuelArena> arenas = new ArrayList<>();

    private static File ARENAS_FILE = new File(Samurai.getInstance().getDataFolder(), "kitmap-duel-arenas.json");

    public DuelArenaHandler() {

        // load arenas
        if (ARENAS_FILE.exists()) {
            try (Reader reader = Files.newReader(ARENAS_FILE, Charsets.UTF_8)) {
                Type arenaListType = new TypeToken<List<DuelArena>>() {}.getType();
                arenas = Samurai.PLAIN_GSON.fromJson(reader, arenaListType);
            } catch (IOException e) {
                Samurai.getInstance().getLogger().severe("Failed to load duel arenas!");
                e.printStackTrace();
            }
        }
    }

    public void saveArenas() {
        try {
            Files.write(Samurai.PLAIN_GSON.toJson(arenas), ARENAS_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            Samurai.getInstance().getLogger().severe("Failed to save duel arenas!");
            e.printStackTrace();
        }
    }


    public DuelArena getArenaByName(String name) {
        return arenas.stream()
                .filter(arena -> arena.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public DuelArena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }

        return arenas.get(Samurai.RANDOM.nextInt(arenas.size()));
    }

    public void addArena(DuelArena arena) {
        arenas.add(arena);
        saveArenas();
    }

    public void removeArena(DuelArena arena) {
        arenas.remove(arena);
        saveArenas();
    }
}
