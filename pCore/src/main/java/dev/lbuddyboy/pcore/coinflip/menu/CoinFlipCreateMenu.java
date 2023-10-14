package dev.lbuddyboy.pcore.coinflip.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.CoinFlipType;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CoinFlipCreateMenu extends Menu {

    private EconomyType economy;
    private double amount;

    @Override
    public String getTitle(Player player) {
        return "Create a CoinFlip...";
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new HeadsButton());
        buttons.add(new TailsButton());

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    private class HeadsButton extends Button {

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .setName("&a&lHEADS")
                    .setLore("&7Click here to choose your side as heads!")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (!economy.hasAmount(player.getUniqueId(), amount)) {
                player.sendMessage(CC.translate("&cYou do not have enough " + economy.getName() + " to create a coin flip."));
                return;
            }

            economy.removeAmount(player.getUniqueId(), amount, IEconomy.EconomyChange.builder().build());
            pCore.getInstance().getCoinFlipHandler().createCoinFlip(player, amount, economy, CoinFlipType.HEADS);
            player.sendMessage(CC.translate("&a&lCOIN FLIP &7" + CC.UNICODE_ARROW_RIGHT + " You have created a new coin flip!"));
            player.closeInventory();
        }
    }

    private class TailsButton extends Button {

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.RABBIT_FOOT)
                    .setName("&a&lTAILS")
                    .setLore("&7Click here to choose your side as tails!")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (!economy.hasAmount(player.getUniqueId(), amount)) {
                player.sendMessage(CC.translate("&cYou do not have enough " + economy.getName() + " to create a coin flip."));
                return;
            }

            boolean removes = economy.removeAmount(player.getUniqueId(), amount, IEconomy.EconomyChange.builder().forced(false).build());

            if (!removes) {
                player.sendMessage(CC.translate("&cYou do not have enough " + economy.getName() + " to create a coin flip."));
                return;
            }

            pCore.getInstance().getCoinFlipHandler().createCoinFlip(player, amount, economy, CoinFlipType.TAILS);
            player.sendMessage(CC.translate("&a&lCOIN FLIP &7" + CC.UNICODE_ARROW_RIGHT + " You have created a new coin flip!"));
            player.closeInventory();
        }
    }

}
