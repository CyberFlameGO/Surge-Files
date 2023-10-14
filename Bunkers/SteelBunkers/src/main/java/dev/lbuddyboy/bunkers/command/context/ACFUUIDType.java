package dev.lbuddyboy.bunkers.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ACFUUIDType implements ContextResolver<UUID, BukkitCommandExecutionContext> {

    @Override
    public UUID getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.popFirstArg();

        if (sender != null && arg.isOptional() && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return sender.getUniqueId();
        }

        try {
            return Bukkit.getOfflinePlayer(source.toLowerCase()).getUniqueId();
        } catch (Exception ignored) {

        }

        throw new InvalidCommandArgument("No player with the name " + source + " found.");
    }
}