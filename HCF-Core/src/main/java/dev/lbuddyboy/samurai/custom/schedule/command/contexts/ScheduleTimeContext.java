package dev.lbuddyboy.samurai.custom.schedule.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.schedule.ScheduledTime;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

public class ScheduleTimeContext implements ContextResolver<ScheduledTime, BukkitCommandExecutionContext> {

    @Override
    public ScheduledTime getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        for (String s : Samurai.getInstance().getScheduleHandler().getScheduledTimes().keySet()) {
            if (s.equalsIgnoreCase(source)) return Samurai.getInstance().getScheduleHandler().getScheduledTimes().get(s);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid scheduled time."));
    }
}
