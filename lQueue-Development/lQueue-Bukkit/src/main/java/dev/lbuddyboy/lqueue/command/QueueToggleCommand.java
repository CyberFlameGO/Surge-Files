package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.packet.QueuePausedUpdatePacket;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.util.CC;
import org.bukkit.command.CommandSender;

@CommandAlias("queuetoggle|qtoggle")
@CommandPermission("lqueue.admin")
public class QueueToggleCommand extends BaseCommand {

    @Default
    @CommandCompletion("@queues")
    public void toggleQueue(CommandSender sender, @Name("queue") String queueName) {
        Queue queue = lQueueAPI.getQueueByName(queueName);

        if (queue == null) {
            return;
        }

        queue.setPaused(!queue.isPaused());

        lQueue.getInstance().getPidginHandler().sendPacket(new QueuePausedUpdatePacket(queueName, queue.isPaused(), DistributionType.GLOBAL));
        sender.sendMessage(CC.translate("&aQueue " + queueName + " is now " + (queue.isPaused() ? "paused" : "unpaused") + "."));
    }

}
