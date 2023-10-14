package dev.aurapvp.samurai.enchants.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.Samurai;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArmorSetCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(Samurai.getInstance().getArmorSetHandler().getArmorSets().keySet());
        } else {
            for (String s : Samurai.getInstance().getArmorSetHandler().getArmorSets().keySet()) {
                if (s.startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
