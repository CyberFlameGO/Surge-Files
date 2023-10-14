package dev.lbuddyboy.samurai.util.loottable.editor.menu;

import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.button.BackButton;
import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class EditLootTableMenu extends PagedMenu {

    private static final int[] ITEM_SLOTS = new int[]{
            10, 11, 12, 13, 14, 15, 16, 17, 18,
            19, 20, 21, 22, 23, 24, 25, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35, 36,
            37, 38, 39, 40, 41, 42, 43, 44, 45,
            46, 47, 48, 49, 50, 51, 52, 53, 54
    };

    private final LootTable lootTable;
    private List<LootTableItem> items = new ArrayList<>();
    private boolean massEditing = false;

    @Override
    public String getPageTitle(Player player) {
        return "Edit Items";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (LootTableItem item : lootTable.getItems().values()) {
            buttons.add(new ItemButton(i++, item));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(4, new LootTablesMenu()));
        buttons.add(new MassEditToggleButton());

        buttons.add(new Button() {
            @Override
            public int getSlot() {
                return 6;
            }

            @Override
            public ItemStack getItem() {
                if (massEditing) {
                    return ItemBuilder.of(Material.GREEN_BANNER).name("&aClick to edit all items selected.").build();
                }
                return ItemBuilder.of(Material.GREEN_BANNER).name("&aCreate a new empty item.").build();
            }

            @Override
            public void action(InventoryClickEvent event) {
                if (massEditing) {
                    if (items.isEmpty()) {
                        player.sendMessage(CC.translate("&cYou cannot edit an empty item set."));
                        return;
                    }
                    new EditLootTableItemMenu(items).openMenu(player);
                    return;
                }
                LootTableCommand.createItem(player, lootTable, String.valueOf(lootTable.getItems().size()), false);
            }
        });

        return buttons;
    }

    @Override
    public int[] getButtonSlots() {
        return ITEM_SLOTS;
    }

    @Override
    public int getPreviousButtonSlot() {
        return 1;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 9;
    }

    @Override
    public int getMaxPageButtons() {
        return ITEM_SLOTS.length;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class ItemButton extends Button {

        private int slot;
        private LootTableItem item;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            ItemBuilder builder = ItemBuilder.copyOf(this.item.getItem().clone());
            List<String> lore = new ArrayList<>();

            if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasLore()) {
                lore.addAll(ItemUtils.getLore(item.getItem()));
            }

            lore.add(" ");
            lore.add("&eChance&7: &f" + item.getChance() + "%");
            lore.add("&eID&7: &f" + item.getId());
            lore.add("&eSlot&7: &f" + item.getSlot());
            lore.add("&eGive Item&7: &f" + (item.isGiveItem() ? "Yes" : "No"));
            lore.add("&eCommands&7:");
            for (String command : item.getCommands()) {
                lore.add(CC.GRAY + "- " + command);
            }

            if (items.contains(this.item)) {
                builder.enchant(Enchantment.DURABILITY, 1);
                builder.flags(ItemFlag.HIDE_ENCHANTS);
            }

            builder.setLore(lore);

            return builder.build();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (massEditing) {
                items.add(item);
                return;
            }

            new EditLootTableItemMenu(this.item).openMenu(player);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    @AllArgsConstructor
    public class MassEditToggleButton extends Button {

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            if (!massEditing) return ItemBuilder.of(Material.RED_WOOL).name("&fMass Edit&7: &cOff").setLore(Collections.singletonList("&7Click to toggle on")).build();
            return ItemBuilder.of(Material.GREEN_WOOL).name("&fMass Edit&7: &aOn").setLore(Collections.singletonList("&7Click to toggle off")).build();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            items.clear();
            massEditing = !massEditing;
        }
    }

}
