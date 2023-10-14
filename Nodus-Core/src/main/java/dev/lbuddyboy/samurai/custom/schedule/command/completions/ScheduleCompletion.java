package dev.lbuddyboy.samurai.custom.schedule.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScheduleCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(Samurai.getInstance().getScheduleHandler().getScheduledTimes().keySet());
        } else {
            for (String s : Samurai.getInstance().getScheduleHandler().getScheduledTimes().keySet()) {
                if (s.startsWith(input.toUpperCase())) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
