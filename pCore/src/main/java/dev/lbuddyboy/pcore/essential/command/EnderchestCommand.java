package dev.lbuddyboy.pcore.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("enderchest|ec|echest")
@CommandPermission("pcore.command.enderchest")
public class EnderchestCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("target") @Optional OfflinePlayer target) {
        if (target == null) target = sender;

        pCore.getInstance().getEnderchestHandler().openEnderChest(sender, target.getUniqueId());
    }
    
}
