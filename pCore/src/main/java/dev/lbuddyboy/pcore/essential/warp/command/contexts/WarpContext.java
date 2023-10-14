package dev.lbuddyboy.pcore.essential.warp.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.pcore.essential.warp.Warp;
import dev.lbuddyboy.pcore.essential.warp.menu.WarpMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.entity.Player;

public class WarpContext implements ContextResolver<Warp, BukkitCommandExecutionContext> {

    @Override
    public Warp getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (c.getIndex() == 1) {
            if (source.isEmpty() || source.equalsIgnoreCase("null")) {
                return null;
            }
        }

        for (Warp warp : pCore.getInstance().getWarpHandler().getWarps().values()) {
            if (warp.getName().equalsIgnoreCase(source)) {
                return warp;
            }
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid warp name."));
    }
}
