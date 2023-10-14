package dev.lbuddyboy.hub.queue.custom.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.hub.queue.custom.CustomQueue;
import org.bukkit.entity.Player;

@CommandAlias("joinqueue|play|queuejoin|join")
public class JoinQueueCommand extends BaseCommand {

    @Default
    public void joinQueue(Player sender, @Name("queue") CustomQueue queue) {
        queue.addToQueue(sender);
    }

}
