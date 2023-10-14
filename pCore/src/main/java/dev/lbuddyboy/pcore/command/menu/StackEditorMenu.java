package dev.lbuddyboy.pcore.command.menu;

import dev.lbuddyboy.pcore.command.CoreCommand;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StackEditorMenu extends Menu {

    private ItemStack stack;

    @Override
    public String getTitle(Player player) {
        return "Item Editor";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        if (stack.getItemMeta() != null && stack.getItemMeta() instanceof SkullMeta) {
            for (CoreCommand.SkullEditorType type : CoreCommand.SkullEditorType.values()) {
                buttons.add(new SkullEditorButton(type));
            }
        }

        for (CoreCommand.StackEditorType type : CoreCommand.StackEditorType.values()) {
            buttons.add(new EditorButton(type));
        }

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @AllArgsConstructor
    public class EditorButton extends Button {

        private CoreCommand.StackEditorType type;

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

/*            if (type == CoreCommand.StackEditorType.COLOR) {
                new ChangeColorMenu(stack, null).openMenu(player);
                return;
            }*/

            if (type == CoreCommand.StackEditorType.LORE) {
                new LoreEditorMenu(stack, ItemUtils.getLore(stack), StackEditorMenu.this).openMenu(player);
                return;
            }

            player.closeInventory();
            ConversationBuilder builder = new ConversationBuilder(player);
            builder.closeableStringPrompt(CC.translate(this.type.getPrompt()), (ctx, response) -> {
                        this.type.getEdit().call(player, stack, response);
                        new StackEditorMenu(stack).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false);

            player.beginConversation(builder.build());
        }
    }

    @AllArgsConstructor
    public class SkullEditorButton extends Button {

        private CoreCommand.SkullEditorType type;

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

            player.closeInventory();
            ConversationBuilder builder = new ConversationBuilder(player);
            builder.closeableStringPrompt(CC.translate(this.type.getPrompt()), (ctx, response) -> {
                        this.type.getEdit().call(player, stack, response);
                        new StackEditorMenu(stack).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false);

            player.beginConversation(builder.build());
        }
    }

}