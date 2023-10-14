package dev.lbuddyboy.pcore.timer.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ServerTimerCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(pCore.getInstance().getTimerHandler().getServerTimers().values().stream().map(ServerTimer::getName).collect(Collectors.toList()));
        } else {
            for (String s : pCore.getInstance().getTimerHandler().getServerTimers().values().stream().map(ServerTimer::getName).collect(Collectors.toList())) {
                if (s.startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
