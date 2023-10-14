package dev.lbuddyboy.lqueue.distributed.listener;

import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.distributed.packet.*;
import dev.lbuddyboy.lqueue.distributed.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.lqueue.distributed.pidgin.packet.listener.PacketListener;
import dev.lbuddyboy.lqueue.api.util.TimeUtils;

public class QueueUpdateListener implements PacketListener {

    @IncomingPacketHandler
    public void onQueueUpdateListener(QueuePausedUpdatePacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        queue.setPaused(packet.status());
        System.out.println("[lQueue-Distributed] Updated " + packet.getQueue() + "'s queue status to " + (packet.status() ? "paused" : "unpaused") + ".");
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
        if (queue.isOffline()) queue.setOnlinePlayers(0);

        System.out.println("[lQueue-Distributed] Updated " + packet.getQueue().getName() + "'s queue server statistics.");
        if (serializedQueue.getStoppedAt() > -1) {
            System.out.println("[lQueue-Distributed] Downtime: " + TimeUtils.formatLongIntoHHMMSS((System.currentTimeMillis() - serializedQueue.getStoppedAt()) / 1000));
        } else {
            System.out.println("[lQueue-Distributed] Up Time: " + TimeUtils.formatLongIntoHHMMSS((System.currentTimeMillis() - serializedQueue.getStartedAt()) / 1000));
        }
        System.out.println("[lQueue-Distributed] Online Players: " + serializedQueue.getOnlinePlayers() + "/" + serializedQueue.getMaxPlayers());
        System.out.println("[lQueue-Distributed] Whitelisted: " + serializedQueue.isWhitelisted());
        System.out.println("[lQueue-Distributed] Queued Players: " + serializedQueue.getQueuePlayers().size());
        System.out.println(" ");
    }

    @IncomingPacketHandler
    public void onQueueAddPlayer(QueueAddPlayerPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        QueuePlayer player = packet.getQueuePlayer();

        queue.getQueuePlayers().removeIf(qp -> qp.getUniqueId().equals(player.getUniqueId()));
        queue.getQueuePlayers().add(player);
        queue.prioritize();

        System.out.println("[lQueue-Distributed] Updated " + packet.getQueue() + "'s queue players. There is now " + queue.getQueuePlayers().size() + " players queued.");
    }

    @IncomingPacketHandler
    public void onQueueRemovePlayer(QueueRemovePlayerPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        queue.getQueuePlayers().removeIf(qp -> qp.getUniqueId().equals(packet.getQueuePlayer().getUniqueId()));
        queue.prioritize();
        System.out.println("[lQueue-Distributed] Updated " + packet.getQueue() + "'s queue players. There is now " + queue.getQueuePlayers().size() + " players queued.");
    }

    @IncomingPacketHandler
    public void onQueueCreate(QueueCreatePacket packet) {
        Queue queue = lQueueAPI.createQueue(packet.getQueue());

        System.out.println("[lQueue-Distributed] Created the " + queue.getName() + " queue.");
    }

    @IncomingPacketHandler
    public void onQueueSend(QueueSendPlayerPacket packet) {
        if (packet.distributionType() == DistributionType.BUKKIT) {
            Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

            if (queue == null) {
                return;
            }

            QueuePlayer queuePlayer = packet.getQueuePlayer();

            queue.getQueuePlayers().removeIf(qp -> qp.getUniqueId().equals(queuePlayer.getUniqueId()));
            return;
        }

        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

        if (queue == null) {
            return;
        }

        QueuePlayer queuePlayer = packet.getQueuePlayer();

        queue.getQueuePlayers().removeIf(qp -> qp.getUniqueId().equals(queuePlayer.getUniqueId()));

        System.out.println("[lQueue-Distributed] " + queuePlayer.getUuid().toString() + " ---> " + queue.getName());
    }

    @IncomingPacketHandler
    public void onQueueClear(QueueClearPacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue().getName());

        if (queue == null) {
            return;
        }

        queue.getQueuePlayers().clear();
        System.out.println("[lQueue-Distributed] Updated " + packet.getQueue() + "'s queue players to 0 due to a queue clear packet.");
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
        toUpdate.setLeftAtDuration(queuePlayer.getLeftAtDuration());

        System.out.println("[lQueue-Distributed] Offline Queue: " + toUpdate.getUuid().toString() + " (" + TimeUtils.formatIntoMMSS((int) (toUpdate.getLeftAtExpiry() / 1000)) + ")");
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
        System.out.println("[lQueue-Distributed] Offline Queue: " + queuePlayer.getUuid().toString() + " (RESET JOIN)");
    }

}
