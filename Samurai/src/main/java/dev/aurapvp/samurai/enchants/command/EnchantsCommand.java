package dev.aurapvp.samurai.enchants.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.enchants.menu.EnchantsMenu;
import org.bukkit.entity.Player;

@CommandAlias("enchants")
public class EnchantsCommand extends BaseCommand {

    @Default
    public void enchanter(Player sender) {
        new EnchantsMenu().openMenu(sender);
    }

    @Subcommand("nbtinfo")
    @CommandPermission("op")
    public void info(Player sender) {
        NBTItem item = new NBTItem(sender.getItemInHand());

        System.out.println(item);
    }

}
