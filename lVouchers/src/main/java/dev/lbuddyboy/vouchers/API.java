package dev.lbuddyboy.vouchers;

import dev.lbuddyboy.vouchers.object.Voucher;
import org.bukkit.entity.Player;

public abstract class API {

    public abstract boolean attemptUse(Player player, Voucher voucher);

}
