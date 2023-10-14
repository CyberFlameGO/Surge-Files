package dev.lbuddyboy.lqueue.listener;

import dev.lbuddyboy.libs.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.libs.pidgin.packet.listener.PacketListener;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.packet.*;
import dev.lbuddyboy.lqueue.util.BungeeUtils;
import dev.lbuddyboy.lqueue.util.CC;
import me.lemonypancakes.bukkit.api.actionbar.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueUpdateListener implements PacketListener {

    @IncomingPacketHandler
    public void onQueueUpdateListener(QueuePausedUpdatePacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        queue.setPaused(packet.status());
    }

    @IncomingPacketHandler
    public void onQueueStatusUpdate(QueueStatusUpdatePacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());
        Queue serializedQueue = packet.getQueue();

        if (queue == null) {
            return;
        }

        queue.setQueuePlayers(serializedQueue.getQueuePlayers());
        queue.setStartedAt(serializedQueue.getStartedAt());
        queue.setStoppedAt(serializedQueue.getStoppedAt());
        queue.setOnlinePlayers(serializedQueue.getOnlinePlayers());
        queue.setMaxPlayers(serializedQueue.getMaxPlayers());
        queue.setWhitelisted(serializedQueue.isWhitelisted());
    }

    @IncomingPacketHandler
    public void onQueueAddPlayer(QueueAddPlayerPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        QueuePlayer player = packet.getQueuePlayer();

        queue.getQueuePlayers().removeIf(qp -> qp.getUuid().toString().equals(player.getUuid().toString()));
        queue.getQueuePlayers().add(player);
        queue.prioritize();
    }

    @IncomingPacketHandler
    public void onQueueMessagePlayer(QueueMessagePlayerPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

        if (queue == null) return;

        QueuePlayer qp = queue.getQueuePlayer(packet.getQueuePlayer().getUuid());

        if (qp == null) return;

        Player player = Bukkit.getPlayer(qp.getUuid());

        if (player == null) return;

        double delay = lQueue.getInstance().getConfig().getDouble("messages.queue-reminder.message-delay");
        String message = lQueue.getInstance().getConfig().getString("messages.queue-reminder.message");

        if (!qp.canBeMessage(delay)) return;

        qp.setLastMessage(System.currentTimeMillis());

        if (lQueue.getInstance().getConfig().getBoolean("messages.queue-reminder.action-bar")) {
            ActionBarAPI.sendMessage(player, CC.translate(CC.replaceQueue(message, queue)).replaceAll("%pos%", String.valueOf(queue.getPosition(qp))));
            return;
        }

        player.sendMessage(CC.translate(CC.replaceQueue(message, queue)).replaceAll("%pos%", String.valueOf(queue.getPosition(qp))));
    }

    @IncomingPacketHandler
    public void onQueueRemovePlayer(QueueRemovePlayerPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        queue.getQueuePlayers().removeIf(qp -> qp.getUuid().toString().equals(packet.getQueuePlayer().getUuid().toString()));
        queue.prioritize();
    }

    @IncomingPacketHandler
    public void onQueueClear(QueueClearPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

        if (queue == null) {
            return;
        }

        queue.getQueuePlayers().clear();
    }

    @IncomingPacketHandler
    public void onQueueCreate(QueueCreatePacket packet) {
        Queue queue = lQueueAPI.createQueue(packet.getQueue());
        Bukkit.getLogger().info("New Queue Created! " + queue.getName() + " will now be displayed around all servers and able to be queued for!");
    }

    @IncomingPacketHandler
    public void onQueueSend(QueueSendPlayerPacket packet) {
        if (packet.distributionType() == DistributionType.DISTRIBUTED) return;

        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

        if (queue == null) {
            return;
        }

        QueuePlayer queuePlayer = packet.getQueuePlayer();
        Player player = Bukkit.getPlayer(queuePlayer.getUuid());

        queue.getQueuePlayers().removeIf(qp -> qp.getUuid().toString().equals(queuePlayer.getUuid().toString()));

        if (player == null) return;

        BungeeUtils.sendPlayerToServer(player, queue.getName());
    }

    @IncomingPacketHandler
    public void onQueueQuit(QueuePlayerQuitPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        QueuePlayer queuePlayer = packet.getQueuePlayer();
        QueuePlayer toUpdate = queue.getQueuePlayer(queuePlayer.getUuid());
        if (toUpdate == null) return;

        toUpdate.setLeftAt(queuePlayer.getLeftAt());
    }

    @IncomingPacketHandler
    public void onQueueJoin(QueuePlayerJoinPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        QueuePlayer queuePlayer = packet.getQueuePlayer();
        QueuePlayer toUpdate = queue.getQueuePlayer(queuePlayer.getUuid());

        if (toUpdate == null) return;

        toUpdate.setLeftAt(-1);
    }

}
