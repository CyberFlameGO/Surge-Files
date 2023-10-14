package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.packet.QueueClearPacket;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.util.CC;
import org.bukkit.command.CommandSender;

@CommandAlias("clearqueue|clearq")
@CommandPermission("lqueue.admin")
public class ClearQueueCommand extends BaseCommand {

    @Default
    @CommandCompletion("@queues")
    public void clearQueue(CommandSender sender, @Name("queue") String queueName) {
        Queue queue = lQueueAPI.getQueueByName(queueName);

        if (queue == null) {
            sender.sendMessage(CC.translate("&cCould not find a queue under that name."));
            return;
        }

        lQueue.getInstance().getPidginHandler().sendPacket(new QueueClearPacket(queue, DistributionType.GLOBAL));
        sender.sendMessage(CC.translate("&aCleared the " + queueName + " queue successfully!"));
    }

}
