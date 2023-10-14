package dev.lbuddyboy.samurai.team;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.UUIDUtils;
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
            return UUIDUtils.uuid(source.toLowerCase());
        } catch (Exception ignored) {

        }

        throw new InvalidCommandArgument("No team or member with the name " + source + " found.");
    }
}