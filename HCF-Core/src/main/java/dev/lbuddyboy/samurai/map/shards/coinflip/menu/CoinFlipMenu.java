package dev.lbuddyboy.samurai.map.shards.coinflip.menu;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlip;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CoinFlipMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (CoinFlip cf : Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().values()) {
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
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getGuiSettings().getPreviousSlot();
    }

    @AllArgsConstructor
    public class PlayerInfoButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getInt("menu-settings.info-button.slot");
        }

        @Override
        public ItemStack getItem() {
            int profit = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getProfit(player.getUniqueId());

            return new ItemBuilder(Material.PLAYER_HEAD)
                    .setDurability(3)
                    .setOwner(player.getName())
                    .setName(Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getString("menu-settings.info-button.name"))
                    .setLore(Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getStringList("menu-settings.info-button.lore"),
                            "%wins%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getWins(player.getUniqueId()),
                            "%losses%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getLosses(player.getUniqueId()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.getNumberInstance(Locale.US).format(profit)
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
            double profit = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getProfit(coinFlip.getSender());

            return new ItemBuilder(Material.PLAYER_HEAD)
                    .setOwner(this.coinFlip.getSenderName())
                    .setName(Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getString("menu-settings.button-format.name"),
                            "%wins%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getWins(coinFlip.getSender()),
                            "%losses%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getLosses(coinFlip.getSender()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.getNumberInstance(Locale.US).format(profit),
                            "%player%", coinFlip.getSenderName(),
                            "%sent-at%", sdf.format(this.coinFlip.getSentAt()),
                            "%amount%", this.coinFlip.getEconomy().getPrefix() + "" + NumberFormat.getNumberInstance(Locale.US).format(this.coinFlip.getAmount()),
                            "%side%", this.coinFlip.getSide().getName()
                    )
                    .setLore(Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getStringList("menu-settings.button-format.lore"),
                            "%wins%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getWins(coinFlip.getSender()),
                            "%losses%", Samurai.getInstance().getShardHandler().getCoinFlipHandler().getLosses(coinFlip.getSender()),
                            "%profit%", (profit < 0 ? CC.RED : CC.GREEN) + NumberFormat.getNumberInstance(Locale.US).format(profit),
                            "%player%", coinFlip.getSenderName(),
                            "%sent-at%", sdf.format(this.coinFlip.getSentAt()),
                            "%amount%", this.coinFlip.getEconomy().getPrefix() + "" + NumberFormat.getNumberInstance(Locale.US).format(this.coinFlip.getAmount()),
                            "%side%", this.coinFlip.getSide().getName()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            if (coinFlip.getSender() == player.getUniqueId()) return;

            coinFlip.accept(player);
        }
    }

}
