package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.UUID;

@CommandAlias("pay|p2p")
public class PayCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void pay(Player sender, @Name("player") UUID player, @Name("amount") float amount) {
        double balance = FrozenEconomyHandler.getBalance(sender.getUniqueId());
        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (sender.equals(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        if (amount < 5) {
            sender.sendMessage(ChatColor.RED + "You must send at least $5!");
            return;
        }

        if (balance > 100000) {
            sender.sendMessage("§cYour balance is too high to send money. Please contact an admin to transfer money.");
            Bukkit.getLogger().severe("[ECONOMY] " + sender.getName() + " tried to send " + amount);
            return;
        }

        if (Double.isNaN(balance)) {
            sender.sendMessage("§cYou can't send money because there was an error with your balance.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (balance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + amount + "!");
            return;
        }

        FrozenEconomyHandler.deposit(player, amount);
        FrozenEconomyHandler.withdraw(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.WHITE + "You sent " + ChatColor.GOLD + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.WHITE + " to " + ChatColor.GOLD + UUIDUtils.name(player) + ChatColor.WHITE + ".");
        bukkitPlayer.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.WHITE + " sent you " + ChatColor.GOLD + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.WHITE + ".");
    }

}