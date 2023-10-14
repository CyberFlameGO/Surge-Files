package dev.lbuddyboy.pcore.essential.vaults.listener;

import dev.lbuddyboy.pcore.essential.vaults.PlayerVaultConstants;
import dev.lbuddyboy.pcore.essential.vaults.VaultInfo;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class PlayerVaultListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (player.hasMetadata(PlayerVaultConstants.VAULT_METADATA)) {
            VaultInfo info = (VaultInfo) player.getMetadata(PlayerVaultConstants.VAULT_METADATA).get(0).value();
            pCore.getInstance().getPlayerVaultHandler().updateVault(info.getTarget(), info.getVaultNumber(), inventory.getContents());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (player.hasMetadata(PlayerVaultConstants.VAULT_METADATA)) {
            VaultInfo info = (VaultInfo) player.getMetadata(PlayerVaultConstants.VAULT_METADATA).get(0).value();
            pCore.getInstance().getPlayerVaultHandler().updateVault(info.getTarget(), info.getVaultNumber(), inventory.getContents());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (player.hasMetadata(PlayerVaultConstants.VAULT_METADATA)) {
            VaultInfo info = (VaultInfo) player.getMetadata(PlayerVaultConstants.VAULT_METADATA).get(0).value();
            pCore.getInstance().getPlayerVaultHandler().updateVault(info.getTarget(), info.getVaultNumber(), inventory.getContents());
            player.removeMetadata(PlayerVaultConstants.VAULT_METADATA, pCore.getInstance());
        }
    }

}
