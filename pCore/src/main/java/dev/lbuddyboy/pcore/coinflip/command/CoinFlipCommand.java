package dev.lbuddyboy.pcore.coinflip.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.CoinFlip;
import dev.lbuddyboy.pcore.coinflip.menu.CoinFlipCreateMenu;
import dev.lbuddyboy.pcore.coinflip.menu.CoinFlipMenu;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.PriceAmount;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("coinflip|cf")
public class CoinFlipCommand extends BaseCommand {

    @Default
    public void cf(Player sender) {
        new CoinFlipMenu().openMenu(sender);
    }

    @Default
    @CommandCompletion("@economyTypes")
    public void cf(Player sender, @Name("tokens|money") String ecoType, @Name("amount") PriceAmount amount) {

        if (amount.getAmount() < 10000) {
            sender.sendMessage(CC.translate("&cThat amount is too low to bet."));
            return;
        }

        Cooldown cooldown = pCore.getInstance().getCoinFlipHandler().getCooldown();

        if (cooldown.onCooldown(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou are currently on coin flip cooldown for " + cooldown.getRemaining(sender) + "."));
            return;
        }

        if (Arrays.stream(EconomyType.values()).map(Enum::name).noneMatch(s -> s.equalsIgnoreCase(ecoType))) {
            sender.sendMessage(CC.translate("&cThat economy type does not exist."));
            return;
        }

        EconomyType type = EconomyType.valueOf(ecoType.toUpperCase());

        if (pCore.getInstance().getCoinFlipHandler().getCoinFlips().containsKey(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou already have an active coin flip."));
            return;
        }

        if (!type.hasAmount(sender.getUniqueId(), amount.getAmount())) {
            sender.sendMessage(CC.translate("&cYou do not have enough " + type.getName() + " to create a coin flip."));
            return;
        }

        new CoinFlipCreateMenu(type, amount.getAmount()).openMenu(sender);
    }

    @Subcommand("cancel")
    public void cancel(Player sender) {
        CoinFlip cf = pCore.getInstance().getCoinFlipHandler().getCoinFlips().getOrDefault(sender.getUniqueId(), null);

        if (cf == null) {
            return;
        }

        cf.refund();
    }

}
