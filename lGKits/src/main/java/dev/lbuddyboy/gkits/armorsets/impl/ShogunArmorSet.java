package dev.lbuddyboy.gkits.armorsets.impl;

import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShogunArmorSet extends ArmorSet {

    @Override
    public String getName() {
        return "shogun";
    }

    @Override
    public String getDisplayName() {
        return "Shogun";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GREEN;
    }

    @Override
    public ItemStack getHelmet() {
        return new ItemBuilder(Material.DIAMOND_HELMET)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&a&lShogun Helmet")
                .setLore(getDescription())
                .nbtString("armor-set", getName())
                .create();
    }

    @Override
    public ItemStack getChestplate() {
        return new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&a&lShogun Chestplate")
                .setLore(getDescription())
                .nbtString("armor-set", getName())
                .create();
    }

    @Override
    public ItemStack getLeggings() {
        return new ItemBuilder(Material.DIAMOND_LEGGINGS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&a&lShogun Leggings")
                .setLore(getDescription())
                .nbtString("armor-set", getName())
                .create();
    }

    @Override
    public ItemStack getBoots() {
        return new ItemBuilder(Material.DIAMOND_BOOTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.PROTECTION_FALL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&a&lShogun Boots")
                .setLore(getDescription())
                .nbtString("armor-set", getName())
                .create();
    }

    @Override
    public ItemStack getWeapon() {
        return new ItemBuilder(Material.DIAMOND_AXE)
                .addEnchant(Enchantment.DAMAGE_ALL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&a&lShogun Axe")
                .setLore(getDescription())
                .nbtString("armor-set", getName())
                .create();
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.DIAMOND_AXE;
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();

        lore.add("  ");
        lore.add(getColor() + "&l" + getDisplayName() + " Perks");
        lore.add("&fCritical hits are multiplied by 10%");
        lore.add("&fwhile wielding an axe.");
        lore.add("  ");

        return lore;
    }

    @Override
    public List<String> getLoreFormat() {
        List<String> lore = new ArrayList<>();

        lore.add("  ");
        lore.add(getColor() + "&l" + getDisplayName() + " Perks");
        lore.add("&fCritical hits are multiplied by 10%");
        lore.add("&fwhile wielding an axe.");

        return lore;
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        if (!hasOn(damager)) return;


        if (!damager.getInventory().getItemInMainHand().getType().name().contains("_AXE")) return;
        if (damager.getFallDistance() < 0.22777924) return;
        if (damager.getFallDistance() > 0.075444065) return;

        event.setDamage(event.getDamage() * 1.05D);
    }

}
