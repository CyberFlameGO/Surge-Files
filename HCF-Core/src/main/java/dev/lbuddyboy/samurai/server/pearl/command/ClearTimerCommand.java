package dev.lbuddyboy.samurai.server.pearl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import org.bukkit.command.CommandSender;

@CommandAlias("enderpearl")
@CommandPermission("op")
public class ClearTimerCommand extends BaseCommand {

    @Subcommand("remove")
    public static void ex(CommandSender sender, @Name("player") @Flags("other") OnlinePlayer target) {
        EnderpearlCooldownHandler.clearEnderpearlTimer(target.getPlayer());
    }

}
