package dev.lbuddyboy.lqueue.listener;

import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.packet.QueuePlayerJoinPacket;
import dev.lbuddyboy.lqueue.packet.QueuePlayerQuitPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OfflineQueueListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Queue queue = lQueueAPI.getQueueByPlayer(player.getUniqueId());

        if (queue == null) return;

        QueuePlayer queuePlayer = queue.getQueuePlayer(player.getUniqueId());

        queuePlayer.setLeftAt(System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(lQueue.getInstance(), () -> {
            lQueue.getInstance().getPidginHandler().sendPacket(new QueuePlayerQuitPacket(queue.getName(), queuePlayer, DistributionType.GLOBAL));
        }, 20);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Queue queue = lQueueAPI.getQueueByPlayer(player.getUniqueId());

        if (queue == null) return;

        QueuePlayer queuePlayer = queue.getQueuePlayer(player.getUniqueId());

        queuePlayer.setLeftAt(-1);

        Bukkit.getScheduler().runTaskLater(lQueue.getInstance(), () -> {
            lQueue.getInstance().getPidginHandler().sendPacket(new QueuePlayerJoinPacket(queue.getName(), queuePlayer, DistributionType.GLOBAL));
        }, 30);
    }

}
