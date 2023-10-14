package dev.lbuddyboy.samurai.custom.battlepass.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassProgress;
import dev.lbuddyboy.samurai.custom.battlepass.menu.BattlePassMenu;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("battlepass|bp|pass|challenges|missions")
public class BattlePassCommand extends BaseCommand {

    @Default
    public static void execute(Player player) {

        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.BATTLE_PASS)) {
            player.sendMessage(CC.translate("&cThis feature is currently disabled."));
            return;
        }

        if (Samurai.getInstance().getBattlePassHandler() == null) {
            return;
        }

        BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
        if (progress != null) {
            new BattlePassMenu(progress).openMenu(player);
        } else {
            player.sendMessage(CC.RED + "Couldn't open BattlePass!");
        }
    }

    @Subcommand("disable")
    @CommandPermission("battlepass.disable")
    public static void execute(CommandSender sender) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Samurai.getInstance().getBattlePassHandler().setAdminDisabled(!Samurai.getInstance().getBattlePassHandler().isAdminDisabled());

        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            sender.sendMessage(ChatColor.RED + "BattlePass has been temporarily disabled!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "BattlePass has been enabled!");
        }
    }

    @Subcommand("dump")
    @CommandPermission("battlepass.dump")
    public static void dump(CommandSender sender, @Name("player") UUID targetUuid) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        sender.sendMessage(FrozenUUIDCache.name(progress.getUuid()) + "'s BattlePass Dump");
        sender.sendMessage("XP: " + progress.getExperience());
        sender.sendMessage("Tier: " + progress.getCurrentTier().getNumber());
        sender.sendMessage("Premium: " + progress.isPremium());
        sender.sendMessage("Challenges Completed: " + progress.getCompletedChallenges().size());
        sender.sendMessage("Daily Challenges Completed: " + progress.getCompletedDailyChallenges().size());
    }

    @Subcommand("reset")
    @CommandPermission("battlepass.reset")
    public static void reset(CommandSender player, @Name("player") UUID targetUuid) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            player.sendMessage(ChatColor.RED + "The Season Pass is not enabled on this server!");
            return;
        }

        Samurai.getInstance().getBattlePassHandler().clearProgress(targetUuid);
        player.sendMessage(ChatColor.GREEN + "Cleared Season Pass progress of " + ChatColor.WHITE + FrozenUUIDCache.name(targetUuid) + ChatColor.GREEN + "!");
    }

    @Subcommand("resetdaily")
    @CommandPermission("battlepass.resetdaily")
    public static void resetDaily(CommandSender sender) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Samurai.getInstance().getBattlePassHandler().generateNewDailyChallenges();
        sender.sendMessage(ChatColor.GREEN + "You have generated a new Daily Challenges set!");
    }

    @Subcommand("setpremium")
    @CommandPermission("battlepass.setpremium")
    public static void setpremium(CommandSender sender, @Name("player") UUID targetUuid) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        progress.setPremium(true);
        progress.requiresSave();

        Samurai.getInstance().getBattlePassHandler().saveProgress(progress);

        Player player = Bukkit.getPlayer(targetUuid);
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "You've been give the Premium BattlePass! Type /bp to get started!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }

        sender.sendMessage(ChatColor.GREEN + "Granted premium BattlePass to " + ChatColor.WHITE + FrozenUUIDCache.name(targetUuid) + ChatColor.GREEN + "!");
    }

    @Subcommand("setxp")
    @CommandPermission("battlepass.setxp")
    public static void setXp(CommandSender sender, @Name("player") UUID targetUuid, @Name("xp") int xp) {
        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        progress.setExperience(xp);
        progress.requiresSave();

        Samurai.getInstance().getBattlePassHandler().saveProgress(progress);

        sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + FrozenUUIDCache.name(targetUuid) + ChatColor.GREEN + "'s BattlePass XP to " + ChatColor.WHITE + Formats.formatNumber(xp) + ChatColor.GREEN + "!");
    }

    @Subcommand("wipe")
    @CommandPermission("battlepass.wipe")
    public static void wipe(CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed through console!");
            return;
        }

        if (Samurai.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Samurai.getInstance().getBattlePassHandler().wipe();
    }

}
