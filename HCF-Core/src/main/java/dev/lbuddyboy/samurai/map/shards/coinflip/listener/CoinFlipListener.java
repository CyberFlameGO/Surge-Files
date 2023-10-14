package dev.lbuddyboy.samurai.map.shards.coinflip.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlip;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CoinFlipListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CoinFlip cf = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().get(player.getUniqueId());

        if (cf == null) return;

        cf.refund();
    }

}
