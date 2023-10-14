package dev.lbuddyboy.pcore.coinflip.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.CoinFlip;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.NumberFormat;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CoinFlipMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(pCore.getInstance().getCoinFlipHandler().getGuiSettings().getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (CoinFlip cf : pCore.getInstance().getCoinFlipHandler().getCoinFlips().values()) {
            buttons.add(new CoinFlipButton(cf));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new PlayerInfoButton(player));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getCoinFlipHandler().getGuiSettings().getPreviousSlot();
    }

    @AllArgsConstructor
    public class PlayerInfoButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return pCore.getInstance().getCoinFlipHandler().getConfig().getInt("menu-settings.info-button.slot");
        }

        @Override
        public ItemStack getItem() {
            double profit = pCore.getInstance().getCoinFlipHandler().getProfit(player.getUniqueId());

            return new ItemBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setOwner(player.getName())
                    .setName(pCore.getInstance().getCoinFlipHandler().getConfig().getString("menu-settings.info-button.name"))
                    .setLore(pCore.getInstance().getCoinFlipHandler().getConfig().getStringList("menu-settings.info-button.lore"),
                            "%wins%", pCore.getInstance().getCoinFlipHandler().getWins(player.getUniqueId()),
                            "%losses%", pCore.getInstance().getCoinFlipHandler().getLosses(player.getUniqueId()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.formatNumber(profit)
                            )
                    .create();
        }
    }

    @AllArgsConstructor
    public class CoinFlipButton extends Button {

        private CoinFlip coinFlip;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            double profit = pCore.getInstance().getCoinFlipHandler().getProfit(coinFlip.getSender());

            return new ItemBuilder(Material.SKULL_ITEM)
                    .setOwner(this.coinFlip.getSenderName())
                    .setName(pCore.getInstance().getCoinFlipHandler().getConfig().getString("menu-settings.button-format.name"),
                            "%wins%", pCore.getInstance().getCoinFlipHandler().getWins(coinFlip.getSender()),
                            "%losses%", pCore.getInstance().getCoinFlipHandler().getLosses(coinFlip.getSender()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.formatNumber(profit),
                            "%player%", coinFlip.getSenderName(),
                            "%sent-at%", sdf.format(this.coinFlip.getSentAt()),
                            "%amount%", this.coinFlip.getEconomy().getPrefix() + "" + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.coinFlip.getAmount()),
                            "%side%", this.coinFlip.getSide().getName()
                    )
                    .setLore(pCore.getInstance().getCoinFlipHandler().getConfig().getStringList("menu-settings.button-format.lore"),
                            "%wins%", pCore.getInstance().getCoinFlipHandler().getWins(coinFlip.getSender()),
                            "%losses%", pCore.getInstance().getCoinFlipHandler().getLosses(coinFlip.getSender()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.formatNumber(profit),
                            "%player%", coinFlip.getSenderName(),
                            "%sent-at%", sdf.format(this.coinFlip.getSentAt()),
                            "%amount%", this.coinFlip.getEconomy().getPrefix() + "" + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.coinFlip.getAmount()),
                            "%side%", this.coinFlip.getSide().getName()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            if (coinFlip.getSender() == player.getUniqueId()) return;

            coinFlip.accept(player);
        }
    }

}
