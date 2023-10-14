package dev.lbuddyboy.samurai.listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("checkdomains")
@CommandPermission("op")
public class DomainListener extends BaseCommand implements Listener {

    public static Map<UUID, String> hostNamesUUID = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void onLogin(PlayerLoginEvent event) {
        hostNamesUUID.put(event.getPlayer().getUniqueId(), event.getHostname());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        hostNamesUUID.remove(event.getPlayer().getUniqueId());
    }

    @Default
    public static void checkDomains(Player player) {
        final Map<String, Integer> hostNamesAmount = new HashMap<>();
        hostNamesUUID.forEach((key, value) -> hostNamesAmount.put(value, hostNamesAmount.getOrDefault(value, 0) + 1));

        hostNamesAmount.forEach((key, value) -> player.sendMessage(CC.translate("&6" + key.replace(":25565", "") + ": &f" + value)));
    }
}