package dev.lbuddyboy.lqueue.distributed.thread;

import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.distributed.lQueueDistributed;
import dev.lbuddyboy.lqueue.distributed.packet.QueueMessagePlayerPacket;
import dev.lbuddyboy.lqueue.distributed.packet.QueueRemovePlayerPacket;
import dev.lbuddyboy.lqueue.distributed.packet.QueueSendPlayerPacket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QueueUpdateThread extends Thread {

    private lQueueDistributed distributed;

    @Override
    public void run() {
        while (this.distributed.threads) {
            try {
                for (Queue queue : lQueueAPI.getQueues()) {
                    if (queue.getQueuePlayers().size() < 1) continue;

                    for (QueuePlayer player : queue.getQueuePlayers()) {
                        if (!player.isOnline() && player.isOfflineExpired()) {
                            removePlayer(queue, player);
                            System.out.println("[lQueue-Distributed] Removing " + player.getUuid().toString() + " from the " + queue.getName() + " queue due to being offline too long.");
                            continue;
                        }
                        if (!player.isOnline()) continue;

                        lQueueDistributed.getInstance().getPidginHandler().sendPacket(new QueueMessagePlayerPacket(queue, player, DistributionType.BUKKIT));
                    }

                    if (!queue.isWhitelisted() && !queue.isPaused()) {
                        for (QueuePlayer player : queue.getQueuePlayers()) {
                            if (!player.isOnline()) continue;
                            if (queue.isWhitelisted() || queue.isPaused()) {
                                if (player.canBypass()) {
                                    sendPlayer(queue, player);
                                }
                                continue;
                            }

                            sendPlayer(queue, player);
                            break;
                        }
                    }
                }

                for (Queue queue : lQueueAPI.getQueues()) {
                    if (queue.getQueuePlayers().size() < 1) continue;

                    queue.save(lQueueDistributed.getInstance().getJedisPool());
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void removePlayer(Queue queue, QueuePlayer player) {
        lQueueDistributed.getInstance().getPidginHandler().sendPacket(new QueueRemovePlayerPacket(queue.getName(), player, DistributionType.GLOBAL));
    }

    public synchronized void sendPlayer(Queue queue, QueuePlayer player) {
        lQueueDistributed.getInstance().getPidginHandler().sendPacket(new QueueSendPlayerPacket(queue, player, DistributionType.BUKKIT));
    }

}
