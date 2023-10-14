package dev.lbuddyboy.crates.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("keys|key|cratekeys|crates")
public class KeysCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        CrateCommand.menu(sender);
    }

}
