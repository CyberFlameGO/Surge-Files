package dev.lbuddyboy.samurai.custom.slots.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.slots.SlotConstants;
import dev.lbuddyboy.samurai.custom.slots.SlotHandler;
import dev.lbuddyboy.samurai.custom.slots.task.SpinTask;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SlotListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        SlotHandler slotHandler = Samurai.getInstance().getSlotHandler();

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getClickedInventory() instanceof PlayerInventory clicked)) return;

        if (clicked != event.getWhoClicked().getInventory()) return;
        if (!player.getOpenInventory().getTitle().equals("Ticket Master")) return;

        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        int current = (int) SlotConstants.countTickets(inventory);

        if (item == null) {
            event.setCancelled(true);
            return;
        }

        if (!item.isSimilar(slotHandler.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (current >= Samurai.getInstance().getSlotHandler().getRoll_slots().size()) {
            event.setCancelled(true);
            return;
        }

        if (!inventory.contains(SlotConstants.NONE)) {
            event.setCancelled(true);
            return;
        }

        if (slotHandler.isRolling(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        event.setCurrentItem(ItemUtils.takeItem(item));

        event.setCancelled(true);
        inventory.setItem(slotHandler.getRoll_slots().get(current) - 1, slotHandler.getItem());
    }

    @EventHandler
    public void onClickTop(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.getOpenInventory().getTitle().equals("Ticket Master")) return;
        if (event.getClickedInventory() == player.getInventory()) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        int current = (int) SlotConstants.countTickets(inventory);

        if (item == null) {
            event.setCancelled(true);
            return;
        }

        if (player.hasMetadata(SlotConstants.OPENING_META)) {
            event.setCancelled(true);
            return;
        }

        if (item.isSimilar(Samurai.getInstance().getSlotHandler().getItem())) {
            inventory.setItem(Samurai.getInstance().getSlotHandler().getRoll_slots().get((int) ((SlotConstants.countTickets(inventory) - 1)))- 1, SlotConstants.NONE);
            player.getInventory().addItem(Samurai.getInstance().getSlotHandler().getItem());
        } else if (item.isSimilar(SlotConstants.ROLL_BUTTON) && current >= 1) {
            SlotConstants.spin(inventory, player);
            ItemBuilder builder = ItemBuilder.of(Material.CLOCK);

            new SpinTask(player, builder, event).runTaskTimer(Samurai.getInstance(), 2, 2);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().hasMetadata(SlotConstants.OPENING_META)) event.getPlayer().removeMetadata(SlotConstants.OPENING_META, Samurai.getInstance());
        if (event.getPlayer().hasMetadata(SlotConstants.OPENED_META)) event.getPlayer().removeMetadata(SlotConstants.OPENED_META, Samurai.getInstance());

        if (!event.getPlayer().getOpenInventory().getTitle().equals("Ticket Master")) return;
        int tickets = (int) SlotConstants.countTickets(event.getPlayer().getOpenInventory().getTopInventory());
        if (tickets < 1) return;

        event.getPlayer().getInventory().addItem(ItemBuilder.copyOf(Samurai.getInstance().getSlotHandler().getItem()).amount(tickets).build());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer().hasMetadata(SlotConstants.OPENING_META)) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> event.getPlayer().openInventory(event.getInventory()));
            return;
        }
        if (!(event.getPlayer() instanceof Player)) return;

        if (event.getPlayer().hasMetadata(SlotConstants.OPENED_META))
            event.getPlayer().removeMetadata(SlotConstants.OPENED_META, Samurai.getInstance());

        if (!event.getPlayer().getOpenInventory().getTitle().equals("Ticket Master")) return;
        int tickets = (int) SlotConstants.countTickets(event.getInventory());
        if (tickets < 1) return;

        for (int i = 0; i < tickets; i++) {
            ItemUtils.tryFit((Player) event.getPlayer(), Samurai.getInstance().getSlotHandler().getItem().clone(), false);
        }
    }

}
