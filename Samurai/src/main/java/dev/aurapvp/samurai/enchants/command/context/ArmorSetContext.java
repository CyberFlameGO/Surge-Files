package dev.aurapvp.samurai.enchants.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.aurapvp.samurai.enchants.set.ArmorSet;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.entity.Player;

public class ArmorSetContext implements ContextResolver<ArmorSet, BukkitCommandExecutionContext> {

    @Override
    public ArmorSet getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();
        ArmorSet set = Samurai.getInstance().getArmorSetHandler().getArmorSets().getOrDefault(source.toLowerCase(), null);

        if (set == null) {
            throw new InvalidCommandArgument(CC.translate("&cInvalid armor set provided."));
        }

        return set;
    }
}
