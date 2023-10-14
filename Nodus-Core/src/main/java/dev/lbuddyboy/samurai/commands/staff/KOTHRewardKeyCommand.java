package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@CommandAlias("kothrewardkey|kothkey")
@CommandPermission("op")
public class KOTHRewardKeyCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void kothRewardKey(Player sender, @Name("player") OnlinePlayer player, @Name("koth") String koth, @Name("amount") int amount) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate player.");
            return;
        }

        if (amount == 0 || 32 < amount) {
            sender.sendMessage(ChatColor.RED + "Illegal amount! Must be between 1 and 32.");
            return;
        }

        ItemStack stack = InventoryUtils.generateKOTHRewardKey(koth);
        stack.setAmount(amount);
        Map<Integer, ItemStack> failed = player.getPlayer().getInventory().addItem(stack);

        String msg;
        if (amount == 1) {
            msg = ChatColor.YELLOW + "Gave " + player.getPlayer().getName() + " a KOTH reward key." + (failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.");
        } else {
            msg = ChatColor.YELLOW + "Gave " + player.getPlayer().getName() + " " + amount + " KOTH reward keys." + (failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.");
        }
        org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
    }

}