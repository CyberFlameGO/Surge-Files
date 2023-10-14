package dev.aurapvp.samurai.enchants.set;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.enchants.EnchantHandler;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ArmorSet implements Listener {

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract ChatColor getColor();
    public abstract Material getDisplayMaterial();
    public abstract List<String> getDescription();
    public abstract List<String> getLoreFormat();
    public abstract ItemStack getHelmet();
    public abstract ItemStack getChestplate();
    public abstract ItemStack getLeggings();
    public abstract ItemStack getBoots();
    public abstract ItemStack getWeapon();

    public void activate(Player player) {
        player.setMetadata(EnchantHandler.getARMOR_SET_TAG(), new FixedMetadataValue(Samurai.getInstance(), true));
        player.setMetadata(getName(), new FixedMetadataValue(Samurai.getInstance(), true));
        player.sendMessage(CC.translate(getColor() + "&l" + getDisplayName() + " SET &7- You have just &aactivated&f the " + getDisplayName() + " armor set."));
    }

    public void deactivate(Player player) {
        player.removeMetadata(getName(), Samurai.getInstance());
        player.removeMetadata(EnchantHandler.getARMOR_SET_TAG(), Samurai.getInstance());
        player.sendMessage(CC.translate(getColor() + "&l" + getDisplayName() + " SET &7-" + " You have just &cdeactivated&f the " + getDisplayName() + " armor set."));
    }

    public boolean hasOn(Player player) {
        return player.hasMetadata(getName());
    }

    public boolean hasSetOn(Player player) {
        int i = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) continue;
            if (hasSet(armor)) i++;
        }
        return i >= 4;
    }

    public boolean hasSet(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;
        NBTItem item = new NBTItem(stack);

        return item.hasTag(EnchantHandler.getARMOR_SET_TAG()) && item.getString(EnchantHandler.getARMOR_SET_TAG()).equalsIgnoreCase(getName());
    }

    public boolean hasWeapon(Player player) {
        return player.getItemInHand() != null
                && player.getItemInHand().hasItemMeta()
                && player.getItemInHand().getItemMeta().hasDisplayName()
                && player.getItemInHand().getItemMeta().getDisplayName().contains(getDisplayName());
    }

    public void reward(Player player) {
        ItemUtils.tryFit(player, getHelmet(), true);
        ItemUtils.tryFit(player, getChestplate(), true);
        ItemUtils.tryFit(player, getLeggings(), true);
        ItemUtils.tryFit(player, getBoots(), true);
        ItemUtils.tryFit(player, getWeapon(), false);
    }

}
