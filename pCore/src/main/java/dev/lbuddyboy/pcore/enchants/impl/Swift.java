package dev.lbuddyboy.pcore.enchants.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Swift extends CustomEnchant implements Listener {

    private static final Cooldown cooldown = new Cooldown();
    private String procMessage = "&3&l[SWIFT] &bYour swift custom enchantment has just proced.";

    @Override
    public void init() {
        registerListener(this);
        if (getConfig().contains("activated-message")) {
            this.procMessage = getConfig().getString("activated-message");
            return;
        }

        getConfig().set("activated-message", "&3&l[SWIFT] &bYour swift custom enchantment has just proced.");
    }
    @Override
    public String getName() {
        return "swift";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will have a chance to receive",
                "&fspeed when at low health!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Swift");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&6");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-2").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return pCore.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Legendary")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 50.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Collections.singletonList("BOOTS");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.getHealth() > 7) return;
        if (!hasEnchantApplied(player)) return;
        if (RANDOM.nextDouble(100) > 40) return;
        int level = player.getMetadata(META_DATA()).get(0).asInt();

        player.sendMessage(CC.translate(this.procMessage));
        player.playEffect(player.getLocation(), Effect.CLOUD, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (level + 1), level));
        cooldown.applyCooldown(player, 30);
    }

}
