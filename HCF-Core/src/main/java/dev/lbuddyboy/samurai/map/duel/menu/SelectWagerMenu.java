package dev.lbuddyboy.samurai.map.duel.menu;

import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.menus.ConfirmMenu;
import dev.lbuddyboy.samurai.util.object.Callback;
import dev.lbuddyboy.samurai.util.NumberUtils;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SelectWagerMenu extends Menu {

    private Callback<Integer> callback;

    private static List<Integer> PURPLE_SLOTS = Arrays.asList(0, 2, 4, 6, 8, 10, 16, 18, 20, 22, 24, 26);
    private static List<Integer> MAGENTA_SLOTS = Arrays.asList(1, 3, 5, 7, 9, 17, 19, 21, 23, 25);

    public SelectWagerMenu(Callback<Integer> callback) {
        super("Choose Wager Amount");

        this.callback = callback;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i : PURPLE_SLOTS) {
            buttons.put(i, Button.placeholder(Material.WHITE_STAINED_GLASS_PANE));
        }

        for (int i : MAGENTA_SLOTS) {
            buttons.put(i, Button.placeholder(Material.ORANGE_STAINED_GLASS_PANE));
        }

        buttons.put(12, new WagerAmountButton(1, callback));
        buttons.put(13, new CustomWagerAmountButton(callback));
        buttons.put(14, new WagerAmountButton(5, callback));

        return buttons;
    }

    @AllArgsConstructor
    private class WagerAmountButton extends Button {

        private int amount;
        private Callback<Integer> callback;

        @Override
        public String getName(Player player) {
            return ChatColor.GREEN.toString() + ChatColor.BOLD + amount + " Shard" + (amount != 1 ? "s" : "");
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public int getAmount(Player player) {
            return amount;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.EMERALD;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            int shards = (int) Samurai.getInstance().getShardMap().getShards(player);
            if (amount > shards) {
                player.sendMessage(ChatColor.RED + "You do not have enough shards for this!");
                return;
            }

            new ConfirmMenu("Are you sure?", success -> {
                if (success) {
                    callback.callback(amount);
                } else {
                    player.closeInventory();
                }
            }).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class CustomWagerAmountButton extends Button {

        private Callback<Integer> callback;

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD.toString() + ChatColor.BOLD + "Custom Amount";
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();

            description.add(ChatColor.WHITE + "Wager a non-specified amount");

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.GOLD_INGOT;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemStack itemStack = super.getButtonItem(player);
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            return ItemBuilder.copyOf(itemStack).flags(ItemFlag.HIDE_ENCHANTS).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            createConversation(player);
        }

        private void createConversation(Player player) {
            ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                @Override
                public String getPromptText(ConversationContext context) {
                    return "§aHow many shards do you want to wager? Type an amount to wager, or §cquit§a to cancel.";
                }

                @Override
                public Prompt acceptInput(ConversationContext cc, String s) {
                    if (s.equalsIgnoreCase("quit")) {
                        cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                        return Prompt.END_OF_CONVERSATION;
                    }

                    if (NumberUtils.isInteger(s)) {
                        int amount = Integer.parseInt(s);

                        if (amount <= -1) {
                            cc.getForWhom().sendRawMessage(ChatColor.RED + "You cannot wager that low.");
                            return Prompt.END_OF_CONVERSATION;
                        }

                        int shards = (int) Samurai.getInstance().getShardMap().getShards(player);
                        if (amount > shards) {
                            cc.getForWhom().sendRawMessage(ChatColor.RED + "You do not have enough shards for this!");
                            return Prompt.END_OF_CONVERSATION;
                        }

                        new ConfirmMenu("Are you sure?", success -> {
                            if (success) {
                                callback.callback(amount);
                            } else {
                                player.closeInventory();
                            }
                        }).openMenu(player);

                        return Prompt.END_OF_CONVERSATION;
                    }

                    cc.getForWhom().sendRawMessage(ChatColor.RED + "That is not a valid number. Type a number or \"quit\" to cancel.");
                    return Prompt.END_OF_CONVERSATION;
                }
            }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

            Conversation con = factory.buildConversation(player);
            player.beginConversation(con);
        }
    }

}
