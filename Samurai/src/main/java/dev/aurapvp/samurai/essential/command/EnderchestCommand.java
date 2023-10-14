package dev.aurapvp.samurai.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("enderchest|ec|echest")
@CommandPermission("samurai.command.enderchest")
public class EnderchestCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("target") @Optional OfflinePlayer target) {
        if (target == null) target = sender;

        Samurai.getInstance().getEnderchestHandler().openEnderChest(sender, target.getUniqueId());
    }
    
}
