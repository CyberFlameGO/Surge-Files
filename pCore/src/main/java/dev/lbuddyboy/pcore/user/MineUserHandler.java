package dev.lbuddyboy.pcore.user;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.listener.MineUserListener;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Getter
public class MineUserHandler implements IModule {

    private final Map<UUID, MineUser> users;

    public MineUserHandler() {
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        Bukkit.getPluginManager().registerEvents(new MineUserListener(), plugin);
    }

    @Override
    public void unload(pCore plugin) {
        for (Map.Entry<UUID, MineUser> entry : new ConcurrentHashMap<>(this.users).entrySet()) {
            if (entry.getValue().isUpdated()) {
                entry.getValue().save(false);
                System.out.println("Executed an update for " + entry.getKey().toString() + ".");
            }
        }
    }

    @Override
    public void save() {
        for (Map.Entry<UUID, MineUser> entry : new ConcurrentHashMap<>(this.users).entrySet()) {
            if (entry.getValue().isRemovable()) {
                entry.getValue().save();
                this.users.remove(entry.getKey());
                System.out.println("Removed an inactive mine profile for " + entry.getKey().toString() + ".");
            }
            if (!entry.getValue().isRemovable() && entry.getValue().isUpdated()) {
                entry.getValue().save();
                System.out.println("Executed an update for " + entry.getKey().toString() + ".");
            }
        }
    }

    @Override
    public long cooldown() {
        return 60_000L * 5;
    }

    public MineUser getMineUser(UUID uuid) {
        return getMineUser(uuid, false);
    }

    public MineUser getMineUser(UUID uuid, boolean searchDB) {
        if (this.users.containsKey(uuid)) return this.users.get(uuid);

        if (searchDB) return pCore.getInstance().getStorageHandler().getStorage().loadMineUser(uuid, true);

        return null;
    }

    public CompletableFuture<MineUser> getMineUserAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getMineUser(uuid, true));
    }

    public MineUser tryMineUserAsync(UUID uuid) {
        try {
            return getMineUserAsync(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
