package dev.aurapvp.samurai.faction.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.FactionType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FactionTypeCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(Arrays.stream(FactionType.values()).map(Enum::name).collect(Collectors.toList()));
        } else {
            for (String s : Arrays.stream(FactionType.values()).map(Enum::name).collect(Collectors.toList())) {
                if (s.startsWith(input.toUpperCase())) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
