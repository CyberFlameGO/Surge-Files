package dev.lbuddyboy.pcore.user.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import lombok.SneakyThrows;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MineUserListener implements Listener {

    @SneakyThrows
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!pCore.isENABLED()) {
            event.setKickMessage(CC.translate("&cThe server is still starting up."));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(event.getUniqueId(), true);

        if (user == null) user = new MineUser(event.getUniqueId());
        user.setName(event.getName());

        pCore.getInstance().getMineUserHandler().getUsers().put(event.getUniqueId(), user);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(event.getPlayer().getUniqueId());

        if (user == null) {
            player.sendMessage(CC.translate("&cFailed to load your profile. Please re-log."));
            player.kickPlayer(CC.translate("&cFailed to load your profile. Please re-log."));
            return;
        }

        user.setOfflineTime(0);

    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (!pCore.isENABLED()) return;

        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(event.getPlayer().getUniqueId());

        user.flagOffline();

    }

}
