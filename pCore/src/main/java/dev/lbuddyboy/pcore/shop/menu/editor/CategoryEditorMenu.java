package dev.lbuddyboy.pcore.shop.menu.editor;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.essential.kit.menu.KitMenu;
import dev.lbuddyboy.pcore.essential.kit.menu.KitPreviewMenu;
import dev.lbuddyboy.pcore.shop.EditorType;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CategoryEditorMenu extends Menu {

    private ShopCategory category;

    @Override
    public String getTitle(Player player) {
        return category.getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (EditorType type : EditorType.values()) {
            buttons.add(new EditorButton(type));
        }

        buttons.add(new ViewButton());
        buttons.add(new BackButton(1, new ShopEditorMenu()));

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
            List<String> lore = new ArrayList<>(ItemUtils.getLore(category.getDisplayItem()));

            lore.add("&eSlot&7: &f" + category.getSlot());
            lore.add("&eID&7: &f" + category.getName());
            lore.add("&eDisplay&7: &f" + category.getDisplayName());
            lore.add("&eItems&7: &f" + category.getItems().size());

            return new ItemBuilder(category.getDisplayItem())
                    .setName(category.getDisplayName())
                    .setLore(lore)
                    .create();
        }
    }

    @AllArgsConstructor
    public class EditorButton extends Button {

        private EditorType type;

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

            if (type == EditorType.ITEMS) {
                this.type.getEdit().call(player, category, "");
                return;
            }

            player.closeInventory();
            ConversationBuilder builder = new ConversationBuilder(player);
            builder.closeableStringPrompt(CC.translate(this.type.getPrompt()), (ctx, response) -> {
                        this.type.getEdit().call(player, category, response);
                        category.save();
                        new CategoryEditorMenu(category).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false);

            player.beginConversation(builder.build());
        }
    }

}
