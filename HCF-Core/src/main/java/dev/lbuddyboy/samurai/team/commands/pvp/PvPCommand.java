package dev.lbuddyboy.samurai.team.commands.pvp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.flash.util.PagedItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("pvp|timer|pvptimer")
public class PvPCommand extends BaseCommand {

    @Subcommand("addlives")
    @Description("Adds lives to a player")
    @CommandPermission("foxtrot.pvp.addlives")
    @CommandCompletion("@players")
    public static void pvpSetLives(CommandSender sender, @Name("target") OfflinePlayer fakePlayer, @Name("amount") int amount) {
        UUID player = fakePlayer.getUniqueId();
        Samurai.getInstance().getFriendLivesMap().setLives(player, Samurai.getInstance().getFriendLivesMap().getLives(player) + amount);
        sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " lives.");

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            String suffix = sender instanceof Player ? " from " + sender.getName() : "";
            bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
        }
    }

    @Subcommand("create")
    @Description("Creates a new PvP timer")
    @CommandPermission("foxtrot.pvp.create")
    @CommandCompletion("@players")
    public static void pvpCreate(Player sender, @Optional UUID player) {//TODO Smth for optional here there was a self after optional
        if (player == null) player = sender.getUniqueId();
        Samurai.getInstance().getPvPTimerMap().createTimer(player, (int) TimeUnit.MINUTES.toSeconds(30));
        sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + UUIDUtils.name(player) + ".");
    }
    @Subcommand("enable|remove")
    @Description("Enables or disables a PvP timer")
    public static void pvpEnable(Player sender, @Optional Player target) {//TODO: Might need to fix this got no clue tbh
        if (target != sender && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
            Samurai.getInstance().getPvPTimerMap().removeTimer(target.getUniqueId());

            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Your PvP Timer has been removed!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + "'s PvP Timer has been removed!");
            }
        } else {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "You do not have a PvP Timer!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " does not have a PvP Timer.");
            }
        }
    }

    @Subcommand("lives")
    @Description("Shows the amount of lives you have")
    public static void pvpLives(Player sender) {
        UUID player = sender.getUniqueId();
        String name = UUIDUtils.name(player);

        sender.sendMessage(ChatColor.GOLD + name + "'s Lives: " + ChatColor.WHITE + Samurai.getInstance().getFriendLivesMap().getLives(player));
    }

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        int page = help.getPage();
        List<String> header = CC.translate(Arrays.asList(
                CC.CHAT_BAR,
                "&6&lPvP Command Help"
        ));
        List<String> entries = new ArrayList<>();

        for (HelpEntry entry : help.getHelpEntries()) {
            entries.add("&e/" + entry.getCommand() + " " + entry.getParameterSyntax() + " &7- " + entry.getDescription());
        }

        PagedItem pagedItem = new PagedItem(entries, header, 10);

        pagedItem.send(help.getIssuer().getIssuer(), page);

        help.getIssuer().sendMessage(" ");
        help.getIssuer().sendMessage("&7You can do /pvp help <page> - You're currently viewing on page #" + page + ".");
        help.getIssuer().sendMessage(CC.CHAT_BAR);
    }

    @Subcommand("revive")
    @Description("Revives a player!")
    public static void pvpRevive(Player sender, @Name("player") UUID player) {
        if (player == null) player = sender.getUniqueId();

        int friendLives = Samurai.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if (Samurai.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives <= 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!Samurai.getInstance().getDeathbanMap().isDeathbanned(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        // Use a friend life.
        Samurai.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " with a life!");

        if (Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " has used a life to revive you!");
        }

        Samurai.getInstance().getDeathbanMap().revive(player);
    }

    @Subcommand("setlives")
    @Description("Sets a player's lives")
    @CommandPermission("foxtrot.admin")
    public static void pvpSetLives(Player sender, @Name("player") UUID player, int amount) {
        Samurai.getInstance().getFriendLivesMap().setLives(player, amount);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s life count to " + amount + ".");

    }

    @Subcommand("time")
    @Description("Show the time left on the PvP Timer")
    public static void pvpTime(Player sender) {
        if (Samurai.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.formatIntoMMSS(Samurai.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId())) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}