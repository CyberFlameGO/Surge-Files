package dev.lbuddyboy.samurai.custom.daily.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.daily.menu.DailyMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("daily|rewards|dailyclaim|claim|dailyrewards")
public class DailyCommand extends BaseCommand {

    @Default
    public static void daily(Player sender) {
        new DailyMenu().openMenu(sender);
    }

    @Subcommand("reload")
    @CommandPermission("op")
    public static void reload(CommandSender sender) {
        Samurai.getInstance().getDailyHandler().reload();
    }

    @Subcommand("reset")
    @CommandPermission("op")
    public static void daily(CommandSender sender, @Name("target") Player player) {
        Samurai.getInstance().getDailyHandler().getRewardsMap().setTime(player.getUniqueId(), 0L);
    }

    @Subcommand("resetreward")
    @CommandPermission("op")
    @CommandCompletion("@players @rewards")
    public static void daily(CommandSender sender, @Name("target") Player player, @Name("reward") String reward) {
        Samurai.getInstance().getDailyHandler().getRewards().get(reward).setActive(player.getUniqueId(), false);
    }

}
