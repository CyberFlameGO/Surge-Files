package dev.lbuddyboy.bunkers.util.menu;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.bukkit.CompatibleMaterial;
import dev.lbuddyboy.bunkers.util.bukkit.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {

    public static Map<String, Menu> openedMenus = new ConcurrentHashMap<>();

    private int size;

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

    public Inventory makeInventory(Player player) {
        String title = this.getTitle(player);

        if (title.length() > 32) title = title.substring(0, 32);

        Inventory inventory = Bukkit.createInventory(null, getSize(player), title);

        if (autoFill()) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, autoFillItem());
            }
        }

        for (Button button : getButtons(player)) {
            if (button.getSlot() <= 0) continue;

            inventory.setItem(button.getSlot(), button.getItem());
        }

        size = inventory.getSize();

        return inventory;
    }


    public void openMenu(Player player) {
        player.setMetadata("opening_menu", new FixedMetadataValue(Bunkers.getInstance(), true));
        player.openInventory(makeInventory(player));
        player.removeMetadata("opening_menu", Bunkers.getInstance());
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
            inventory.setItem(button.getSlot(), button.getItem());
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
        return new ItemBuilder(CompatibleMaterial.getMaterial("STAINED_GLASS_PANE")).setName(" ").setDurability(15).create();
    }

    static {
        Bunkers.getInstance().getServer().getScheduler().runTaskTimer(Bunkers.getInstance(), () -> {
            for (Map.Entry<String, Menu> entry : openedMenus.entrySet()) {
                if (!entry.getValue().autoUpdate()) continue;
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) continue;
                entry.getValue().update(player);
            }
        }, 20, 20);

        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), Bunkers.getInstance());
    }

}
