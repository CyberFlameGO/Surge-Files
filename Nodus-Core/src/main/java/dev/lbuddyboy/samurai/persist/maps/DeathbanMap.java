package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class DeathbanMap extends PersistMap<Long> {

    public DeathbanMap() {
        super("Deathbans", "Deathban");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    @Override
    public Object getMongoValue(Long time) {
        return Long.toString(time);
    }

    public boolean isDeathbanned(UUID check) {
        if (getValue(check) != null) {
            return (getValue(check) > System.currentTimeMillis());
        }

        return (false);
    }

    public void deathban(UUID update, long seconds) {
        updateValueAsync(update, System.currentTimeMillis() + (seconds * 1000));
        Samurai.getInstance().getArenaHandler().getUuids().add(update);
    }

    public void reduce(UUID update, long seconds) {
        updateValueAsync(update, getDeathban(update) - (seconds * 1000));
    }

    public void set(UUID update, long seconds) {
        updateValueAsync(update, seconds);
    }

    public void revive(UUID update) {
        updateValueAsync(update, 0L);

        SpawnTagHandler.removeTag(UUIDUtils.name(update));
        Player player = Bukkit.getPlayer(update);
        if (player != null) {
            player.getInventory().clear();
            player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
            Samurai.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), 60 * 30);
        } else {
            Samurai.getInstance().getDiedMap().setActive(update, true);
        }
    }

    public long getDeathban(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public void wipeDeathbans() {
        wipeValues();
    }

    public Collection<UUID> getDeathbannedPlayers() {
        Collection<UUID> deathbannedPlayers = new HashSet<>();

        for (Map.Entry<UUID, Long> entry : wrappedMap.entrySet()) {
            if (isDeathbanned(entry.getKey())) {
                deathbannedPlayers.add(entry.getKey());
            }
        }

        return (deathbannedPlayers);
    }

}