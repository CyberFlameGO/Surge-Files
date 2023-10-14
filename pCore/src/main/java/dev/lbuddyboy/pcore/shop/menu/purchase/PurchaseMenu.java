package dev.lbuddyboy.pcore.shop.menu.purchase;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.shop.listener.ShopListener;
import dev.lbuddyboy.pcore.shop.menu.ShopCategoryMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ShopUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class PurchaseMenu extends Menu {

    public final ShopItem item;
    public final boolean sell;
    @Setter
    public int amount = -1;

    @Override
    public String getTitle(Player player) {
        return sell ? "Sell" : "Purchase";
    }

    private final int[] amounts = new int[]{
            1, 8, 32, 64, 128, 256, 512, 1024, -1
    };

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 10;
        for (int amount : amounts) {
            if (amount == -1) {
                buttons.add(new CustomAmountButton(i++, player));
                continue;
            }
            buttons.add(new ItemButton(i++, amount, player));
        }

        buttons.add(new BackButton(28, new ShopCategoryMenu(this.item.getCategory())));

        return buttons;
    }

    @AllArgsConstructor
    public class ItemButton extends Button {

        private int slot, amount;
        private Player player;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            String format = pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(amount);

            return new ItemBuilder(item.getData().toItemStack(Math.min(amount, 64)))
                    .setName((sell ? "&c&l-" : "&a&l+") + format)
                    .setLore(
                            "&ePrice&7: &f$" + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(item.getPrice(amount, sell)),
                            "&eAmount&7: &f" + format,
                            "",
                            "&7Click to confirm your " + (sell ? "sell" : "purchase") + "!")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            ShopUtils.process(player, item, this.amount, sell);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    @AllArgsConstructor
    public class CustomAmountButton extends Button {

        private int slot;
        private Player player;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.SIGN)
                    .setName("&e&lSpecific Amount")
                    .setLore(Collections.singletonList(
                            "&7Click to " + (sell ? "sell" : "purchase") + " a specific amount!"
                    ))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            player.closeInventory();
            player.setMetadata("change_amount", new FixedMetadataValue(pCore.getInstance(), PurchaseMenu.this));

            if (sell) {
                player.sendMessage(CC.translate("&aType the number of " + item.getDisplayName() + " that you would like to sell. Alternatively, you can type 'all' to sell all or 'cancel' to stop this process."));
                return;
            }
            player.sendMessage(CC.translate("&aType the number of " + item.getDisplayName() + " that you would like to purchase."));
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents(new ShopListener(), pCore.getInstance());
    }


}
