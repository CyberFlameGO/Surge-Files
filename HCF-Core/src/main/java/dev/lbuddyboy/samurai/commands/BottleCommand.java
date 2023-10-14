package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.ExperienceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.Collections;

@CommandAlias("bottle")
@CommandPermission("foxtrot.bottle")
public final class BottleCommand extends BaseCommand implements Listener {

    public BottleCommand() {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
    }
    
    @Default
    public static void bottle(Player sender) {
        ItemStack item = sender.getItemInHand();

        if (item == null || item.getType() != Material.GLASS_BOTTLE || item.getAmount() != 1) {
            sender.sendMessage(ChatColor.RED + "You must be holding one glass bottle in your hand.");
            return;
        }

        ExperienceManager manager = new ExperienceManager(sender);
        int experience = manager.getCurrentExp();
        manager.setExp(0.0D);

        if (experience == 0) {
            sender.sendMessage(ChatColor.RED + "You don't have any experience to bottle!");
            return;
        }

        ItemStack result = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = result.getItemMeta();
        meta.setLore(Collections.singletonList(
                ChatColor.BLUE + "XP: " + ChatColor.WHITE + NumberFormat.getInstance().format(experience)
        ));
        result.setItemMeta(meta);

        sender.setItemInHand(result);
        sender.sendMessage(ChatColor.GREEN + "You have bottled " + NumberFormat.getInstance().format(experience) + " XP!");
        sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

}