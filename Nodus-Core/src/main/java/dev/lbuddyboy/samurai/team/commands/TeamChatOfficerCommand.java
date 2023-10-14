package dev.lbuddyboy.samurai.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import org.bukkit.entity.Player;

@CommandAlias("oc")
public class TeamChatOfficerCommand extends BaseCommand {
    @Default
    public static void oc(Player sender) {
        TeamChatCommand.setChat(sender, ChatMode.OFFICER);
    }
}
