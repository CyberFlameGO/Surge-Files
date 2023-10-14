package dev.lbuddyboy.samurai.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("sethq|sethome")
public class SetHQCommand extends BaseCommand {

    @Default
    public static void sethq(Player sender) {
        TeamCommands.teamSetHQ(sender);
    }

}
