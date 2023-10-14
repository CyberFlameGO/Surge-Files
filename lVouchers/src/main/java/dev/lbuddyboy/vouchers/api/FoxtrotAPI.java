package dev.lbuddyboy.vouchers.api;

import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.vouchers.API;
import dev.lbuddyboy.vouchers.object.Voucher;
import dev.lbuddyboy.vouchers.util.CC;
import org.bukkit.entity.Player;

public class FoxtrotAPI extends API {

    @Override
    public boolean attemptUse(Player player, Voucher voucher) {
        if (!SpawnTagHandler.isTagged(player)) return true;

        player.sendMessage(CC.translate("&cYou can't use vouchers in combat."));
        return false;
    }

}
