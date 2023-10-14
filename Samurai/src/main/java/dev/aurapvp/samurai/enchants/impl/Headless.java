package dev.aurapvp.samurai.enchants.impl;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IntRange;
import dev.aurapvp.samurai.util.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Headless extends CustomEnchant {

    @Override
    public String getName() {
        return "headless";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will receive a better chance to",
                "&fobtain their head upon killing them!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Headless");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&c");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-5").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return Samurai.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Supreme")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 50.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Arrays.asList("SWORD", "AXE", "BOW");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @Override
    public void apply(Player player, int level) {
    }

    @Override
    public void unApply(Player player) {
    }

}
