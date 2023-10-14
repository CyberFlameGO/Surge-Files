package dev.aurapvp.samurai.battlepass.listener;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePassHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattlePassListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(PlayerJoinEvent event) {
        BattlePassHandler battlePassHandler = Samurai.getInstance().getBattlePassHandler();

        battlePassHandler.loadBattlePass(event.getPlayer().getUniqueId(), true);
    }

}
