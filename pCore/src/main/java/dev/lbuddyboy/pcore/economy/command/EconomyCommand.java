package dev.lbuddyboy.pcore.economy.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.economy.transaction.Transaction;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.PriceAmount;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("economy|eco|bal|balance|money")
public class EconomyCommand extends BaseCommand {

    private final IEconomy economy;

    public EconomyCommand() {
        this.economy = pCore.getInstance().getEconomyHandler().getEconomy();
    }

    @Default
    public void economy(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        BankAccount account = this.economy.getBankAccount(player.getUniqueId(), true);

        player.sendMessage(CC.translate("&aBank Account&7: &f$" + this.economy.formatMoney(account.getMoney())));
        player.sendMessage(CC.translate("&eToken Account&7: &f$" + this.economy.formatMoney(EconomyType.COINS.getAmount(player.getUniqueId()))));
    }

    @Subcommand("transactions")
    @CommandPermission("pcore.command.economy.transactions")
    public void transactions(Player sender) {
        BankAccount account = this.economy.getBankAccount(sender.getUniqueId(), true);
        List<Transaction> transactions = account.getTransactions();

        sender.sendMessage(CC.translate("&a&lTransactions &7(" + transactions.size() + ")"));
        for (Transaction transaction : transactions) {
            sender.sendMessage(CC.WHITE + transaction.getId() + ": " + transaction.getEconomy() + " (" + transaction.getTransaction() + ")");
        }
    }

    @Subcommand("add")
    @CommandPermission("pcore.command.economy.add")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") PriceAmount amount) {
        this.economy.addMoney(target.getUniqueId(), amount.getAmount(), IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added $" + this.economy.formatMoney(amount.getAmount()) + " to " + target.getName() + "'s bank account!"));
    }

    @Subcommand("remove")
    @CommandPermission("pcore.command.economy.remove")
    @CommandCompletion("@players")
    public void remove(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") PriceAmount amount) {
        this.economy.removeMoney(target.getUniqueId(), amount.getAmount(), IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have removed $" + this.economy.formatMoney(amount.getAmount()) + " from " + target.getName() + "'s bank account!"));
    }

    @Subcommand("set")
    @CommandPermission("pcore.command.economy.set")
    @CommandCompletion("@players")
    public void set(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") PriceAmount amount) {
        this.economy.setMoney(target.getUniqueId(), amount.getAmount(), IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added $" + this.economy.formatMoney(amount.getAmount()) + " to " + target.getName() + "'s bank account!"));
    }

}
