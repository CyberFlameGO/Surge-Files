package dev.lbuddyboy.hub.queue.impl;

import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.placeholder.PlaceholderType;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.queue.QueueImpl;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.Queue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:26 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.impl
 */
public class lQueue implements QueueImpl {

    @Override
    public String id() {
        return "LQUEUE";
    }

    @Override
    public int getPosition(Player player) {
        Queue queue = getQueue(player);

        return queue == null ? -1 : queue.getPosition(queue.getQueuePlayer(player.getUniqueId()));
    }

    @Override
    public int getQueueSize(Player player) {
        return inQueue(player) ? getQueue(player).getQueuePlayers().size() : -1;
    }

    @Override
    public int getQueueSize(String queueName) {
        Queue queue = getQueue(queueName);

        if (queue == null) return -1;

        return queue.getQueuePlayers().size();
    }

    @Override
    public String getQueueName(Player player) {
        return inQueue(player) ? getQueue(player).getName() : "";
    }

    @Override
    public boolean inQueue(Player player) {
        return getQueue(player) != null;
    }

    @Override
    public void addToQueue(Player player, String queue) {
        Queue toAdd = getQueue(queue);

        if (toAdd == null) return;

        Bukkit.dispatchCommand(player, "joinq " + queue);
    }

    @Override
    public List<String> getQueues() {
        return lQueueAPI.getQueues().stream().map(Queue::getName).collect(Collectors.toList());
    }

    @Override
    public void update(Placeholder placeholder) {
        String replacement = "";
        String online = lHub.getInstance().getSettingsHandler().getOnline();
        String offline = lHub.getInstance().getSettingsHandler().getOffline();
        String whitelisted = lHub.getInstance().getSettingsHandler().getWhitelisted();

        if (placeholder.getType() == PlaceholderType.QUEUE_SIZE) {
            String queueName = placeholder.getHolder().replaceAll("%queue-size-", "").replaceAll("%", "");

            replacement = String.valueOf(getQueueSize(queueName));
        } else if (placeholder.getType() == PlaceholderType.QUEUE_NAME) {
            replacement = placeholder.getHolder().replaceAll("%queue-name-", "").replaceAll("%", "");
        } else if (placeholder.getType() == PlaceholderType.SERVER_STATUS) {
            Queue queue = getQueue(placeholder.getHolder().replaceAll("%server-status-", "").replaceAll("%", ""));
            if (queue == null) return;

            replacement = queue.isWhitelisted() && !queue.isOffline() ? whitelisted : queue.isOffline() ? offline : online;
        } else if (placeholder.getType() == PlaceholderType.SERVER_MAX) {
            Queue queue = getQueue(placeholder.getHolder().replaceAll("%server-max-", "").replaceAll("%", ""));
            if (queue == null) return;

            replacement = String.valueOf(queue.getMaxPlayers());
        } else if (placeholder.getType() == PlaceholderType.SERVER_ONLINE) {
            String queueName = placeholder.getHolder().replaceAll("%server-online-", "").replaceAll("%", "");
            Queue queue = getQueue(queueName);
            if (queue == null) {
                if (queueName.equals("Bunkers")) {
                    int total = 0;
                    for (Queue bunker : lQueueAPI.getQueues()) {
                        if (!bunker.getName().startsWith("Bunkers-")) continue;

                        total += bunker.getOnlinePlayers();
                    }
                    replacement = String.valueOf(SpoofHandler.getSpoofedCount(total));
                } else {
                    return;
                }
            } else {
                replacement = String.valueOf(SpoofHandler.getSpoofedCount(queue.getOnlinePlayers()));
            }
        }

        placeholder.setReplacement(replacement);
    }

    @Override
    public int getCount(String queueName) {
        Queue queue = getQueue(queueName);
        if (queue == null) return 0;

        return queue.getOnlinePlayers();
    }

    @Override
    public void loadPlaceholders() {
        for (String name : getQueues()) {
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%queue-position-" + name + "%", PlaceholderType.QUEUE_SIZE));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%queue-size-" + name + "%", PlaceholderType.QUEUE_SIZE));
        }
        for (Queue queue : lQueueAPI.getQueues()) {
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-online-" + queue.getName() + "%", PlaceholderType.SERVER_ONLINE));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-max-" + queue.getName() + "%", PlaceholderType.SERVER_MAX));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-status-" + queue.getName() + "%", PlaceholderType.SERVER_STATUS));
        }
        lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-online-Bunkers%", PlaceholderType.SERVER_ONLINE));
    }

    public Queue getQueue(Player player) {
        return lQueueAPI.getQueueByPlayer(player.getUniqueId());
    }

    public Queue getQueue(String name) {
        return lQueueAPI.getQueueByName(name);
    }

}
