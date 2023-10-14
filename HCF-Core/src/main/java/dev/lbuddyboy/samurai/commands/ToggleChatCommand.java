package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("togglechat|tgc|toggleglobalchat|toggleglobal")
public class ToggleChatCommand extends BaseCommand {

    @Default
    public static void toggleChat(Player sender) {
        boolean val = !Samurai.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        Samurai.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
    }

}