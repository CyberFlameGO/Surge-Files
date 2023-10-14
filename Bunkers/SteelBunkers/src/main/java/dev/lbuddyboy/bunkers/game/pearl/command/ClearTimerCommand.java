package dev.lbuddyboy.bunkers.game.pearl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.bunkers.game.pearl.EnderpearlCooldownHandler;
import org.bukkit.command.CommandSender;

@CommandAlias("enderpearl")
public class ClearTimerCommand extends BaseCommand {

    @Subcommand("remove")
    public static void execute(CommandSender player, @Name("player") OnlinePlayer target) {
        EnderpearlCooldownHandler.clearEnderpearlTimer(target.getPlayer());
    }

}
