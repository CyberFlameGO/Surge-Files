package dev.lbuddyboy.samurai.custom.pets;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public interface IPet {

    String getName();
    Config getConfig();
    String getHeadURL();
    String getDisplayName();
    PetRarity getPetRarity();
    List<String> getMenuLore();
    List<String> getLore();
    int getMaxLevel();
    long getCooldownTime(int level);
    Cooldown getCooldown();
    boolean isClickable();
    boolean isEnabled();
    void proc(Player player, int level);

    default ItemStack createPet(int level) {
        ItemStack stack = CC.getCustomSkull(getHeadURL());
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(CC.translate(getDisplayName()));
        meta.setLore(CC.translate(getLore(),
                "%level%", level,
                "%experience%", 0,
                "%needed-experience%", (level + 1) * 350
        ));

        stack.setItemMeta(meta);

        NBTItem item = new NBTItem(stack);

        item.setUUID("id", UUID.randomUUID());
        item.setString("pet", getName());
        item.setInteger("pet-level", level);
        item.setInteger("pet-experience", 0);

        return item.getItem();
    }

    default boolean isPet(ItemStack stack) {
        return new NBTItem(stack).hasTag("pet");
    }

    default boolean isThisPet(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;

        return isPet(stack) && new NBTItem(stack).getString("pet").equalsIgnoreCase(getName());
    }

    default int getLevel(ItemStack stack) {
        NBTItem item = new NBTItem(stack);

        return item.hasTag("pet-level") ? item.getInteger("pet-level") : -1;
    }

    default int getXP(ItemStack stack) {
        NBTItem item = new NBTItem(stack);

        return item.hasTag("pet-experience") ? item.getInteger("pet-experience") : getLevel(stack) * 350;
    }

    default ItemStack addXP(ItemStack stack, int xp) {
        return setXP(stack, xp + getXP(stack));
    }

    default ItemStack setXP(ItemStack stack, int xp) {
        NBTItem item = new NBTItem(stack);

        item.setInteger("pet-experience", xp);

        stack = item.getItem();
        ItemMeta meta = stack.getItemMeta();
        int level = getLevel(stack);

        meta.setLore(CC.translate(getLore(),
                "%level%", level,
                "%experience%", getXP(stack),
                "%needed-experience%", getNeededXP(stack)
        ));
        stack.setItemMeta(meta);

        if (getXP(stack) >= getNeededXP(stack)) return setLevel(stack, level + 1);

        return stack;
    }

    default ItemStack setLevel(ItemStack stack, int level) {
        NBTItem item = new NBTItem(stack);

        item.setInteger("pet-level", level);
        item.setInteger("pet-experience", 0);

        stack = item.getItem();
        ItemMeta meta = stack.getItemMeta();

        meta.setLore(CC.translate(getLore(),
                "%level%", level,
                "%experience%", 0,
                "%needed-experience%", getNeededXP(level)
        ));
        stack.setItemMeta(meta);


        return stack;
    }

    default int getNeededXP(ItemStack stack) {
        return (getLevel(stack) + 1) * 350;
    }

    default int getNeededXP(int level) {
        return (level + 1) * 350;
    }

}
