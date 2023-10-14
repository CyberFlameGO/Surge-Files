package dev.lbuddyboy.pcore.enchants.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.pcore.enchants.menu.EnchanterMenu;
import org.bukkit.entity.Player;

@CommandAlias("enchanter")
public class EnchanterCommand extends BaseCommand {

    @Default
    public void enchanter(Player sender) {
        new EnchanterMenu().openMenu(sender);
    }

}
