package dev.lbuddyboy.pcore.economy.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.PriceAmount;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("pay")
public class PayCommand extends BaseCommand {

    private final IEconomy economy;

    public PayCommand() {
        this.economy = pCore.getInstance().getEconomyHandler().getEconomy();
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("economyTypes", (s) -> Arrays.asList("tokens", "money"));
    }

    @Default
    @CommandCompletion("@players @economyTypes")
    public void send(Player sender, @Name("target") OfflinePlayer target, @Name("amount") PriceAmount amount) {
        boolean targetValid = this.economy.addMoney(target.getUniqueId(), amount.getAmount(),
                IEconomy.EconomyChange.builder()
                        .forced(false)
                        .build());
        boolean senderValid = this.economy.removeMoney(sender.getUniqueId(), amount.getAmount(),
                IEconomy.EconomyChange.builder()
                        .forced(false)
                        .build());

        if (!targetValid) {
            sender.sendMessage(CC.translate("&c" + target.getName() + " is not receiving money at the moment."));
            return;
        }

        char prefix = '$';

        if (!senderValid) {
            sender.sendMessage(CC.translate("&cYou do not have " + prefix + this.economy.formatMoney(amount.getAmount()) + "."));
            return;
        }

        sender.sendMessage(CC.translate("&aYou have just sent &f" + prefix + this.economy.formatMoney(amount.getAmount()) + " to " + target.getName() + "."));
        if (target.isOnline()) {
            target.getPlayer().sendMessage(CC.translate("&a" + sender.getName() + " has just sent you &f" + prefix + this.economy.formatMoney(amount.getAmount()) + "."));
        }
    }

}
