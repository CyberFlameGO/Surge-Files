package dev.aurapvp.samurai.faction.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FactionMemberCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        String input = context.getInput();
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (faction == null) return Collections.emptyList();

        for (String s : faction.getMembers().stream().map(FactionPermission.FactionMember::getName).collect(Collectors.toList())) {
            if (s.startsWith(input.toUpperCase())) {
                completions.add(s);
            }
        }

        return completions;
    }

}
