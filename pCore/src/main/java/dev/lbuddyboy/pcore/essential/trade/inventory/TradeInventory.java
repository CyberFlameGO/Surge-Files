package dev.lbuddyboy.pcore.essential.trade.inventory;

import dev.lbuddyboy.pcore.essential.trade.Trade;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeInventory {

    public static int ACCEPT_SLOTS = 45;
    public static int[] COUNTDOWN_SLOTS = new int[]{46, 53};
    public static int[] SENDER_SLOTS = new int[]{
            0, 1, 2, 3,
            9, 10, 11, 12,
            18, 19, 20, 21,
            27, 28, 29, 30,
            36, 37, 38, 39,
    };
    public static int[] OTHER_SLOTS = new int[]{
            5, 6, 7, 8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44,
    };
    public static int[] FILLER_SLOTS = new int[]{
            4,
            13,
            22,
            31,
            40,
            47, 48, 49, 50, 51, 52
    };

    public static void createInventory(Player player, Trade trade) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Trade Menu");
        UUID targetUUID = trade.getTarget() == player.getUniqueId() ? trade.getSender() : trade.getTarget();
        Player target = Bukkit.getPlayer(targetUUID);

        loadSlots(inventory, player, target, trade);

        player.openInventory(inventory);
        trade.getInventories().put(player.getUniqueId(), inventory);
    }

    public static void loadSlots(Inventory inventory, Player player, Player target, Trade trade) {
        if (trade.getAccepted().contains(player.getUniqueId())) {
            inventory.setItem(ACCEPT_SLOTS, new ItemBuilder(Material.REDSTONE).setName("&cClick to decline the trade!").create());
        } else {
            inventory.setItem(ACCEPT_SLOTS, new ItemBuilder(Material.EMERALD).setName("&aClick to accept the trade!").create());
        }
        inventory.setItem(COUNTDOWN_SLOTS[0], new ItemBuilder(Material.STAINED_GLASS_PANE, trade.getCountdown()).setDurability(trade.getAccepted().contains(player.getUniqueId()) ? 5 : 14).setName("&a" + trade.getCountdown() + "...").create());
        inventory.setItem(COUNTDOWN_SLOTS[1], new ItemBuilder(Material.STAINED_GLASS_PANE, trade.getCountdown()).setDurability(trade.getAccepted().contains(target.getUniqueId()) ? 5 : 14).setName("&a" + trade.getCountdown() + "...").create());

        for (int slot : FILLER_SLOTS) {
            inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").create());
        }

        for (int slot : SENDER_SLOTS) {
            inventory.setItem(slot, null);
        }

        for (int slot : OTHER_SLOTS) {
            inventory.setItem(slot, null);
        }

        List<ItemStack> senderItems = trade.getItems().getOrDefault(player.getUniqueId(), new ArrayList<>()), targetItems = trade.getItems().getOrDefault(target.getUniqueId(), new ArrayList<>());
        int i = 0;
        for (ItemStack item : senderItems) {
            inventory.setItem(SENDER_SLOTS[i++], item);
        }
        i = 0;
        for (ItemStack item : targetItems) {
            inventory.setItem(OTHER_SLOTS[i++], item);
        }

    }

    public static void updateInventory(Trade trade) {
        for (UUID uuid : trade.getInventories().keySet()) {
        Player player = Bukkit.getPlayer(uuid);
        UUID targetUUID = trade.getTarget() == player.getUniqueId() ? trade.getSender() : trade.getTarget();
        Player target = Bukkit.getPlayer(targetUUID);
        Inventory inventory = trade.getInventories().get(uuid);

        loadSlots(inventory, player, target, trade);
    }
}

}
