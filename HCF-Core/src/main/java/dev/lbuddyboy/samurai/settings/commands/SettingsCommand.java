package dev.lbuddyboy.samurai.settings.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;

@CommandAlias("settings|options|pref|preferences")
public class SettingsCommand extends BaseCommand {

    @Default
    public static void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}
