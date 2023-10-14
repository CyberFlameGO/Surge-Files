package dev.aurapvp.samurai.faction.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FactionContext implements ContextResolver<Faction, BukkitCommandExecutionContext> {

    @Override
    public Faction getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(sender.getUniqueId());

            if (faction == null) {
                throw new InvalidCommandArgument(FactionConfiguration.FACTION_NOT_CREATED.getString());
            }

            return faction;
        }

        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByName(source);

        if (faction != null) {
            return faction;
        }

        faction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(Bukkit.getOfflinePlayer(source).getUniqueId());

        if (faction == null) {
            throw new InvalidCommandArgument(FactionConfiguration.FACTION_NOT_FOUND.getString("%faction-name%", source));
        }

        return faction;
    }
}
