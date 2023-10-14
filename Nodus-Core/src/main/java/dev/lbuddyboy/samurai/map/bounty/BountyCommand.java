package dev.lbuddyboy.samurai.map.bounty;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

@CommandAlias("bounty")
public class BountyCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void def(Player sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
        bounty(sender, target, amount);
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    public static void bounty(Player sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        if (Samurai.getInstance().getBountyCooldownMap().isOnCooldown(target.getPlayer().getUniqueId())) {
            long millisLeft = Samurai.getInstance().getBountyCooldownMap().getCooldown(target.getPlayer().getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "Bounty cooldown: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
            return;
        }

        if (amount < 5) {
            sender.sendMessage(CC.RED + "Your bounty must be at least 5 shards!");
            return;
        }

        if (sender == target.getPlayer()) {
            sender.sendMessage(CC.RED + "You cannot put a bounty on yourself!");
            return;
        }

        Bounty bounty = Samurai.getInstance().getMapHandler().getBountyManager().getBounty(target.getPlayer());

        if (bounty != null && bounty.getShards() >= amount) {
            sender.sendMessage(CC.RED + "Your bounty must be higher than the current bounty of " + bounty.getShards() + " shards!");
            return;
        }

        if (!Samurai.getInstance().getShardMap().removeShards(sender.getUniqueId(), amount)) {
            sender.sendMessage(CC.RED + "You do not have enough shards for this!");
            return;
        }

        if (bounty != null) {
            Samurai.getInstance().getShardMap().addShards(bounty.getPlacedBy(), bounty.getShards(), true);
        }

        Samurai.getInstance().getMapHandler().getBountyManager().placeBounty(sender, target.getPlayer(), amount);

        Bukkit.broadcastMessage(CC.GRAY + "[" + CC.GOLD + "Bounty" + CC.GRAY + "] " + sender.getDisplayName() + CC.YELLOW + " placed a bounty on "
                + target.getPlayer().getDisplayName() + CC.YELLOW + " of " + CC.WHITE + amount + " shards" + CC.YELLOW + "!");

        FrozenNametagHandler.reloadPlayer(target.getPlayer());

        Samurai.getInstance().getBountyCooldownMap().applyCooldown(target.getPlayer().getUniqueId(), 30);
    }

    @Subcommand("list")
    public static void bountyList(Player sender) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        Samurai.getInstance().getMapHandler().getBountyManager().getBountyMap()
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach((entry) ->
                        sender.sendMessage(ChatColor.GOLD + FrozenUUIDCache.name(entry.getKey()) + ": "
                                + ChatColor.GREEN + entry.getValue().getShards() + " shards"));
    }
}
