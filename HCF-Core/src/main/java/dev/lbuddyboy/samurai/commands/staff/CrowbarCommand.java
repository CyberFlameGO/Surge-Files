package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("crowbar")
@CommandPermission("op")
public class CrowbarCommand extends BaseCommand {

    @Default
    public static void crowbar(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InventoryUtils.CROWBAR);
        sender.sendMessage(ChatColor.YELLOW + "Gave you a crowbar.");
    }

    @Default
    @CommandCompletion("@players")
    public static void crowbar(CommandSender sender, @Name("player") OnlinePlayer target) {
        target.getPlayer().getInventory().addItem(InventoryUtils.CROWBAR);
        target.getPlayer().sendMessage(ChatColor.YELLOW + "You received a crowbar from " + sender.getName() + ".");
        sender.sendMessage(ChatColor.YELLOW + "You gave a crowbar to " + target.getPlayer().getName() + ".");
    }

}