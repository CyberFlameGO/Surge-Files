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

@CommandAlias("wipequeue|clearqueue|queueclear|queuewipe")
public class WipeQueueCommand extends BaseCommand {

    @Default
    public void wipeQueue(CommandSender sender, @Name("queue") CustomQueue queue) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!queue.getPlayers().contains(player.getUniqueId())) continue;

            player.sendTitle(CC.translate("&a&lQUEUE CLEARED"), CC.translate("&a" + queue.getName() + " Queue has been reset!"));
        }
        queue.getPlayers().clear();

        sender.sendMessage(CC.translate("&aSuccessfully clear " + queue.getName() + "'s queue."));
    }

}
