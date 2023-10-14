package dev.aurapvp.samurai.events.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.aurapvp.samurai.events.koth.KoTH;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.ServerTimer;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.entity.Player;

public class KoTHContext implements ContextResolver<KoTH, BukkitCommandExecutionContext> {

    @Override
    public KoTH getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (Samurai.getInstance().getEventHandler().getEvents().containsKey(source) && Samurai.getInstance().getEventHandler().getEvents().get(source) instanceof KoTH) {
            return (KoTH) Samurai.getInstance().getEventHandler().getEvents().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid koth."));
    }
}
