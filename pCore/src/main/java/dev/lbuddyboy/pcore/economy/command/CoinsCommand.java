package dev.lbuddyboy.pcore.economy.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("coins|coin|mobcoin|mobcoins")
public class CoinsCommand extends BaseCommand {

    private final IEconomy economy;

    public CoinsCommand() {
        this.economy = pCore.getInstance().getEconomyHandler().getEconomy();
    }

    @Default
    public void economy(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        BankAccount account = this.economy.getBankAccount(player.getUniqueId(), true);

        player.sendMessage(CC.translate("&eCoins&7: &f⛁" + this.economy.formatMoney(account.getCoins())));
    }

    @Subcommand("add")
    @CommandPermission("pcore.command.mobcoins.add")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.addCoins(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added ⛁" + this.economy.formatMoney(amount) + " to " + target.getName() + "'s bank account!"));
    }

    @Subcommand("remove")
    @CommandPermission("pcore.command.mobcoins.remove")
    @CommandCompletion("@players")
    public void remove(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.removeCoins(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have removed ⛁" + this.economy.formatMoney(amount) + " from " + target.getName() + "'s bank account!"));
    }

    @Subcommand("set")
    @CommandPermission("pcore.command.mobcoins.set")
    @CommandCompletion("@players")
    public void set(CommandSender sender, @Name("user") OfflinePlayer target, @Name("amount") double amount) {
        this.economy.setCoins(target.getUniqueId(), amount, IEconomy.EconomyChange.builder()
                .forced(true)
                .build());

        sender.sendMessage(CC.translate("&aYou have added ⛁" + this.economy.formatMoney(amount) + " to " + target.getName() + "'s bank account!"));
    }

}
