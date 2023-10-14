package dev.lbuddyboy.samurai.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

public class BitmaskContext implements ContextResolver<DTRBitmask, BukkitCommandExecutionContext> {

    @Override
    public DTRBitmask getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        for (DTRBitmask bitmask : DTRBitmask.values()) {
            if (bitmask.name().equalsIgnoreCase(source)) return bitmask;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid bitmask."));
    }
}
