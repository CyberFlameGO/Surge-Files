package dev.lbuddyboy.hub.queue.impl;

import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.placeholder.PlaceholderType;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.queue.QueueImpl;
import dev.lbuddyboy.hub.queue.custom.CustomQueue;
import dev.lbuddyboy.hub.queue.custom.command.JoinQueueCommand;
import dev.lbuddyboy.hub.queue.custom.command.PauseQueueCommand;
import dev.lbuddyboy.hub.queue.custom.command.WipeQueueCommand;
import dev.lbuddyboy.hub.queue.custom.command.param.QueueParam;
import dev.lbuddyboy.hub.util.object.BungeeServer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:26 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.impl
 */
public class Custom implements QueueImpl {

    public Custom(List<CustomQueue> queues) {
        lHub.getInstance().getCommandManager().getCommandCompletions().registerCompletion("queues", (s) -> queues.stream().map(CustomQueue::getName).collect(Collectors.toList()));
        lHub.getInstance().getCommandManager().getCommandContexts().registerContext(CustomQueue.class, new QueueParam());
        lHub.getInstance().getCommandManager().registerCommand(new JoinQueueCommand());
        lHub.getInstance().getCommandManager().registerCommand(new PauseQueueCommand());
        lHub.getInstance().getCommandManager().registerCommand(new WipeQueueCommand());
    }

    @Override
    public String id() {
        return "CustomQueue";
    }

    @Override
    public int getPosition(Player player) {
        if (!inQueue(player)) return -1;
        return lHub.getInstance().getQueueHandler().getQueueByPlayer(player).getPlayers().indexOf(player.getUniqueId()) + 1;
    }

    @Override
    public int getQueueSize(Player player) {
        if (!inQueue(player)) return 0;
        return lHub.getInstance().getQueueHandler().getQueueByPlayer(player).getQueueSize();
    }

    @Override
    public int getQueueSize(String queueName) {
        CustomQueue queue = lHub.getInstance().getQueueHandler().getQueueByName(queueName);
        if (queue == null) return 0;
        return queue.getQueueSize();
    }

    @Override
    public String getQueueName(Player player) {
        return inQueue(player) ? lHub.getInstance().getQueueHandler().getQueueByPlayer(player).getName() : "";
    }

    @Override
    public boolean inQueue(Player player) {
        return lHub.getInstance().getQueueHandler().getQueueByPlayer(player) != null;
    }

    @Override
    public void addToQueue(Player player, String queue) {
        CustomQueue q = lHub.getInstance().getQueueHandler().getQueueByName(queue);
        if (q == null) return;
        q.addToQueue(player);
    }

    @Override
    public List<String> getQueues() {
        return lHub.getInstance().getQueueHandler().getCustomQueues().stream().map(CustomQueue::getName).collect(Collectors.toList());
    }

    @Override
    public void update(Placeholder placeholder) {
        String replacement = "";
        if (placeholder.getType() == PlaceholderType.QUEUE_SIZE) {
            String queueName = placeholder.getHolder().replaceAll("%queue-size-", "").replaceAll("%", "");

            replacement = String.valueOf(getQueueSize(queueName));
        } else if (placeholder.getType() == PlaceholderType.QUEUE_NAME) {
            replacement = placeholder.getHolder().replaceAll("%queue-name-", "").replaceAll("%", "");
        } else if (placeholder.getType() == PlaceholderType.SERVER_ONLINE) {
            BungeeServer server = lHub.getInstance().getQueueHandler().getServer(placeholder.getHolder().replaceAll("%server-online-", "").replaceAll("%", ""));

            replacement = String.valueOf(server.getOnlinePlayers());
        } else if (placeholder.getType() == PlaceholderType.SERVER_MAX) {
            BungeeServer server = lHub.getInstance().getQueueHandler().getServer(placeholder.getHolder().replaceAll("%server-max-", "").replaceAll("%", ""));

            replacement = String.valueOf(server.getMaxPlayers());
        } else if (placeholder.getType() == PlaceholderType.SERVER_STATUS) {
            BungeeServer server = lHub.getInstance().getQueueHandler().getServer(placeholder.getHolder().replaceAll("%server-status-", "").replaceAll("%", ""));

            replacement = server.getStatus();
        }

        placeholder.setReplacement(replacement);
    }

    @Override
    public void loadPlaceholders() {
        for (String name : getQueues()) {
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%queue-position-" + name + "%", PlaceholderType.QUEUE_SIZE));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%queue-size-" + name + "%", PlaceholderType.QUEUE_SIZE));
        }
        for (BungeeServer server : lHub.getInstance().getQueueHandler().getServers()) {
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-online-" + server.getBungeeName() + "%", PlaceholderType.SERVER_ONLINE));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-max-" + server.getBungeeName() + "%", PlaceholderType.SERVER_MAX));
            lHub.getInstance().getSettingsHandler().getPlaceholders().add(new Placeholder("%server-status-" + server.getBungeeName() + "%", PlaceholderType.SERVER_STATUS));
        }
    }
}
