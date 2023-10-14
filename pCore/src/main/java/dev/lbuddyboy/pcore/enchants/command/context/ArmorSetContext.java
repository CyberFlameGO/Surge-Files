package dev.lbuddyboy.pcore.enchants.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.enchants.set.ArmorSet;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.PriceAmount;
import org.bukkit.entity.Player;

public class ArmorSetContext implements ContextResolver<ArmorSet, BukkitCommandExecutionContext> {

    @Override
    public ArmorSet getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();
        ArmorSet set = pCore.getInstance().getEnchantHandler().getArmorSets().getOrDefault(source.toLowerCase(), null);

        if (set == null) {
            throw new InvalidCommandArgument(CC.translate("&cInvalid armor set provided."));
        }

        return set;
    }
}
