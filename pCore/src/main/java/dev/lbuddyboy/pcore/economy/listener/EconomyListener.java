package dev.lbuddyboy.pcore.economy.listener;

import dev.lbuddyboy.pcore.pCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class EconomyListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if (pCore.getInstance().getEconomyHandler().getEconomy().hasBankAccount(uuid)) return;

        pCore.getInstance().getEconomyHandler().getEconomy().createBankAccount(uuid, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pCore.getInstance().getEconomyHandler().getEconomy().saveBankAccount(event.getPlayer().getUniqueId(), true);
    }

}
