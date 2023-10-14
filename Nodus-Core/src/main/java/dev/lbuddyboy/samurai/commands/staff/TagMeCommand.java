package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import org.bukkit.entity.Player;

@CommandAlias("tagme")
@CommandPermission("op")
public class TagMeCommand extends BaseCommand {

    @Default
    public static void tagMe(Player sender) {
        SpawnTagHandler.addOffensiveSeconds(sender, SpawnTagHandler.getMaxTagTime());
    }

}