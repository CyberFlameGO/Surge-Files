package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@CommandAlias("fix|fixhand|repair")
@CommandPermission("foxtrot.fix")
public class FixCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        ItemStack stack = sender.getInventory().getItemInMainHand();
        if (stack.getType() != Material.AIR) {
            if (!unfixable.contains(stack.getType())) {
                stack.setDurability((short) 0);
            } else {
                sender.sendMessage(CC.translate("&cThat item is not allowed to be repaired."));
                return;
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "Repaired held item.");
    }

    private static List<Material> unfixable = Arrays.asList(Material.ELYTRA, Material.BOW);

}
