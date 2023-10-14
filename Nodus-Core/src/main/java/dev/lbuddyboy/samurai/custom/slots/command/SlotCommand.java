package dev.lbuddyboy.samurai.custom.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.slots.SlotConstants;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.menu.LootTablePreviewMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandAlias("roll")
public class SlotCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        Inventory inventory = Bukkit.createInventory(null, Samurai.getInstance().getSlotHandler().getConfig().getInt("slot-tickets.inventory-size"), "Ticket Master");

        for (int slot : Samurai.getInstance().getSlotHandler().getRoll_slots()) {
            inventory.setItem(slot - 1, SlotConstants.NONE);
        }

        inventory.setItem(Samurai.getInstance().getSlotHandler().getConfig().getInt("slot-tickets.start-roll-slot") - 1, SlotConstants.ROLL_BUTTON);

        sender.openInventory(inventory);
    }

    @Subcommand("loot")
    public static void loot(Player sender) {
        new LootTablePreviewMenu(Samurai.getInstance().getSlotHandler().getLootTable(), (Menu) null).openMenu(sender);
    }

    @Subcommand("giveticket")
    @CommandPermission("op")
    public static void give(CommandSender sender, @Name("player") OnlinePlayer player, @Name("amount") int amount) {
        Player target = player.getPlayer();

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlease provide a valid player."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            ItemUtils.tryFit(target, Samurai.getInstance().getSlotHandler().getItem().clone(), false);
        }
    }

}
