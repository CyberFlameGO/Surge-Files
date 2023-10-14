package dev.lbuddyboy.samurai.persist.maps;

import com.google.common.collect.Maps;
import dev.lbuddyboy.samurai.persist.maps.event.SyncPlaytimeEvent;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaytimeMap extends PersistMap<Long> {

    private Map<UUID, Long> joinDate = new HashMap<>();

    public PlaytimeMap() {
        super("PlayerPlaytimes", "Playtime");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> Bukkit.getPluginManager().callEvent(new SyncPlaytimeEvent(player)));
            }
        }, 20 * 30, 20 * 30);
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
        return (time.intValue());
    }

    public void playerJoined(UUID update) {
        joinDate.put(update, System.currentTimeMillis());

        if (!contains(update)) {
            updateValueAsync(update, 0L);
        }
    }

    public void unload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerQuit(player.getUniqueId(), false);
        }
    }

    public void playerQuit(UUID update, boolean async) {
        if (async) {
            updateValueAsync(update, getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        } else {
            updateValueSync(update, getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        }
        joinDate.remove(update);
    }

    public long getCurrentSession(UUID check) {
        if (joinDate.containsKey(check)) {
            return (System.currentTimeMillis() - joinDate.get(check));
        }

        return (0L);
    }

    public long getPlaytime(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public boolean hasPlayed(UUID check) {
        return (contains(check));
    }

    public void setPlaytime(UUID update, long playtime) {
        updateValueSync(update, playtime);
    }

    private static final long HOUR_IN_MS = 3_600_000L;

    private long calculateNextRewardTime(UUID uuid) {
        return System.currentTimeMillis() + ((HOUR_IN_MS * 2) - (((getPlaytime(uuid) * 1000L) + getCurrentSession(uuid)) % (HOUR_IN_MS * 2)));
    }

}