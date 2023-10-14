package dev.aurapvp.samurai.timer.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTimerCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(Samurai.getInstance().getTimerHandler().getPlayerTimers().stream().map(PlayerTimer::getName).collect(Collectors.toList()));
        } else {
            for (String s : Samurai.getInstance().getTimerHandler().getPlayerTimers().stream().map(PlayerTimer::getName).collect(Collectors.toList())) {
                if (s.startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
