package dev.lbuddyboy.gkits.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.util.CC;
import org.bukkit.entity.Player;

public class KitContext implements ContextResolver<GKit, BukkitCommandExecutionContext> {

    @Override
    public GKit getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (lGKits.getInstance().getGKits().containsKey(source)) {
            return lGKits.getInstance().getGKits().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid kit name."));
    }
}
