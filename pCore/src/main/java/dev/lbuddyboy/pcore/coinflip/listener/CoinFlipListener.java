package dev.lbuddyboy.pcore.coinflip.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.CoinFlip;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CoinFlipListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CoinFlip cf = pCore.getInstance().getCoinFlipHandler().getCoinFlips().get(player.getUniqueId());

        if (cf == null) return;

        cf.refund();
    }

}
