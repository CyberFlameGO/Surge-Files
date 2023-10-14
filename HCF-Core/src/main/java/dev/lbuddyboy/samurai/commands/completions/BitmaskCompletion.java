package dev.lbuddyboy.samurai.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BitmaskCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(Arrays.stream(DTRBitmask.values()).map(Enum::name).toList());
        } else {
            for (String s : Arrays.stream(DTRBitmask.values()).map(Enum::name).toList()) {
                if (s.startsWith(input.toUpperCase())) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
