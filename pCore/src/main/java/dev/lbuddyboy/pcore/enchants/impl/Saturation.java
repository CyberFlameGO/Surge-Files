package dev.lbuddyboy.pcore.enchants.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Saturation extends CustomEnchant implements Listener {

    @Override
    public void init() {
        registerListener(this);
    }

    @Override
    public String getName() {
        return "saturation";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will NOT need to eat!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Saturation");
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

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!hasEnchantApplied(player)) return;

        event.setFoodLevel(event.getFoodLevel() + 1);
    }

}
