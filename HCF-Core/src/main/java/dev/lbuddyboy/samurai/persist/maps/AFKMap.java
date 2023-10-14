package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.persist.maps.event.SyncPlaytimeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AFKMap extends PersistMap<Long> implements Listener {

    private final Map<UUID, Long> afkDate = new HashMap<>();
    private final List<UUID> staffActions = new ArrayList<>();

    public AFKMap() {
        super("AFKTimes", "AFK");

        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("foxtrot.staff")) continue;

                if (!staffActions.contains(player.getUniqueId())) {
                    playerAFK(player.getUniqueId());
                    return;
                }

                staffActions.remove(player.getUniqueId());
                if (afkDate.containsKey(player.getUniqueId())) {
                    playerMoved(player.getUniqueId(), true);
                    afkDate.remove(player.getUniqueId());
                }
            }
        }, 20 * 5, 20 * 5);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().hasPermission("foxtrot.staff")) return;
        if (staffActions.contains(event.getPlayer().getUniqueId())) return;

        staffActions.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPermission("foxtrot.staff")) return;
        if (staffActions.contains(event.getPlayer().getUniqueId())) return;

        staffActions.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("foxtrot.staff")) return;
        if (staffActions.contains(event.getPlayer().getUniqueId())) return;

        staffActions.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().hasPermission("foxtrot.staff")) return;

        staffActions.remove(event.getPlayer().getUniqueId());

        if (afkDate.containsKey(event.getPlayer().getUniqueId())) {
            playerMoved(event.getPlayer().getUniqueId(), true);
            afkDate.remove(event.getPlayer().getUniqueId());
        }
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

    public void playerAFK(UUID update) {
        afkDate.put(update, System.currentTimeMillis());

        if (!contains(update)) {
            updateValueAsync(update, 0L);
        }
    }

    public void playerMoved(UUID update, boolean async) {
        if (async) {
            updateValueAsync(update, getPlaytime(update) + (System.currentTimeMillis() - afkDate.get(update)) / 1000);
        } else {
            updateValueSync(update, getPlaytime(update) + (System.currentTimeMillis() - afkDate.get(update)) / 1000);
        }
    }

    public long getCurrentSession(UUID check) {
        if (afkDate.containsKey(check)) {
            return (System.currentTimeMillis() - afkDate.get(check));
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