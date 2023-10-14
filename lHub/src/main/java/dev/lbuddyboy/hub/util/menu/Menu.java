package dev.lbuddyboy.hub.util.menu;

import dev.lbuddyboy.flash.util.bukkit.CompMaterial;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    public Inventory makeInventory(Player player) {
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
        player.openInventory(makeInventory(player));
        openedMenus.put(player.getName(), this);
    }

    public void close(Player player) {
        openedMenus.remove(player.getName());
    }

    public void update(Player player) {
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
        return new ItemBuilder(CompMaterial.WHITE_STAINED_GLASS_PANE.getMaterial()).setDisplayName(" ").setData(15).create();
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
/*        Bukkit.getScheduler().runTaskTimer(lHub.getInstance(), () -> {
            for (Map.Entry<String, Menu> entry : openedMenus.entrySet()) {
                if (!entry.getValue().autoUpdate()) continue;
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) continue;
                entry.getValue().update(player);
            }
        }, 20, 20);*/

        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), lHub.getInstance());
    }

}
