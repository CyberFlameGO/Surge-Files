package dev.lbuddyboy.hub.queue.custom.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.hub.queue.custom.CustomQueue;
import dev.lbuddyboy.hub.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("pausequeue|togglequeue|unpausequeue")
public class PauseQueueCommand extends BaseCommand {

    @Default
    public void pauseQueue(CommandSender sender, @Name("queue") CustomQueue queue) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!queue.getPlayers().contains(player.getUniqueId())) continue;

            player.sendTitle(CC.translate("&a&lQUEUE UPDATE"), CC.translate("&a" + queue.getName() + " Queue has been paused!"));
        }
        queue.setPaused(!queue.isPaused());

        sender.sendMessage(CC.translate("&aSuccessfully " + (queue.isPaused() ? "paused " : "unpaused ") + queue.getName() + "'s queue. Players will no longer be sent."));
    }

}
