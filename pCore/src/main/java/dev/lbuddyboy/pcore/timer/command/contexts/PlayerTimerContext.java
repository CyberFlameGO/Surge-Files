package dev.lbuddyboy.pcore.timer.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.entity.Player;

public class PlayerTimerContext implements ContextResolver<PlayerTimer, BukkitCommandExecutionContext> {

    @Override
    public PlayerTimer getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        for (PlayerTimer timer : pCore.getInstance().getTimerHandler().getPlayerTimers()) {
            if (timer.getName().equalsIgnoreCase(source)) {
                return timer;
            }
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid player timer."));
    }
}
