package dev.lbuddyboy.samurai.custom.supplydrops.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.supplydrops.SupplyCrate;
import dev.lbuddyboy.samurai.custom.supplydrops.SupplyDropHandler;
import dev.lbuddyboy.samurai.custom.supplydrops.menu.SupplyCrateMenu;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 10:26 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops.command
 */

@CommandAlias("supplycrate|supplycrates|scrates")
public class SupplyCrateCommand extends BaseCommand {

    @Default
    public static void def(CommandSender sender) {
        info(sender);
    }

    @Subcommand("info")
    public static void info(CommandSender sender) {
        SupplyCrate supplyCrate = Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate();
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&d&lSupply Crate Information"));
        if (supplyCrate != null) {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fThere is a &dsupply crate&f spawned @ &d" + supplyCrate.getEndLocation().getBlockX() + ", " + supplyCrate.getEndLocation().getBlockY() + ", " + supplyCrate.getEndLocation().getBlockZ()));
        } else {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fThere is no supply crate spawned."));
        }
        sender.sendMessage(CC.translate(""));
    }

    @Subcommand("givesummoner")
    @CommandPermission("foxtrot.supplycrate")
    public static void giveSupplyCrate(CommandSender sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
        ItemStack stack = Samurai.getInstance().getSupplyDropHandler().getSupplyDropSummonerItem().clone();
        stack.setAmount(amount);
        InventoryUtils.tryFit(target.getPlayer().getInventory(), stack);
    }

    @Subcommand("setloot")
    @CommandPermission("foxtrot.supplycrate")
    public static void supplyCrateSave(Player sender) {

        Samurai.getInstance().getSupplyDropHandler().saveLoot(Arrays.asList(sender.getInventory().getStorageContents()));

        sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully saved your inventory as the supply drop loot."));
    }

    @Subcommand("addhandloot")
    @CommandPermission("foxtrot.supplycrate")
    public static void supplyCrateAdd(Player sender) {

        if (sender.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        Samurai.getInstance().getSupplyDropHandler().addLoot(sender.getItemInHand());

        sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully added your hand item to the supply drop loot."));
    }

    @Subcommand("addinvloot")
    @CommandPermission("foxtrot.supplycrate")
    public static void supplyCrateAddInv(Player sender) {

        Samurai.getInstance().getSupplyDropHandler().addLoot(Arrays.asList(sender.getInventory().getStorageContents()));

        sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully added your inventory to the supply drop loot."));
    }

    @Subcommand("forcespawn")
    @CommandPermission("foxtrot.supplycrate")
    public static void forceSpawn(Player sender) {

        if (Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate() != null) {
            sender.sendMessage(CC.translate("&aThere is an active supply crate already."));
            return;
        }

        if (sender.getLocation().getBlockY() < 85) {
            sender.sendMessage(CC.translate("&cYou need to be at least y level 125 to do this."));
            return;
        }

        Samurai.getInstance().getSupplyDropHandler().buildCrate(sender.getLocation());

        sender.sendMessage(CC.translate("&aSuccessfully spawned a supply drop!"));
    }

    @Subcommand("loot")
    public static void loottable(Player sender) {
        new SupplyCrateMenu().openMenu(sender);
    }

    @Subcommand("forceend|stop|forcestop")
    @CommandPermission("foxtrot.supplycrate")
    public static void forceEnd(CommandSender sender) {

        if (Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate() == null) {
            sender.sendMessage(CC.translate("&cThere is not an active supply crate."));
            return;
        }

        Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate().despawn(true);

        sender.sendMessage(CC.translate("&aSuccessfully ended & despawned the active supply drop!"));
    }

}
