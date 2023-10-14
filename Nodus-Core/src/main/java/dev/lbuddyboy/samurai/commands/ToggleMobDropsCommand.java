package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("mobdrops")
public class ToggleMobDropsCommand extends BaseCommand {

    @Default
    public static void mobdrops(Player sender) {
        boolean val = !Samurai.getInstance().getMobDropsPickupMap().isMobPickup(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to pick up mob drops!");
        Samurai.getInstance().getMobDropsPickupMap().setMobPickup(sender.getUniqueId(), val);
    }

}
