package dev.lbuddyboy.pcore.shop.menu.editor;

import dev.lbuddyboy.pcore.command.menu.LoreEditorMenu;
import dev.lbuddyboy.pcore.shop.EditorType;
import dev.lbuddyboy.pcore.shop.ItemEditorType;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ShopItemEditorMenu extends Menu {

    private ShopItem item;

    @Override
    public String getTitle(Player player) {
        return item.getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ItemEditorType type : ItemEditorType.values()) {
            buttons.add(new EditorButton(type));
        }

        buttons.add(new BackButton(1, new ItemsEditorMenu(item.getCategory())));
        buttons.add(new ViewButton());

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }


    public class ViewButton extends Button {

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>(item.getDescription());

            lore.add("&eBuy Price&7: &f" + item.getBuyPrice());
            lore.add("&eSell Price&7: &f" + item.getSellPrice());
            lore.add("&eID&7: &f" + item.getName());
            lore.add("&eCategory&7: &f" + item.getCategory().getName());
            lore.add("&eDisplay&7: &f" + item.getDisplayName());

            return new ItemBuilder(item.getData().toItemStack(1))
                    .setName(item.getDisplayName())
                    .setLore(lore)
                    .create();
        }
    }

    @AllArgsConstructor
    public class EditorButton extends Button {

        private ItemEditorType type;

        @Override
        public int getSlot() {
            return this.type.getSlot();
        }

        @Override
        public ItemStack getItem() {
            return this.type.getStack();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (type == ItemEditorType.CATEGORY) {
                this.type.getEdit().call(player, item, "");
                return;
            }

            if (type == ItemEditorType.CLONE) {
                this.type.getEdit().call(player, item, "");
                return;
            }

            if (type == ItemEditorType.DESCRIPTION) {
                new LoreEditorMenu(null, item.getDescription(), ShopItemEditorMenu.this, (lore) -> {
                    item.setDescription(lore);
                    item.save();
                    player.sendMessage(CC.translate("&aSuccessfully updated the lore to the shop item."));
                }).openMenu(player);
                return;
            }

            player.closeInventory();
            ConversationBuilder builder = new ConversationBuilder(player);
            builder.closeableStringPrompt(CC.translate(this.type.getPrompt()), (ctx, response) -> {
                        this.type.getEdit().call(player, item, response);
                        item.save();
                        new ShopItemEditorMenu(item).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false);

            player.beginConversation(builder.build());
        }
    }

}
