package dev.lbuddyboy.samurai.server.commands;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.PriceAmount;
import org.bukkit.entity.Player;

public class PriceAmountContext implements ContextResolver<PriceAmount, BukkitCommandExecutionContext> {

    @Override
    public PriceAmount getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        PriceAmount amount = new PriceAmount(source);

        if (amount.getAmount() <= 0) {
            throw new InvalidCommandArgument(CC.translate("&cInvalid amount provided."));
        }

        return amount;
    }
}
