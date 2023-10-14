package dev.lbuddyboy.pcore.essential.warp.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.pcore.essential.warp.Warp;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WarpCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();

        if (input.isEmpty()) {
            completions.addAll(pCore.getInstance().getWarpHandler().getWarps().values().stream().map(Warp::getName).collect(Collectors.toList()));
        } else {
            for (String s : pCore.getInstance().getWarpHandler().getWarps().values().stream().map(Warp::getName).collect(Collectors.toList())) {
                if (s.startsWith(input)) {
                    completions.add(s);
                }
            }
        }

        return completions;
    }

}
