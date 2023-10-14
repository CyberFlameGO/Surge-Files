package dev.lbuddyboy.samurai.map.shards.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.shards.ShardHandler;
import dev.lbuddyboy.samurai.map.shards.menu.ShardMenu;
import dev.lbuddyboy.samurai.map.shards.menu.ExchangeMenu;
import dev.lbuddyboy.samurai.persist.maps.ShardMap;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

@CommandAlias("shards|shard")
public class ShardsCommand extends BaseCommand {

    @Subcommand("help")
    public static void help(CommandSender sender) {
        String[] msg = {
                "§8§m-------------------------------------------",
                "§6§lShard Help",
                " ",
                "§7/shards §f- View your shard balance.",
                "§7/shards <player> §f- View a player's shard balance.",
                "§7/shardshop §f- Open the shard shop.",
                "§7/shard pay <player> <amount> §f- Send a player shards.",

                "§8§m-------------------------------------------",
        };

        sender.sendMessage(msg);
    }


    @Subcommand("exchange|trade")
    public static void exchange(Player sender) {
        new ExchangeMenu().openMenu(sender);
    }

    @Default
    @CommandCompletion("@players")
    public static void def(Player sender, @Name("player") @Flags("self") @Optional UUID target) {
        if (target == null) target = sender.getUniqueId();
        shards(sender, target);
    }

    @Subcommand("balance|bal|shards")
    @CommandCompletion("@players")
    public static void shards(Player sender, @Name("player") @Flags("self") @Optional UUID target) {
        ShardMap gemMap = Samurai.getInstance().getShardMap();

        if (sender.getUniqueId() == target) {
            sender.sendMessage(CC.GOLD + "Shards: ◆" + CC.WHITE + gemMap.getShards(target));
        } else {
            sender.sendMessage(CC.GOLD + UUIDUtils.name(target) + "'s Shards: ◆" + CC.WHITE + gemMap.getShards(target));
        }
    }

    @Subcommand("shop")
    public static void gemShop(Player sender) {

        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.SHARD_SHOP)) {
            sender.sendMessage(CC.translate("&cThis feature is currently disabled."));
            return;
        }

        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(CC.RED + "You cannot do this while spawn tagged!");
            return;
        }

        if (Samurai.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Samurai.getInstance().getMapHandler().getGameHandler() != null
                && Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                && Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())
        ) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a game!");
            return;
        }

        new ShardMenu().openMenu(sender);
    }

    @Subcommand("pay|send")
    @CommandCompletion("@players")
    public static void gemPay(Player sender, @Name("player") Player target, @Name("amount") int amount) {
        if (amount <= 0) {
            sender.sendMessage(CC.RED + "Invalid shard amount.");
            return;
        }

        if (sender == target) {
            sender.sendMessage(CC.RED + "You cannot pay yourself!");
            return;
        }

        if (!Samurai.getInstance().getShardMap().removeShards(sender.getUniqueId(), amount)) {
            sender.sendMessage(CC.RED + "You do not have enough shards for this!");
            return;
        }

        Samurai.getInstance().getShardMap().addShards(target.getUniqueId(), amount, true);
        sender.sendMessage(CC.WHITE + "You paid " + target.getDisplayName() + " " + CC.GOLD + "◆" + amount + CC.WHITE + " shards!");
        target.sendMessage(sender.getDisplayName() + CC.WHITE + " has sent you " + CC.GOLD + "◆" + amount + CC.WHITE + " shards!");
    }

    @Subcommand("set")
    @CommandPermission("foxtrot.admin")
    @CommandCompletion("@players")
    public static void gemsSet(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
        Samurai.getInstance().getShardMap().setValue(player, amount);
        if (Bukkit.getPlayer(player) != null)
            Bukkit.getPlayer(player).sendMessage(CC.GREEN + "Your shards have been set to " + amount + ".");
        sender.sendMessage(CC.GREEN + "Set " + UUIDUtils.name(player) + " shards to " + amount);
    }

    @Subcommand("add")
    @CommandPermission("foxtrot.admin")
    @CommandCompletion("@players")
    public static void gemsAdd(CommandSender sender,
                               @Name("player") UUID target,
                               @Name("amount") int amount) {
        ShardMap gemMap = Samurai.getInstance().getShardMap();
        long newAmount = gemMap.getShards(target) + amount;
        gemMap.setValue(target, newAmount);
        sender.sendMessage(CC.GREEN + "Added " + amount + " shards to " + target + " total: " + newAmount);
    }

    @Subcommand("hour start")
    @CommandPermission("foxtrot.admin")
    public static void gemHour(CommandSender sender, @Name("time") String time) {
        int seconds;
        try {
            seconds = TimeUtils.parseTime(time);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(CC.RED + e.getMessage());
            return;
        }
        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------"));
        Bukkit.broadcastMessage(CC.translate("&6&lDouble Shards Event"));
        Bukkit.broadcastMessage(CC.translate(" "));
        Bukkit.broadcastMessage(CC.translate("&fAll &6shards&f that are earned"));
        Bukkit.broadcastMessage(CC.translate("&fwill now be doubled."));
        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------"));

        SOTWCommand.getCustomTimers().put(ShardHandler.DOUBLE_GEM_PREFIX, System.currentTimeMillis() + (seconds * 1000));
        try {
            DiscordLogger.logSpecialEvent("Double Shard Event", TimeUtils.formatIntoDetailedString(seconds), "This event will double any earned shards as long as the timer is active.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Subcommand("hour stop")
    public static void gemHour(Player sender) {
        Long removed = SOTWCommand.getCustomTimers().remove(ShardHandler.DOUBLE_GEM_PREFIX);
        if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the double shard timer.");
            return;
        }

        sender.sendMessage(ChatColor.RED + "Shard timer is not active.");
    }

}
