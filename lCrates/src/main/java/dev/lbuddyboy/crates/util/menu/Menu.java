package dev.lbuddyboy.crates.util.menu;

import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.util.CompMaterial;
import dev.lbuddyboy.crates.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {

    public static Map<String, Menu> openedMenus = new ConcurrentHashMap<>();

    private int size;
    public Inventory inventory;

    public abstract String getTitle(Player player);
    public abstract List<Button> getButtons(Player player);

    public Menu() {
        if (ticks()) {
            startTick();
        }
    }

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
    public boolean ticks() {
        return false;
    }
    public BukkitRunnable tick() {
        return null;
    }
    public void startTick() {

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
        if (ticks() && tick() != null && Bukkit.getScheduler().isCurrentlyRunning(tick().getTaskId())) tick().cancel();
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

        this.inventory = inventory;

        player.updateInventory();
    }

    public void update(Player player, Button button) {
        if (size != getSize(player)) {
            close(player);
            openMenu(player);
            return;
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();

        inventory.setItem(button.getSlot() - 1, button.getItem());

        this.inventory = inventory;

        player.updateInventory();
    }

    public void onClose() {

    }

    public boolean autoFill() {
        return false;
    }

    public boolean autoUpdate() {
        return false;
    }

    public ItemStack autoFillItem() {
        return new ItemBuilder(CompMaterial.GRAY_STAINED_GLASS_PANE.getMaterial()).setDurability(CompMaterial.GRAY_STAINED_GLASS_PANE.getData()).setName(" ").create();
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
        Bukkit.getScheduler().runTaskTimer(lCrates.getInstance(), () -> {
            for (Map.Entry<String, Menu> entry : openedMenus.entrySet()) {
                if (!entry.getValue().autoUpdate()) continue;
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) continue;
                entry.getValue().update(player);
            }
        }, 10, 10);

        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), lCrates.getInstance());
    }

}
