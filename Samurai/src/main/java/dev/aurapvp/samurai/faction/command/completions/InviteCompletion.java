package dev.aurapvp.samurai.faction.command.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InviteCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();
        Player player = context.getPlayer();
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (faction != null)
            completions.addAll(faction.getInvitations().stream().map(i -> Bukkit.getOfflinePlayer(i.getTarget()).getName()).collect(Collectors.toList()));

        return completions;
    }

}
