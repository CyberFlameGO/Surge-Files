package dev.lbuddyboy.gkits.armorsets.impl;

import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DistanceArcherArmorSet extends ArmorSet {

    private static List<PotionEffect> EFFECTS = Arrays.asList(
            new PotionEffect(PotionEffectType.SLOW, 20 * 3, 0),
            new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 0),
            new PotionEffect(PotionEffectType.POISON, 20 * 3, 0),
            new PotionEffect(PotionEffectType.LEVITATION, 20 * 3, 0)
    );

    @Override
    public String getName() {
        return "distanced_archer";
    }

    @Override
    public String getDisplayName() {
        return "Distanced Archer";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.BOW;
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();

        lore.add("  ");
        lore.add(getColor() + "&l" + getDisplayName() + " Perks");
        lore.add("&fIf you shoot a player from 20+ blocks it will add");
        lore.add("&fa random effect from the following: Slowness, Blindness, Poison, or Levitation.");
        lore.add("  ");

        return lore;
    }

    @Override
    public List<String> getLoreFormat() {
        List<String> lore = new ArrayList<>();

        lore.add("  ");
        lore.add(getColor() + "&l" + getDisplayName() + " Perks");
        lore.add("&f if  you shoot a player from 20+ blocks it will add");
        lore.add("&fa random effect from the following: Slow, Blind, Poison, or Levitation.");

        return lore;
    }

    @Override
    public ItemStack getHelmet() {
        return new ItemBuilder(Material.LEATHER_HELMET)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .nbtString("armor-set", getName())
                .setName("&e&lDistanced Archer Helmet")
                .setLore(getDescription())
                .create();
    }

    @Override
    public ItemStack getChestplate() {
        return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .nbtString("armor-set", getName())
                .setName("&e&lDistanced Archer Chestplate")
                .setLore(getDescription())
                .create();
    }

    @Override
    public ItemStack getLeggings() {
        return new ItemBuilder(Material.LEATHER_LEGGINGS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.DURABILITY, 3)
                .nbtString("armor-set", getName())
                .setName("&e&lDistanced Archer Leggings")
                .setLore(getDescription())
                .create();
    }

    @Override
    public ItemStack getBoots() {
        return new ItemBuilder(Material.LEATHER_BOOTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchant(Enchantment.PROTECTION_FALL, 4)
                .addEnchant(Enchantment.DURABILITY, 3)
                .nbtString("armor-set", getName())
                .setName("&e&lDistanced Archer Boots")
                .setLore(getDescription())
                .create();
    }

    @Override
    public ItemStack getWeapon() {
        return new ItemBuilder(Material.BOW)
                .addEnchant(Enchantment.ARROW_DAMAGE, 5)
                .addEnchant(Enchantment.ARROW_INFINITE, 1)
                .addEnchant(Enchantment.DURABILITY, 3)
                .nbtString("armor-set", getName())
                .setName("&e&lDistanced Archer Bow")
                .setLore(getDescription())
                .create();
    }

    @EventHandler
    public void onDamaged(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!hasOn(player)) return;
        if (!hasSet(player.getInventory().getItemInMainHand())) return;

        event.getProjectile().setMetadata(getName(), new FixedMetadataValue(Samurai.getInstance(), player.getLocation()));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player shooter)) return;

        if (!arrow.hasMetadata(getName())) return;
        Location shotFrom = (Location) arrow.getMetadata(getName()).get(0).value();
        if (shotFrom == null) return;
        if (!lGKits.getInstance().getApi().attemptHit(shooter, victim)) return;

        if (shotFrom.distance(victim.getLocation()) < 20.0) {
            shooter.sendMessage(CC.translate("&cTried to shoot player from a distance for a random negative effect, but you're not far enough away."));
            return;
        }

        victim.addPotionEffect(EFFECTS.get(ThreadLocalRandom.current().nextInt(0, EFFECTS.size())));
        shooter.sendMessage(CC.translate("&c"));

    }

}
