package dev.aurapvp.samurai.util.loottable.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.util.loottable.LootTable;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LootTableItemsCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();
        LootTable crate = context.getContextValue(LootTable.class);


        if (input.isEmpty()) {
            completions.addAll(crate.getItems().values().stream().map(LootTableItem::getId).toList());
        } else {
            for (String s : crate.getItems().values().stream().map(LootTableItem::getId).toList()) {
                if (String.valueOf(s).startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return new ArrayList<>(completions);
    }

}
