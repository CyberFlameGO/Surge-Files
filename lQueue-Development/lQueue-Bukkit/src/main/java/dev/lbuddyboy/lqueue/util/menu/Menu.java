package dev.lbuddyboy.lqueue.util.menu;

import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {

    public static Map<String, Menu> openedMenus = new ConcurrentHashMap<>();

    private int size;
    public Inventory inventory;

    public abstract String getTitle(Player player);
    public abstract List<Button> getButtons(Player player);

    public int getSize(Player player) {
        List<Button> buttons = getButtons(player);
        int highest = 0;
        for (Button button : buttons) {
            if (button.getSlot() <= 0) continue;
            if (button.getSlot() - 1 <= highest) continue;

            highest = button.getSlot() - 1;
        }
        return (int) (Math.ceil((double) (highest + 1) / 9.0) * 9.0);
    }

    public boolean cancellable() {
        return false;
    }

    public Inventory makeInventory(Player player) throws IOException {
        String title = this.getTitle(player);

        if (title.length() > 32) title = title.substring(0, 32);

        this.inventory = Bukkit.createInventory(null, getSize(player), title);

        if (autoFill()) {
            for (int i = 0; i < this.inventory.getSize(); i++) {
                this.inventory.setItem(i, autoFillItem());
            }
        }

        for (Button button : getButtons(player)) {
            if (button.getSlot() <= 0) continue;

            this.inventory.setItem(button.getSlot() - 1, button.getItem());
        }

        size = this.inventory.getSize();

        return this.inventory;
    }


    public void openMenu(Player player) {
        try {
            player.openInventory(makeInventory(player));
            openedMenus.put(player.getName(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(Player player) {
        openedMenus.remove(player.getName());
    }

    public void update(Player player) throws IOException {
        if (size != getSize(player)) {
            close(player);
            openMenu(player);
            return;
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();

        inventory.setContents(new ItemStack[0]);

        if (autoFill()) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, autoFillItem());
            }
        }

        for (Button button : getButtons(player)) {
            inventory.setItem(button.getSlot() - 1, button.getItem());
        }

        player.updateInventory();
    }

    public boolean autoFill() {
        return false;
    }

    public boolean autoUpdate() {
        return false;
    }

    public ItemStack autoFillItem() {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").setData(15).create();
    }

    public static void onDisable() {
        for (Map.Entry<String, Menu> entry : openedMenus.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) continue;
            player.closeInventory();
            entry.getValue().close(player);
        }
    }

    static {
        Bukkit.getScheduler().runTaskTimer(lQueue.getInstance(), () -> {
            for (Map.Entry<String, Menu> entry : openedMenus.entrySet()) {
                if (!entry.getValue().autoUpdate()) continue;
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) continue;
                try {
                    entry.getValue().update(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 20, 20);

        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), lQueue.getInstance());
    }

}
