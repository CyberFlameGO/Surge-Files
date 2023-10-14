package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@CommandAlias("fixall|fixinv")
@CommandPermission("foxtrot.fixall")
public class FixAllCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        HashSet<ItemStack> toRepair = new HashSet<>();

        PlayerInventory targetInventory = sender.getInventory();
        toRepair.addAll(Arrays.asList(targetInventory.getStorageContents()));
        toRepair.addAll(Arrays.asList(targetInventory.getArmorContents()));

        for (ItemStack stack : toRepair) {
            if (stack != null && stack.getType() != Material.AIR) {
                if (!unfixable.contains(stack.getType())) {
                    stack.setDurability((short) 0);
                }
            }
        }

        sender.sendMessage(ChatColor.YELLOW + "Repaired your inventory.");
    }

    private static List<Material> unfixable = Arrays.asList(Material.ELYTRA, Material.BOW);

}
