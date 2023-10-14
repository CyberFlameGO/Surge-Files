package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("togglefd|fdtoggle|togglefinddiamond|fd")
public class FDToggleCommand extends BaseCommand {

    @Default
    public static void fdToggle(Player sender) {
        boolean val = !Samurai.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Found Diamonds messages!");
        Samurai.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(sender.getUniqueId(), val);
    }

}