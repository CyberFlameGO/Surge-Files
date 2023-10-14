package dev.lbuddyboy.samurai.map.kits.editor.menu;

import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.map.kits.Kit;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
final class KitRenameButton extends Button {

    private final Kit kit;

    @Override
    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Rename";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.GRAY + "Click to " + ChatColor.LIGHT_PURPLE + "rename" + ChatColor.GRAY + " this kit!"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEGACY_SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Renaming " + ChatColor.RESET + kit.getName() + ChatColor.YELLOW + "... Enter the new name now.";
            }

            @Override
            public Prompt acceptInput(ConversationContext ctx, String s) {
                if (s.length() > 20) {
                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Kit names can't have more than 20 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                kit.setName(s);

                ctx.getForWhom().sendRawMessage(ChatColor.YELLOW + "Kit renamed.");

                new KitsMenu().openMenu(player);

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false);

        player.closeInventory();
        player.beginConversation(factory.buildConversation(player));
    }

}