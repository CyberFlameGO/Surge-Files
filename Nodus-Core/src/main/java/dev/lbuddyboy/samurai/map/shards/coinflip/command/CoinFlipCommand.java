package dev.lbuddyboy.samurai.map.shards.coinflip.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.map.shards.EconomyType;
import dev.lbuddyboy.samurai.map.shards.coinflip.menu.CoinFlipCreateMenu;
import dev.lbuddyboy.samurai.map.shards.coinflip.menu.CoinFlipMenu;
import dev.lbuddyboy.samurai.util.PriceAmount;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlip;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("coinflip|cf|sf|shardflip|moneyflip|mf")
public class CoinFlipCommand extends BaseCommand {

    @Default
    public void cf(Player sender) {
        new CoinFlipMenu().openMenu(sender);
    }

    @Default
    @CommandCompletion("@players @economyTypes")
    public void cf(Player sender, @Name("amount") PriceAmount amount, @Name("shards|money") @Optional String ecoType) {
        if (ecoType == null) ecoType = "money";
        int min = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getInt("minimum-bet-shards");

        if (!ecoType.equalsIgnoreCase("money")) min = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getInt("minimum-bet-money");

        if (amount.getAmount() < min) {
            sender.sendMessage(CC.translate("&cThat amount is too low to bet."));
            return;
        }

        Cooldown cooldown = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCooldown();

        if (cooldown.onCooldown(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou are currently on coin flip cooldown for " + cooldown.getRemaining(sender) + "."));
            return;
        }

        String finalEcoType = ecoType;
        if (Arrays.stream(EconomyType.values()).map(Enum::name).noneMatch(s -> s.equalsIgnoreCase(finalEcoType))) {
            sender.sendMessage(CC.translate("&cThat economy type does not exist."));
            return;
        }

        EconomyType type = EconomyType.valueOf(ecoType.toUpperCase());

        if (Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().containsKey(sender.getUniqueId())) {
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
        CoinFlip cf = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().getOrDefault(sender.getUniqueId(), null);

        if (cf == null) {
            return;
        }

        cf.refund();
    }

}
