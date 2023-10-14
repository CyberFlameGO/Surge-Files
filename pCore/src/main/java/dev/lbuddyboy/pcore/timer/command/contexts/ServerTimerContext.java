package dev.lbuddyboy.pcore.timer.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.entity.Player;

public class ServerTimerContext implements ContextResolver<ServerTimer, BukkitCommandExecutionContext> {

    @Override
    public ServerTimer getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (pCore.getInstance().getTimerHandler().getServerTimers().containsKey(source)) {
            return pCore.getInstance().getTimerHandler().getServerTimers().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid server timer."));
    }
}
