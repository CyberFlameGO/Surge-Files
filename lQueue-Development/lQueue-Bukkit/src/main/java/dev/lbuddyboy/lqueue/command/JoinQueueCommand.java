package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.packet.QueueAddPlayerPacket;
import dev.lbuddyboy.lqueue.packet.QueueRemovePlayerPacket;
import dev.lbuddyboy.lqueue.util.BungeeUtils;
import dev.lbuddyboy.lqueue.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("joinqueue|joinq")
public class JoinQueueCommand extends BaseCommand {

    @Default
    @CommandCompletion("@queues")
    public void joinQueue(Player sender, @Name("queue") String queueName) {
        Queue queue = lQueueAPI.getQueueByName(queueName);

        if (queue == null) {
            sender.sendMessage(CC.translate("&cCould not find a queue under that name."));
            return;
        }

        queue = lQueueAPI.getQueueByPlayer(sender.getUniqueId());
        if (queue != null) {

            QueuePlayer player = queue.getQueuePlayer(sender.getUniqueId());
            queue.getQueuePlayers().removeIf(qp -> qp.getUuid().toString().equals(player.getUuid().toString()));

            lQueue.getInstance().getPidginHandler().sendPacket(new QueueRemovePlayerPacket(queue.getName(), player, DistributionType.GLOBAL));
            sender.sendMessage(CC.translate("&aLeft the " + queue.getName() + " queue successfully!"));
        }
        queue = lQueueAPI.getQueueByName(queueName);

        if (sender.hasPermission("lqueue.staff")) {
            BungeeUtils.sendPlayerToServer(sender.getPlayer(), queueName);
            sender.sendMessage(CC.translate("&aBypassing the " + queueName + " successfully!"));
            return;
        }

        QueuePlayer player = new QueuePlayer(sender.getUniqueId().toString());

        player.setPriority(lQueue.getInstance().getPriority(sender));
        player.setLeftAtDuration(lQueue.getInstance().getOfflineQueue(sender) * 1000L);

        queue.getQueuePlayers().add(player);
        queue.prioritize();

        lQueue.getInstance().getPidginHandler().sendPacket(new QueueAddPlayerPacket(queueName, player, DistributionType.GLOBAL));
        sender.sendMessage(CC.translate("&aJoined the " + queueName + " successfully!"));
    }

}
