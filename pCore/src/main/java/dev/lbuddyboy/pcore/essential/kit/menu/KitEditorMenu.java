package dev.lbuddyboy.pcore.essential.kit.menu;

import dev.lbuddyboy.pcore.essential.kit.editor.EditorType;
import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.essential.kit.editor.KitEdit;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.Tasks;
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
public class KitEditorMenu extends Menu {

    private Kit kit;

    @Override
    public String getTitle(Player player) {
        return kit.getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (EditorType type : EditorType.values()) {
            buttons.add(new EditorButton(type));
        }

        buttons.add(new PreviewButton());
        buttons.add(new BackButton(1, new KitMenu(kit.getCategory(), kit -> new KitEditorMenu(kit).openMenu(player))));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    public class PreviewButton extends Button {

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.ENDER_CHEST).setName("&6&lContent Preview &7(Click)").create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            new KitPreviewMenu(kit, KitEditorMenu.this).openMenu((Player) event.getWhoClicked());
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

            if (this.type == EditorType.CATEGORY) {
                this.type.getEdit().call(player, kit, "");
                return;
            }

            player.closeInventory();
            ConversationBuilder builder = new ConversationBuilder(player);
            builder.closeableStringPrompt(CC.translate(this.type.getPrompt()), (ctx, response) -> {
                        this.type.getEdit().call(player, kit, response);
                        kit.save();
                        new KitEditorMenu(kit).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false);

            player.beginConversation(builder.build());
        }
    }

}
