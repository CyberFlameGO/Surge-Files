package dev.lbuddyboy.hub.queue.custom.command.param;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.queue.custom.CustomQueue;
import org.bukkit.entity.Player;

public class QueueParam implements ContextResolver<CustomQueue, BukkitCommandExecutionContext> {

    @Override
    public CustomQueue getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.popFirstArg();

        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            throw new InvalidCommandArgument("Invalid queue name.");
        }

        CustomQueue queue = lHub.getInstance().getQueueHandler().getQueueByName(source);
        if (queue != null) return queue;

        throw new InvalidCommandArgument("No queue with the name " + source + " could be found.");
    }
}