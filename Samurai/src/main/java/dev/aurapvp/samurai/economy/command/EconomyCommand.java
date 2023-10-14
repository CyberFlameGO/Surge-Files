package dev.aurapvp.samurai.economy.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.BankAccount;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.economy.transaction.Transaction;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("economy|eco|bal|balance|money")
public class EconomyCommand extends BaseCommand {

    private final IEconomy economy;

    public EconomyCommand() {
        this.economy = Samurai.getInstance().getEconomyHandler().getEconomy();
    }

    @Default
    public void economy(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        BankAccount account = this.economy.getBankAccount(player.getUniqueId(), true);

        player.sendMessage(CC.translate("&aBank Account&7: &f$" + this.economy.formatMoney(account.getMoney())));
    }

    @Subcommand("transactions")
    @CommandPermission("samurai.command.economy.transactions")
    public void transactions(Player sender) {
        BankAccount account = this.economy.getBankAccount(sender.getUniqueId(), true);
        List<Transaction> transactions = account.getTransactions();

        sender.sendMessage(CC.translate("&a&lTransactions &7(" + transactions.size() + ")"));
        for (Transaction transaction : transactions) {
            sender.sendMessage(CC.WHITE + transaction.getId() + ": " + transaction.getEconomy() + " (" + transaction.getTransaction() + ")");
        }
    }

    @Subcommand("add")
    @CommandPermission("samurai.command.economy.add")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.addMoney(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added $" + this.economy.formatMoney(amount) + " to " + target.getName() + "'s bank account!"));
    }

    @Subcommand("remove")
    @CommandPermission("samurai.command.economy.remove")
    @CommandCompletion("@players")
    public void remove(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.removeMoney(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have removed $" + this.economy.formatMoney(amount) + " from " + target.getName() + "'s bank account!"));
    }

    @Subcommand("set")
    @CommandPermission("samurai.command.economy.set")
    @CommandCompletion("@players")
    public void set(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.setMoney(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added $" + this.economy.formatMoney(amount) + " to " + target.getName() + "'s bank account!"));
    }

}
