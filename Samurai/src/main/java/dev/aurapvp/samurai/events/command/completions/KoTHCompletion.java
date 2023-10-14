package dev.aurapvp.samurai.events.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.events.Event;
import dev.aurapvp.samurai.events.koth.KoTH;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.ServerTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KoTHCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        for (Event event : Samurai.getInstance().getEventHandler().getEvents().values()) {
            if (!(event instanceof KoTH)) continue;

            completions.add(event.getName());
        }

        return completions;
    }

}
