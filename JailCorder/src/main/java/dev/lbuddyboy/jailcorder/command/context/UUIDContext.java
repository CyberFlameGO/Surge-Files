package dev.lbuddyboy.jailcorder.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.jailcorder.JailCorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDContext implements ContextResolver<UUID, BukkitCommandExecutionContext> {

    @Override
    public UUID getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String value = context.popFirstArg();
        CommandSender sender = context.getSender();
        UUID uuid = JailCorder.getInstance().getMongoHandler().getCache().getOrDefault(value.toLowerCase(), null);

        if (uuid == null) throw new InvalidCommandArgument("No player with the name " + value + " could be found.");

        return uuid;
    }
}
