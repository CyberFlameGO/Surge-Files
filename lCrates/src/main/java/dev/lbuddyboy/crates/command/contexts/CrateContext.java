package dev.lbuddyboy.crates.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.util.CC;
import org.bukkit.entity.Player;

public class CrateContext implements ContextResolver<Crate, BukkitCommandExecutionContext> {

    @Override
    public Crate getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (lCrates.getInstance().getCrates().containsKey(source)) {
            return lCrates.getInstance().getCrates().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid crate."));
    }
}
