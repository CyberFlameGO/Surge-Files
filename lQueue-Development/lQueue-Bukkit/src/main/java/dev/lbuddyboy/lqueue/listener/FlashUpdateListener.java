package dev.lbuddyboy.lqueue.listener;

import dev.lbuddyboy.libs.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.libs.pidgin.packet.listener.PacketListener;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.packet.QueuePausedUpdatePacket;

public class FlashUpdateListener implements PacketListener {

    @IncomingPacketHandler
    public void onQueueUpdateListener(QueuePausedUpdatePacket packet) {
        Queue queue = lQueueAPI.getQueueByName(packet.getQueue());

        if (queue == null) {
            return;
        }

        queue.setPaused(packet.status());
    }


}
