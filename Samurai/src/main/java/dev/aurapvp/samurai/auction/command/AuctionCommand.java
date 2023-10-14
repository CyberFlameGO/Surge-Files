package dev.aurapvp.samurai.auction.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.auction.menu.AuctionHouseMenu;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.util.CC;
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
    public void sell(Player sender, @Name("price") double price) {
        ItemStack item = sender.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou must be holding an item to do this."));
            return;
        }

        sender.setItemInHand(null);
        Samurai.getInstance().getAuctionHandler().createAuction(sender, item, price, EconomyType.MONEY);
        sender.sendMessage(CC.translate("&aYou have just created a new auction for $" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(price)));
    }

    @Subcommand("search")
    public void search(Player sender, @Name("key-word") String search) {
        AuctionHouseMenu menu = new AuctionHouseMenu();

        menu.setSearch(search);

        menu.openMenu(sender);
    }

}
