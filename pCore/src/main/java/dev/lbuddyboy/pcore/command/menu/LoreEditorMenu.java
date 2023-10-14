package dev.lbuddyboy.pcore.command.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class LoreEditorMenu extends PagedMenu {

    private final ItemStack stack;
    private final List<String> lore;
    private final Menu previous;
    private Callback.SingleCallback<List<String>> callable;

    @Override
    public String getPageTitle(Player player) {
        return "Edit Lore";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (String s : lore) {
            buttons.add(new LineButton(i++, s));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new AddLineButton());
        buttons.add(new ConfirmButton());
        buttons.add(new ViewButton());

        return buttons;
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (player.hasMetadata("opening_other")) return;
        if (previous == null) return;

        Tasks.run(() -> previous.openMenu(player));
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    public class AddLineButton extends Button {

        @Override
        public int getSlot() {
            return 7;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL).setDurability(4).setName("&a&lADD LINE &7(CLICK)").create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            lore.add("&7Empty Line");
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    public class ViewButton extends Button {

        @Override
        public int getSlot() {
            return 3;
        }

        @Override
        public ItemStack getItem() {
            if (stack == null) {
                return new ItemBuilder(Material.BOOK).setLore(lore).create();
            }
            return new ItemBuilder(stack).setLore(lore).create();
        }

    }

    public class ConfirmButton extends Button {

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.ANVIL).setName("&a&lCONFIRM EDIT &7(CLICK)").create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            if (callable != null) {
                callable.call(lore);
                return;
            }

            ItemMeta meta = stack.getItemMeta();
            meta.setLore(CC.translate(lore));
            stack.setItemMeta(meta);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

    }

    @AllArgsConstructor
    public class LineButton extends Button {

        private int index;
        private String line;

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            ItemBuilder builder = new ItemBuilder(Material.PAPER).setName(line);

            builder.setLore("&7Click to edit the text of this line!");
            builder.addLoreLine("&7Drop to remove this line!");

            return builder.create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() != ClickType.DROP && event.getClick() != ClickType.CONTROL_DROP) {
                ConversationBuilder builder = new ConversationBuilder(player);

                player.setMetadata("opening_other", new FixedMetadataValue(pCore.getInstance(), true));
                player.closeInventory();
                player.beginConversation(builder
                                .stringPrompt(CC.translate("&aType a new value for this lore line. You can cancel this process by typing 'cancel'!"), (ctx, response) -> {
                                    if (response.equalsIgnoreCase("cancel")) {
                                        player.sendMessage(CC.translate("&cProcess cancelled."));
                                        new LoreEditorMenu(stack, lore, previous).openMenu(player);
                                        return Prompt.END_OF_CONVERSATION;
                                    }

                                    lore.set(index, response);
                                    new LoreEditorMenu(stack, lore, previous, callable).openMenu(player);
                                    return Prompt.END_OF_CONVERSATION;
                                })
                        .echo(false).build());
                player.removeMetadata("opening_other", pCore.getInstance());
                return;
            }

            lore.remove(line);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}