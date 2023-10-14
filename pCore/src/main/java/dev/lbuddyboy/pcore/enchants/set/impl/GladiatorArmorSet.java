package dev.lbuddyboy.pcore.enchants.set.impl;

import dev.lbuddyboy.pcore.enchants.EnchantHandler;
import dev.lbuddyboy.pcore.enchants.set.ArmorSet;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GladiatorArmorSet extends ArmorSet {

    @Override
    public String getName() {
        return "gladiator";
    }

    @Override
    public String getDisplayName() {
        return "Gladiator";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public ItemStack getHelmet() {
        ItemStack stack = new ItemBuilder(Material.DIAMOND_HELMET)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&6&lGladiator Helmet")
                .nbtString(EnchantHandler.getARMOR_SET_TAG(), getName())
                .create();

        return pCore.getInstance().getEnchantHandler().setArmorSet(stack, this);
    }

    @Override
    public ItemStack getChestplate() {
        ItemStack stack = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&6&lGladiator Chestplate")
                .nbtString(EnchantHandler.getARMOR_SET_TAG(), getName())
                .create();

        return pCore.getInstance().getEnchantHandler().setArmorSet(stack, this);
    }

    @Override
    public ItemStack getLeggings() {
        ItemStack stack = new ItemBuilder(Material.DIAMOND_LEGGINGS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&6&lGladiator Leggings")
                .nbtString(EnchantHandler.getARMOR_SET_TAG(), getName())
                .create();

        return pCore.getInstance().getEnchantHandler().setArmorSet(stack, this);
    }

    @Override
    public ItemStack getBoots() {
        ItemStack stack = new ItemBuilder(Material.DIAMOND_BOOTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addEnchant(Enchantment.PROTECTION_FALL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&6&lGladiator Boots")
                .nbtString(EnchantHandler.getARMOR_SET_TAG(), getName())
                .create();

        return pCore.getInstance().getEnchantHandler().setArmorSet(stack, this);
    }

    @Override
    public ItemStack getWeapon() {
        ItemStack stack = new ItemBuilder(Material.DIAMOND_AXE)
                .addEnchant(Enchantment.DAMAGE_ALL, 5)
                .addEnchant(Enchantment.DURABILITY, 3)
                .setName("&6&lGladiator Axe")
                .nbtString(EnchantHandler.getARMOR_SET_TAG(), getName())
                .create();

        return pCore.getInstance().getEnchantHandler().setArmorSet(stack, this);
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
        lore.add("&fEnjoy a 10% critical damage multiplier while wielding an axe!");
        lore.add("&fEnjoy 5% damage resistance in this armor set!");
        lore.add("  ");

        return lore;
    }

    @Override
    public List<String> getLoreFormat() {
        List<String> lore = new ArrayList<>();

        lore.add("  ");
        lore.add(getColor() + "&l" + getDisplayName() + " Perks");
        lore.add("&fEnjoy a 10% critical damage multiplier while wielding an axe!");
        lore.add("&fEnjoy 5% damage resistance in this armor set!");

        return lore;
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();

        if (!hasOn(damager)) return;


        if (!damager.getInventory().getItemInHand().getType().name().contains("_AXE")) return;
        if (damager.getFallDistance() < 0.22777924) return;
        if (damager.getFallDistance() > 0.075444065) return;

        event.setDamage(event.getDamage() * 1.05D);
    }

}
