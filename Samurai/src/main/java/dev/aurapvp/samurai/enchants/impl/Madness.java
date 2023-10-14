package dev.aurapvp.samurai.enchants.impl;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IntRange;
import dev.aurapvp.samurai.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class Madness extends CustomEnchant implements Listener {

    @Override
    public void init() {
        registerListener(this);
    }

    @Override
    public String getName() {
        return "madness";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will receive Strength I for",
                "&f5 seconds upon killing a player!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Madness");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&6");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-1").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return Samurai.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Legendary")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 15.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        if (!hasEnchantApplied(killer)) return;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5 + 10, 0));
    }

}
