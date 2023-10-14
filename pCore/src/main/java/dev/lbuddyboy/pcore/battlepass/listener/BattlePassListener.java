package dev.lbuddyboy.pcore.battlepass.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.BattlePassHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BattlePassListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(PlayerJoinEvent event) {
        BattlePassHandler battlePassHandler = pCore.getInstance().getBattlePassHandler();

        battlePassHandler.loadBattlePass(event.getPlayer().getUniqueId(), true);
    }

}
