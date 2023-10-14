package dev.lbuddyboy.pcore.essential.offline.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.user.MineUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OfflineListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OfflineData cache = pCore.getInstance().getOfflineHandler().fetchCache(player.getUniqueId());

        if (cache != null && !cache.isEdited()) {
            MineUser samuraiPlayer = pCore.getInstance().getMineUserHandler().tryMineUserAsync(player.getUniqueId());
            samuraiPlayer.setOfflineData(null);
            samuraiPlayer.flagUpdate();
            return;
        }

        if (cache == null) return;

        player.getInventory().setArmorContents(cache.getArmor());
        player.getInventory().setContents(cache.getContents());
        player.teleport(cache.getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        pCore.getInstance().getOfflineHandler().updateCache(player.getUniqueId(), new OfflineData(player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getLocation(), false));
    }

}
