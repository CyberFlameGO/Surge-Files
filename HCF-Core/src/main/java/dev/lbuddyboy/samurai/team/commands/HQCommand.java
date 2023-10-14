package dev.lbuddyboy.samurai.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("hq|home")
public class HQCommand extends BaseCommand {

    @Default
    public static void hq(Player sender) {
        TeamCommands.teamHQ(sender);
    }

}
