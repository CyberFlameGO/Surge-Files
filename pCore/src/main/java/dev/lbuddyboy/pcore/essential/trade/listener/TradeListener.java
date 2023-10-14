package dev.lbuddyboy.pcore.essential.trade.listener;

import dev.lbuddyboy.pcore.essential.trade.Trade;
import dev.lbuddyboy.pcore.essential.trade.inventory.TradeInventory;
import dev.lbuddyboy.pcore.pCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class TradeListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase("Trade Menu")) return;
        Player player = (Player) event.getWhoClicked();
        Trade trade = pCore.getInstance().getTradeHandler().findTrade(player.getUniqueId());
        ItemStack clicked = event.getCurrentItem();

        event.setCancelled(true);

        if (trade == null) return;
        if (clicked == null) return;
        if (trade.getTask() != null) {
            if (event.getRawSlot() == TradeInventory.ACCEPT_SLOTS) {
                if (clicked.getType() == Material.REDSTONE) {
                    trade.cancel();
                } else {
                    trade.accept(player.getUniqueId());
                }
            }
            return;
        }

        if (event.getRawSlot() == TradeInventory.ACCEPT_SLOTS) {
            if (clicked.getType() == Material.REDSTONE) {
                trade.cancel();
            } else {
                trade.accept(player.getUniqueId());
            }
        }

        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getType() == Material.AIR) return;

            trade.addItem(player, clicked);
            TradeInventory.updateInventory(trade);
            event.setCurrentItem(null);
        } else {
            for (int slot : TradeInventory.SENDER_SLOTS) {
                if (event.getRawSlot() == slot) {
                    trade.removeItem(player, event.getCurrentItem());
                    TradeInventory.updateInventory(trade);
                }
            }
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase("Trade Menu")) return;
        Player player = (Player) event.getPlayer();
        Trade trade = pCore.getInstance().getTradeHandler().findTrade(player.getUniqueId());

        if (trade == null) return;

        if (trade.isOver()) {
            return;
        }

        if (trade.getAccepted().size() < 2) {
            trade.refund();
            return;
        }

        player.openInventory(trade.getInventories().get(player.getUniqueId()));
    }

}
