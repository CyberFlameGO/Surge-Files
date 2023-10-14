package dev.lbuddyboy.samurai.util.loottable.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import org.bukkit.entity.Player;

public class LootTableContext implements ContextResolver<LootTable, BukkitCommandExecutionContext> {

    @Override
    public LootTable getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        for (LootTable lootTable : LootTableHandler.getLootTables()) {
            if (lootTable.getName().equalsIgnoreCase(source)) return lootTable;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid loottable."));
    }
}
