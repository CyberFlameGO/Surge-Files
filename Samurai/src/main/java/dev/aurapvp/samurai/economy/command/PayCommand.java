package dev.aurapvp.samurai.economy.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("pay")
public class PayCommand extends BaseCommand {

    private final IEconomy economy;

    public PayCommand() {
        this.economy = Samurai.getInstance().getEconomyHandler().getEconomy();
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("economyTypes", (s) -> Arrays.asList("coins", "money"));
    }

    @Default
    @CommandCompletion("@players @economyTypes")
    public void send(Player sender, @Name("target") OfflinePlayer target, @Name("amount") double amount, @Name("coins|money") String economyType) {
        boolean targetValid = this.economy.addMoney(target.getUniqueId(), amount,
                IEconomy.EconomyChange.builder()
                        .forced(false)
                        .build());
        boolean senderValid = economyType.equalsIgnoreCase("coins") ? this.economy.removeCoins(sender.getUniqueId(), amount,
                IEconomy.EconomyChange.builder()
                        .forced(false)
                        .build()) : this.economy.removeMoney(sender.getUniqueId(), amount,
                IEconomy.EconomyChange.builder()
                        .forced(false)
                        .build());

        if (!targetValid) {
            sender.sendMessage(CC.translate("&c" + target.getName() + " is not receiving money at the moment."));
            return;
        }

        char prefix = economyType.equalsIgnoreCase("mobcoins") ? '⛁' : '$';

        if (!senderValid) {
            sender.sendMessage(CC.translate("&cYou do not have " + prefix + this.economy.formatMoney(amount) + "."));
            return;
        }

        sender.sendMessage(CC.translate("&aYou have just sent &f" + prefix + this.economy.formatMoney(amount) + " to " + target.getName() + "."));
        if (target.isOnline()) {
            target.getPlayer().sendMessage(CC.translate("&a" + sender.getName() + " has just sent you &f" + prefix + this.economy.formatMoney(amount) + "."));
        }
    }

}
