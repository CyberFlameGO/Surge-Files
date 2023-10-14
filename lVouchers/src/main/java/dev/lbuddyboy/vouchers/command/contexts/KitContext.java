package dev.lbuddyboy.vouchers.command.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.vouchers.lVouchers;
import dev.lbuddyboy.vouchers.object.Voucher;
import dev.lbuddyboy.vouchers.util.CC;
import org.bukkit.entity.Player;

public class KitContext implements ContextResolver<Voucher, BukkitCommandExecutionContext> {

    @Override
    public Voucher getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        Player sender = c.getPlayer();
        String source = c.popFirstArg();

        if (lVouchers.getInstance().getVoucher().containsKey(source)) {
            return lVouchers.getInstance().getVoucher().get(source);
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid kit name."));
    }
}
