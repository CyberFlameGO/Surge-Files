package dev.lbuddyboy.crates.menu;

import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.model.CrateItem;
import dev.lbuddyboy.crates.util.ItemBuilder;
import dev.lbuddyboy.crates.util.Tasks;
import dev.lbuddyboy.crates.util.menu.Button;
import dev.lbuddyboy.crates.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CratePreviewMenu extends Menu {

    private Crate crate;
    private boolean back;

    @Override
    public String getTitle(Player player) {
        return "Crate Preview";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Map.Entry<Integer, ItemStack> entry : crate.getFillers().entrySet()) {
            buttons.add(new FillerButton(entry.getKey(), entry.getValue()));
        }

        for (CrateItem item : crate.getCrateItems().values()) {
            buttons.add(new ItemButton(item.getSlot(), item.getItem(), item, player));
        }

        return buttons;
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (back) Tasks.run(() -> new OpenCrateMenu().openMenu(player));
    }

    @AllArgsConstructor
    public class FillerButton extends Button {

        private int slot;
        private ItemStack stack;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return this.stack;
        }

    }

    @AllArgsConstructor
    public class ItemButton extends Button {

        private int slot;
        private ItemStack stack;
        private CrateItem item;
        private Player player;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            if (this.stack == null || this.stack.getItemMeta() == null || this.stack.getType() == Material.AIR) return this.stack;

            ItemBuilder builder = new ItemBuilder(this.stack.clone());

            try {
                builder.addLoreLine(" ");
                builder.addLoreLine("&eChance&7: &f" + item.getChance() + "%");
                if (player.hasPermission("crate.admin")) {
                    builder.addLoreLine("&eID&7: &f" + item.getId());
                    builder.addLoreLine("&eSlot&7: &f" + item.getSlot());
                }
            } catch (Exception ignored) {
                builder.setLore(new ArrayList<>(Arrays.asList(" ")));
                builder.addLoreLine(" ");
                builder.addLoreLine("&eChance&7: &f" + item.getChance() + "%");
                if (player.hasPermission("crate.admin")) {
                    builder.addLoreLine("&eID&7: &f" + item.getId());
                    builder.addLoreLine("&eSlot&7: &f" + item.getSlot());
                }
            }

            return builder.create();
        }

    }

}
