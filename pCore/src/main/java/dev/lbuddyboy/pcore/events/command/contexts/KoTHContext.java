package dev.lbuddyboy.pcore.events.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.events.koth.KoTH;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.entity.Player;

public class KoTHContext implements ContextResolver<KoTH, BukkitCommandExecutionContext> {

    @Override
    public KoTH getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (pCore.getInstance().getEventHandler().getEvents().containsKey(source) && pCore.getInstance().getEventHandler().getEvents().get(source) instanceof KoTH) {
            return (KoTH) pCore.getInstance().getEventHandler().getEvents().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid koth."));
    }
}
