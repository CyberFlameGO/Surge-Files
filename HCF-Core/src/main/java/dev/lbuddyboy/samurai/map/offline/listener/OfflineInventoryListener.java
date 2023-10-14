package dev.lbuddyboy.samurai.map.offline.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.offline.OfflineInventory;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class OfflineInventoryListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (Feature.OFFLINE_INVS.isDisabled()) {
            return;
        }

        if (Samurai.getInstance().getArenaHandler().isDeathbanned(event.getPlayer().getUniqueId())) return;

        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            Samurai.getInstance().getOfflineHandler().playerJoin(event.getPlayer());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (Feature.OFFLINE_INVS.isDisabled()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.hasMetadata("gaming")) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        if (Samurai.getInstance().getInDuelPredicate().test(player)) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        if (Samurai.getInstance().getArenaHandler().isDeathbanned(event.getPlayer().getUniqueId())) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        Samurai.getInstance().getOfflineHandler().playerQuit(player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {

        if (Feature.OFFLINE_INVS.isDisabled()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.hasMetadata("gaming")) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        if (Samurai.getInstance().getInDuelPredicate().test(player)) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        if (Samurai.getInstance().getArenaHandler().isDeathbanned(event.getPlayer().getUniqueId())) {
            Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
            return;
        }

        Samurai.getInstance().getOfflineHandler().playerQuit(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!Samurai.getInstance().getOfflineHandler().getEditMap().containsKey(player.getUniqueId())) return;

        Inventory inventory = Samurai.getInstance().getOfflineHandler().getEditMap().get(player.getUniqueId());

        if (event.getInventory() != inventory) return;
        if (Arrays.stream(Arrays.stream(OfflineInventory.GLASS_SLOTS).toArray()).noneMatch(i -> i == event.getRawSlot())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!Samurai.getInstance().getOfflineHandler().getEditMap().containsKey(player.getUniqueId())) return;

        Inventory inventory = Samurai.getInstance().getOfflineHandler().getEditMap().get(player.getUniqueId());

        if (event.getInventory() != inventory) return;

        UUID uuid = UUIDUtils.uuid(event.getView().getTitle().replaceAll("'s Offline", ""));
        OfflineInventory offline = Samurai.getInstance().getOfflineHandler().getOfflineInventories().get(uuid);

        offline.save(inventory);
        Samurai.getInstance().getOfflineHandler().saveOffline(offline);
        Samurai.getInstance().getOfflineHandler().getOfflineInventories().put(player.getUniqueId(), offline);
        player.sendMessage(CC.translate("&aSuccessfully saved " + UUIDUtils.name(uuid) + "'s offline inventory!"));
        Samurai.getInstance().getOfflineHandler().getEditMap().remove(player.getUniqueId());
    }

}
