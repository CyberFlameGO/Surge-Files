package dev.lbuddyboy.pcore.auction.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.auction.AuctionItem;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.impl.ConfirmMenu;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class AuctionHouseMenu extends PagedMenu {

    private boolean bin = false, personal = false;
    private String search;

    @Override
    public String getPageTitle(Player player) {
        if (bin) {
            return "Trash Bin";
        }
        if (personal) {
            return "Your Auctions";
        }
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getTitle();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().isAutoFill();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<AuctionItem> items = new ArrayList<>();

        for (List<AuctionItem> is : pCore.getInstance().getAuctionHandler().getAuctions().values()) {
            items.addAll(is);
        }

        if (!bin && !personal) {
            items = items.stream().filter(i -> !i.isBin()).collect(Collectors.toList());
        } else if (bin) {
            items = items.stream().filter(AuctionItem::isBin).collect(Collectors.toList());
        } else {
            items = items.stream().filter(i -> i.getSender().toString().equals(player.getUniqueId().toString())).filter(i -> !i.isBin()).collect(Collectors.toList());
        }
        if (search != null) {
            items = pCore.getInstance().getAuctionHandler().getAuctionItems(search);
        }


        items.sort(Comparator.comparingLong(AuctionItem::getExpiry));
        Collections.reverse(items);

        for (AuctionItem item : items) {
            buttons.add(new AuctionItemButton(item, player));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return new ArrayList<Button>() {{
            add(new HelpButton());

            if (bin) {
                add(new CollectionBoxButton());
                add(new MainMenuButton(51));
            } else if (personal) {
                add(new MainMenuButton(49));
                add(new TrashBinButton());
            } else {
                add(new CollectionBoxButton());
                add(new TrashBinButton());
            }
        }};
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getSize();
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getPreviousSlot();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getNextSlot();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getAuctionHandler().getPagedGUISettings().getButtonSlots();
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class AuctionItemButton extends Button {

        private AuctionItem item;
        private Player player;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            ItemBuilder builder = new ItemBuilder(item.getItem().clone());
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));

            if (bin) {
                builder.addLoreLines(pCore.getInstance().getAuctionHandler().getConfig().getStringList("menu-settings.bin-format"),
                        "%expires-in%", TimeUtils.formatIntoDetailedString((int) (this.item.getExpiry() / 1000)),
                        "%price%", this.item.getEconomyType().getPrefix() + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.item.getPrice()),
                        "%seller%", Bukkit.getOfflinePlayer(this.item.getSender()).getName()
                );
            } else if (personal && item.getSender().toString().equals(player.getUniqueId().toString())) {
                builder.addLoreLines(pCore.getInstance().getAuctionHandler().getConfig().getStringList("menu-settings.personal-format"),
                        "%expires-in%", TimeUtils.formatIntoDetailedString((int) (this.item.getExpiry() / 1000)),
                        "%price%", this.item.getEconomyType().getPrefix() + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.item.getPrice()),
                        "%seller%", Bukkit.getOfflinePlayer(this.item.getSender()).getName()
                );
            } else {
                builder.addLoreLines(pCore.getInstance().getAuctionHandler().getConfig().getStringList("menu-settings.normal-format"),
                        "%expires-in%", TimeUtils.formatIntoDetailedString((int) (this.item.getExpiry() / 1000)),
                        "%price%", this.item.getEconomyType().getPrefix() + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.item.getPrice()),
                        "%seller%", Bukkit.getOfflinePlayer(this.item.getSender()).getName()
                );
            }

            return builder.create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (bin) {
                int first = player.getInventory().firstEmpty();

                if (first == -1) {
                    player.sendMessage(CC.translate("&cYou do not have enough space for this in your inventory."));
                    return;
                }

                player.getInventory().addItem(item.getItem());
                pCore.getInstance().getAuctionHandler().deleteAuction(player.getUniqueId(), item);
                return;
            }

            if (!item.getSender().toString().equals(player.getUniqueId().toString())) {
                if (!item.getEconomyType().hasAmount(player.getUniqueId(), item.getPrice())) {
                    player.sendMessage(CC.translate("&cYou do not have enough " + item.getEconomyType().getName().toLowerCase() + " to purchase this item."));
                    return;
                }

                int first = player.getInventory().firstEmpty();

                if (first == -1) {
                    player.sendMessage(CC.translate("&cYou do not have enough space for this in your inventory."));
                    return;
                }

                new ConfirmMenu("&a&lConfirm Purchase", "&a&lPurchase Item", "&c&lCancel Purchase", () -> {
                    item.getEconomyType().removeAmount(player.getUniqueId(), item.getPrice(), IEconomy.EconomyChange.builder().build());
                    item.getEconomyType().addAmount(item.getSender(), item.getPrice(), IEconomy.EconomyChange.builder().build());
                    player.getInventory().addItem(item.getItem());
                    player.updateInventory();

                    Player seller = Bukkit.getPlayer(item.getSender());
                    if (seller != null) {
                        seller.sendMessage(CC.translate(pCore.getInstance().getAuctionHandler().getConfig().getString("messages.bought-sender"),
                                "%buyer%", player.getName(),
                                "%item%", ItemUtils.getName(item.getItem()),
                                "%amount%", item.getEconomyType().getPrefix() + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(item.getPrice())
                        ));
                    }

                    pCore.getInstance().getAuctionHandler().deleteAuction(item.getSender(), item);

                    AuctionHouseMenu menu = new AuctionHouseMenu();
                    menu.setBin(isBin());
                    menu.setPersonal(isPersonal());

                    menu.openMenu(player);
                }, () -> {
                    AuctionHouseMenu menu = new AuctionHouseMenu();
                    menu.setBin(isBin());
                    menu.setPersonal(isPersonal());
                    menu.page = page;

                    menu.openMenu(player);
                }).openMenu(player);

                return;
            }

            item.setCancelled(true);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

    }

    public class HelpButton extends Button {

        @Override
        public int getSlot() {
            return 50;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.SIGN)
                    .setName("&6&lUseful Commands")
                    .setLore(
                            CC.MENU_BAR,
                            "&e&lUseful Commands",
                            "&7/ah sell <price> - put the item in your hand up for auction",
                            "&7/ah search <key-word> - searches for the specified key word in lores, names, and materials",
                            CC.MENU_BAR
                    )
                    .create();
        }
    }

    public class CollectionBoxButton extends Button {

        @Override
        public int getSlot() {
            return 49;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.CHEST)
                    .setName("&6&lYour Auctions")
                    .setLore(
                            CC.MENU_BAR,
                            "&7Click to view your auction bin!",
                            CC.MENU_BAR
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            setSearch(null);
            setBin(false);
            setPersonal(true);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    @AllArgsConstructor
    public class MainMenuButton extends Button {

        private int slot;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.PAINTING)
                    .setName("&6&lMain Menu")
                    .setLore(
                            CC.MENU_BAR,
                            "&7Click to view all auctions!",
                            CC.MENU_BAR
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            setSearch(null);
            setBin(false);
            setPersonal(false);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    public class TrashBinButton extends Button {

        @Override
        public int getSlot() {
            return 51;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.DISPENSER)
                    .setName("&6&lTrash Bin")
                    .setLore(
                            CC.MENU_BAR,
                            "&7Click to view your expired/cancelled auctions!",
                            CC.MENU_BAR
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            setSearch(null);
            setPersonal(false);
            setBin(true);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
