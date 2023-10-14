package dev.lbuddyboy.samurai.util.loottable.menu;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootTablePreviewMenu extends Menu {

    private LootTable lootTable;
    private Menu back;
    private dev.lbuddyboy.samurai.util.menu.Menu back2;

    public LootTablePreviewMenu(LootTable lootTable, Menu back) {
        this.lootTable = lootTable;
        this.back = back;
    }

    public LootTablePreviewMenu(LootTable lootTable, dev.lbuddyboy.samurai.util.menu.Menu back) {
        this.lootTable = lootTable;
        this.back2 = back;
    }

    @Override
    public String getTitle(Player player) {
        return "Loot Preview";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Map.Entry<Integer, ItemStack> entry : lootTable.getFillers().entrySet()) {
            buttons.add(new FillerButton(entry.getKey(), entry.getValue()));
        }

        for (LootTableItem item : lootTable.getItems().values()) {
            buttons.add(new ItemButton(item.getSlot(), item.getItem(), item, player));
        }

        return buttons;
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (back != null) Tasks.run(() -> back.openMenu(player));
        if (back2 != null) Tasks.run(() -> back2.openMenu(player));

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
        private LootTableItem item;
        private Player player;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            ItemBuilder builder = new ItemBuilder(this.stack.clone());
            List<String> lore = new ArrayList<>();

            if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasLore()) {
                lore.addAll(ItemUtils.getLore(item.getItem()));
            }

            lore.add(" ");
            lore.add("&eChance&7: &f" + item.getChance() + "%");
            if (player.hasPermission("loottables.admin")) {
                lore.add("&eID&7: &f" + item.getId());
                lore.add("&eSlot&7: &f" + item.getSlot());
                lore.add("&eGive Item&7: &f" + (item.isGiveItem() ? "Yes" : "No"));
                lore.add("&eCommands&7:");
                for (String command : item.getCommands()) {
                    lore.add(CC.GRAY + "- " + command);
                }
            }

            builder.setLore(lore);

            return builder.create();
        }

    }

}
