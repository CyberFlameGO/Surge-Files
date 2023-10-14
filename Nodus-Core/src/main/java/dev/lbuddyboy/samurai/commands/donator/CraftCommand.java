package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("workbench|craft")
@CommandPermission("foxtrot.craft")
public class CraftCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

}
