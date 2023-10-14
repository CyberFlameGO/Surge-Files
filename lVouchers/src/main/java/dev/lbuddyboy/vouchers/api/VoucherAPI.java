package dev.lbuddyboy.vouchers.api;

import dev.lbuddyboy.vouchers.API;
import dev.lbuddyboy.vouchers.object.Voucher;
import org.bukkit.entity.Player;

public class VoucherAPI extends API {

    @Override
    public boolean attemptUse(Player player, Voucher voucher) {
        return false;
    }

}
