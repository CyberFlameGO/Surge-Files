package dev.lbuddyboy.hub.queue.impl;

import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.queue.QueueImpl;
import me.signatured.ezqueueshared.QueueInfo;
import me.signatured.ezqueuespigot.EzQueueAPI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:26 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.impl
 */
public class EzQueue implements QueueImpl {
    @Override
    public String id() {
        return "EZQUEUE";
    }

    @Override
    public int getPosition(Player player) {
        return EzQueueAPI.getPosition(player.getUniqueId());
    }

    @Override
    public int getQueueSize(Player player) {
        return inQueue(player) ? EzQueueAPI.getQueueSize(EzQueueAPI.getQueue(player)) : 0;
    }

    @Override
    public int getQueueSize(String queueName) {
        return EzQueueAPI.getQueueSize(queueName);
    }

    @Override
    public String getQueueName(Player player) {
        return inQueue(player) ? EzQueueAPI.getQueue(player) : "";
    }

    @Override
    public boolean inQueue(Player player) {
        return EzQueueAPI.getQueue(player) != null;
    }

    @Override
    public void addToQueue(Player player, String queue) {
        EzQueueAPI.addToQueue(player, queue);
    }

    @Override
    public List<String> getQueues() {
        return QueueInfo.getQueues().stream().map(QueueInfo::getQueue).collect(Collectors.toList());
    }

    @Override
    public void update(Placeholder placeholder) {

    }
}
