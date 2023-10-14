package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.packet.QueueRemovePlayerPacket;
import dev.lbuddyboy.lqueue.util.BungeeUtils;
import dev.lbuddyboy.lqueue.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("go|server")
@CommandPermission("lqueue.staff")
public class GoCommand extends BaseCommand {

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

        BungeeUtils.sendPlayerToServer(sender.getPlayer(), queueName);

        sender.sendMessage(CC.translate("&aJoined " + queueName + " queue & bypassed successfully!"));
    }

}
