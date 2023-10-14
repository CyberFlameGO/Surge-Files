package dev.lbuddyboy.pcore.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.pcore.listener.SelectionListener;
import org.bukkit.entity.Player;

@CommandAlias("selectionwand|pcorewand")
public class SelectionWandCommand extends BaseCommand {

    @Default
    public void wand(Player sender) {
        sender.getInventory().addItem(SelectionListener.CLAIM_WAND);
    }

}
