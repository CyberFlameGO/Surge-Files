package dev.lbuddyboy.pcore.enchants.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bat extends CustomEnchant {

    @Override
    public String getName() {
        return "bat";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will receive night vision!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Bat");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&7");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-3").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return pCore.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Common")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 50.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Collections.singletonList("HELMET");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @Override
    public void apply(Player player, int level) {
        super.apply(player, level);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, level - 1));
    }

    @Override
    public void unApply(Player player) {
        super.unApply(player);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}
