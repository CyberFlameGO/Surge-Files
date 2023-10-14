package dev.lbuddyboy.crates.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.model.CrateItem;
import dev.lbuddyboy.crates.util.CC;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CrateItemContext implements ContextResolver<CrateItem, BukkitCommandExecutionContext> {

    @Override
    public CrateItem getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();
        Crate crate = (Crate) c.getResolvedArg(Crate.class);

        for (CrateItem item : crate.getItems().values()) {
            if (Objects.equals(item.getId(), source)) return item;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid crate item from " + crate.getName()));
    }
}
