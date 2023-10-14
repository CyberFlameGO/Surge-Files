package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.packet.QueueRemovePlayerPacket;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("leavequeue|leaveq")
public class LeaveQueueCommand extends BaseCommand {

    @Default
    @CommandCompletion("@queues")
    public void leaveQueue(Player sender) {
        Queue queue = lQueueAPI.getQueueByPlayer(sender.getUniqueId());

        if (queue == null) {
            sender.sendMessage(CC.translate("&cYou are not in a queue."));
            return;
        }

        QueuePlayer player = queue.getQueuePlayer(sender.getUniqueId());
        queue.getQueuePlayers().removeIf(qp -> qp.getUuid().toString().equals(player.getUuid().toString()));

        lQueue.getInstance().getPidginHandler().sendPacket(new QueueRemovePlayerPacket(queue.getName(), player, DistributionType.GLOBAL));
        sender.sendMessage(CC.translate("&aLeft the " + queue.getName() + " queue successfully!"));
    }

}
