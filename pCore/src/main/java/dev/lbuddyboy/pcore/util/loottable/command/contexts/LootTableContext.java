package dev.lbuddyboy.pcore.util.loottable.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.loottable.LootTable;
import org.bukkit.entity.Player;

public class LootTableContext implements ContextResolver<LootTable, BukkitCommandExecutionContext> {

    @Override
    public LootTable getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        for (LootTable lootTable : pCore.getInstance().getLootTableHandler().getLootTables()) {
            if (lootTable.getName().equalsIgnoreCase(source)) return lootTable;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid loottable."));
    }
}
