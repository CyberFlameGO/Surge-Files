package dev.lbuddyboy.samurai.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import org.bukkit.entity.Player;

@CommandAlias("gc|pc")
public class TeamChatPublicCommand extends BaseCommand {

    @Default
    public static void gc(Player sender) {
        TeamChatCommand.setChat(sender, ChatMode.PUBLIC);
    }
}
