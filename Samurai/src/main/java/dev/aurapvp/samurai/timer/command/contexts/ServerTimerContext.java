package dev.aurapvp.samurai.timer.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.timer.ServerTimer;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.entity.Player;

public class ServerTimerContext implements ContextResolver<ServerTimer, BukkitCommandExecutionContext> {

    @Override
    public ServerTimer getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (Samurai.getInstance().getTimerHandler().getServerTimers().containsKey(source)) {
            return Samurai.getInstance().getTimerHandler().getServerTimers().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid server timer."));
    }
}
