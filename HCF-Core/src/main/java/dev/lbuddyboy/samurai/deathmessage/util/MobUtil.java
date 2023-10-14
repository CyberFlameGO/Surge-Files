package dev.lbuddyboy.samurai.deathmessage.util;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

    public static String getItemName(ItemStack itemStack, boolean stripColor) {
        if (itemStack.getItemMeta().hasDisplayName()) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            return stripColor ? ChatColor.stripColor(displayName) : displayName;
        }

        return (WordUtils.capitalizeFully(itemStack.getType().name().replace('_', ' ')));
    }

    public static String getItemName(Material material) {
        return (WordUtils.capitalizeFully(material.name().replace('_', ' ')));
    }

}