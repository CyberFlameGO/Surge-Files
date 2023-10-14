package dev.lbuddyboy.lqueue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.lqueue.menu.QueuesMenu;
import org.bukkit.entity.Player;

@CommandAlias("queues|queuesmenu|managequeues")
@CommandPermission("lqueue.admin")
public class QueuesCommand extends BaseCommand {

    @Default
    @CommandCompletion("@queues")
    public void manageQueues(Player sender) {
        new QueuesMenu().openMenu(sender);
    }

}
