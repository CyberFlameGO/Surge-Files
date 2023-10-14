package dev.lbuddyboy.pcore.auction.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.auction.menu.AuctionHouseMenu;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.PriceAmount;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("auction|ah")
public class AuctionCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new AuctionHouseMenu().openMenu(sender);
    }

    @Subcommand("sell")
    public void sell(Player sender, @Name("price") PriceAmount price) {
        ItemStack item = sender.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou must be holding an item to do this."));
            return;
        }

        sender.setItemInHand(null);
        pCore.getInstance().getAuctionHandler().createAuction(sender, item, price.getAmount(), EconomyType.MONEY);
        sender.sendMessage(CC.translate("&aYou have just created a new auction for $" + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(price.getAmount())));
    }

    @Subcommand("search")
    public void search(Player sender, @Name("key-word") String search) {
        AuctionHouseMenu menu = new AuctionHouseMenu();

        menu.setSearch(search);

        menu.openMenu(sender);
    }

}
