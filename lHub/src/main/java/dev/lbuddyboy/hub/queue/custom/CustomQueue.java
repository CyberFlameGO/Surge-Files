package dev.lbuddyboy.hub.queue.custom;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.listener.BungeeListener;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.object.BungeeServer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:27 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.custom
 */

@RequiredArgsConstructor
@Data
public class CustomQueue {

    private final String name;
    private final LinkedList<UUID> players;
    private final BungeeServer server;
    private boolean paused;
    private long lastSent;

    public void load() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(lHub.getInstance(), () -> {
            this.server.request();

            if (this.players.isEmpty()) return;
            if (this.paused) return;

            Player player = Bukkit.getPlayer(this.players.getFirst());
            this.players.remove(this.players.getFirst());
            if (player == null) return;

            BungeeListener.sendPlayerToServer(player, this.name);
        }, 20L * lHub.getInstance().getDocHandler().getQueueDoc().getConfig().getInt("settings.queue-send-delay"), 20L * lHub.getInstance().getDocHandler().getQueueDoc().getConfig().getInt("settings.queue-send-delay"));

        Bukkit.getScheduler().runTaskTimerAsynchronously(lHub.getInstance(), () -> {
            if (lastSent + 800 < System.currentTimeMillis()) {
                for (Player player : this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList())) {
                    int position = this.players.indexOf(player.getUniqueId()) + 1;
                    int size = this.players.size();
                    String message = "&7You are currently &e#" + position + "&7 out of &e" + size + "&7 players in &6&l" + this.name + "'s&7 queue!";

//                    ActionBarAPI.sendActionBar(player, CC.translate(message));
                    lastSent = System.currentTimeMillis();
                }
            }
        }, 10, 10);
    }

    public int getQueueSize() {
        return this.players.size();
    }

    public void addToQueue(Player player) {
        if (this.players.contains(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are already in this queue."));
            return;
        }
        this.players.add(player.getUniqueId());
        this.players.forEach(queuePlayer -> {
            int pos = this.players.indexOf(queuePlayer);
            if (Bukkit.getPlayer(queuePlayer) != player && getPriority(player) < getPriority(Bukkit.getPlayer(queuePlayer))) {
                Collections.swap(this.players, pos, this.players.size() - 1);
            }
        });
    }

    // 0 being the lowest 15 being the highest
    public int getPriority(Player player) {
        for (int i = 0; i < 15; i++) {
            if (player.hasPermission("queue.priority." + i)) {
                return i;
            }
        }
        return 0;
    }

    public void removeFromQueue(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(lHub.getInstance(), () -> {
            this.players.remove(player.getUniqueId());
        }, 20L * lHub.getInstance().getDocHandler().getQueueDoc().getConfig().getInt("settings.queue-leave-delay"));
        lHub.getInstance().getQueueHandler().getPlayerTaskMap().put(player.getUniqueId(), task);
    }

}
