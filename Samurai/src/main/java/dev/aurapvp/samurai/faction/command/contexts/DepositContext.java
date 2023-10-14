package dev.aurapvp.samurai.faction.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DepositContext implements ContextResolver<DepositContext.Deposit, BukkitCommandExecutionContext> {

    @Override
    public DepositContext.Deposit getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        double amount = 0;
        try {
            amount = Double.parseDouble(source);
        } catch (NumberFormatException ignored) {
            if (c.hasFlag("faction") && source.equalsIgnoreCase("all")) {
                Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

                if (faction == null) {
                    throw new InvalidCommandArgument(FactionConfiguration.FACTION_NOT_CREATED.getString());
                }

                return new Deposit(faction.getBalance());
            } else {
                if (source.equalsIgnoreCase("all")) {
                    return new Deposit(EconomyType.MONEY.getAmount(sender.getUniqueId()));
                }
            }
            throw new InvalidCommandArgument(CC.RED + "You need to provide a valid amount.");
        }

        return new Deposit(amount);
    }

    @AllArgsConstructor
    @Getter
    public class Deposit {

        private double amount;

    }

}
