package dev.aurapvp.samurai.player;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.listener.PlayerListener;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Getter
public class PlayerHandler implements IModule {

    private final Map<UUID, SamuraiPlayer> players;

    public PlayerHandler() {
        this.players = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return "profiles";
    }

    @Override
    public void load(Samurai plugin) {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void reload() {

    }

    public SamuraiPlayer loadPlayer(UUID uuid, boolean async) {
        if (players.containsKey(uuid)) return players.get(uuid);

        return Samurai.getInstance().getStorageHandler().getStorage().loadPlayer(uuid, async);
    }

}
