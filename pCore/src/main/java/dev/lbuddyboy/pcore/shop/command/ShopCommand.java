package dev.lbuddyboy.pcore.shop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.pcore.shop.menu.ShopMainMenu;
import dev.lbuddyboy.pcore.shop.menu.editor.ShopEditorMenu;
import org.bukkit.entity.Player;

@CommandAlias("shop")
public class ShopCommand extends BaseCommand {

    @Default
    public void shop(Player sender) {
        new ShopMainMenu().openMenu(sender);
    }

    @Subcommand("editor")
    public void editor(Player sender) {
        new ShopEditorMenu().openMenu(sender);
    }

}
