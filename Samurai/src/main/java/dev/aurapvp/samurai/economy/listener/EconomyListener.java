package dev.aurapvp.samurai.economy.listener;

import dev.aurapvp.samurai.Samurai;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class EconomyListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if (Samurai.getInstance().getEconomyHandler().getEconomy().hasBankAccount(uuid)) return;

        Samurai.getInstance().getEconomyHandler().getEconomy().createBankAccount(uuid, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Samurai.getInstance().getEconomyHandler().getEconomy().saveBankAccount(event.getPlayer().getUniqueId(), true);
    }

}
