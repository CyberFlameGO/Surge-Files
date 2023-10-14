package dev.lbuddyboy.pcore.essential.offline.menu;

import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
public class OfflineEditorMenu extends Menu {

    private final OfflinePlayer target;
    private final OfflineData cache;
    private Menu back;

    @Override
    public String getTitle(Player player) {
        return target.getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (ItemStack stack : cache.getArmor()) {
            buttons.add(Button.fromItem(stack, i++));
        }

        i = 9;
        for (int j = 0; j < 9; j++) {
            buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 1).setName(" ").create(), ++i));
        }

        i = 19;
        for (ItemStack stack : cache.getContents()) {
            buttons.add(Button.fromItem(stack, i++));
        }

        if (back != null) {
            buttons.add(new BackButton(8, this.back));
        }

        buttons.add(new TeleportButton());

        return buttons;
    }

    @Override
    public boolean cancellable() {
        return true;
    }

    @Override
    public void close(Player player) {
        super.close(player);

        if (!cache.isEdited()) return;

        ItemStack[] contents = new ItemStack[36];

        int index = 0;
        for (int i = 19; i <= 54; i++) {
            for (Button button : getButtons(player)) {
                if (button.getSlot() == i) {
                    contents[index++] = button.getItem();
                }
            }
        }

        ItemStack[] armor = new ItemStack[4];

        index = 0;
        for (int i = 1; i <= 4; i++) {
            for (Button button : getButtons(player)) {
                if (button.getSlot() == i) {
                    armor[index++] = button.getItem();
                }
            }
        }

        cache.setContents(contents);
        cache.setContents(armor);

    }

    @AllArgsConstructor
    public class ItemButton extends Button {

        private int i;
        private ItemStack stack;

        @Override
        public int getSlot() {
            return i;
        }

        @Override
        public ItemStack getItem() {
            return stack;
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            event.setCancelled(false);

            if (cache.isEdited()) return;

            cache.setEdited(true);
        }

        @Override
        public boolean cancels() {
            return false;
        }
    }

    @AllArgsConstructor
    public class TeleportButton extends Button {

        @Override
        public int getSlot() {
            return 9;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.ENDER_PEARL)
                    .setName("&d&lTELEPORT HERE &7(CLICK)")
                    .setLore("&7" + cache.getLocation().getBlockX() + ", " + cache.getLocation().getBlockY() + ", " + cache.getLocation().getBlockZ())
                    .create();
        }
    }

}
