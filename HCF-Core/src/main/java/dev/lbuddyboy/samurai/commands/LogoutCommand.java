package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.ServerHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("logout")
public class LogoutCommand extends BaseCommand {

    @Default
    public static void logout(Player sender) {
        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You can't log out while you're frozen!");
            return;
        }

        if(ServerHandler.getTasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return; // dont potato and let them spam logouts
        }

        Samurai.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}