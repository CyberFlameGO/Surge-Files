package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.commands.menu.help.MapHelpMenu;
import org.bukkit.entity.Player;

@CommandAlias("mapkit")
public class KitCommand extends BaseCommand {

    @Default
    public static void kit(Player sender) {
        new MapHelpMenu().openMenu(sender);
    }
}
