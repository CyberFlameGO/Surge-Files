package dev.aurapvp.samurai.enchants.menu;

import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchanterMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Custom Enchants";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (Rarity rarity : Samurai.getInstance().getEnchantHandler().getRarities()) {
            buttons.add(new RarityButton(rarity, i++));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class RarityButton extends Button {

        private Rarity rarity;
        private int slot;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.BOOK)
                    .setName(rarity.getColor() + rarity.getDisplayName())
                    .setLore(
                            "&fXP Needed&7: " + rarity.getColor() + rarity.getXpNeeded() + " Levels",
                            "",
                            "&7Click to purchase an enchantment book!"
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (player.getExpToLevel() < rarity.getXpNeeded()) {
                player.sendMessage(CC.translate("&cYou need " + (rarity.getXpNeeded() - player.getExpToLevel()) + " more xp to obtain this enchant book."));
                return;
            }

            player.getInventory().addItem(rarity.getOpenItem());
            player.setLevel(player.getLevel() - rarity.getXpNeeded());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        }
    }

}
