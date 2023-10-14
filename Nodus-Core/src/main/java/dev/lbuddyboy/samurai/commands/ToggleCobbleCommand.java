package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("cobble")
public class ToggleCobbleCommand extends BaseCommand {

    @Default
    public static void cobble(Player sender) {
        boolean val = !Samurai.getInstance().getCobblePickupMap().isCobblePickup(sender.getUniqueId());


        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to pick up cobblestone while in Miner class!");
        Samurai.getInstance().getCobblePickupMap().setCobblePickup(sender.getUniqueId(), val);
    }

}
