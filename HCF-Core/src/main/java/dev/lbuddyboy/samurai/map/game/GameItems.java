package dev.lbuddyboy.samurai.map.game;

import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class GameItems {

    public static ItemStack LEAVE_EVENT = ItemBuilder.of(Material.RED_DYE).name(ChatColor.RED.toString() + ChatColor.BOLD + "Leave Event").build();
    public static ItemStack VOTE_FOR_ARENA = ItemBuilder.of(Material.ENCHANTED_BOOK).name(ChatColor.GRAY.toString() + "» " + ChatColor.GOLD + ChatColor.BOLD + "Map Vote" + ChatColor.GRAY + " «").build();

}
