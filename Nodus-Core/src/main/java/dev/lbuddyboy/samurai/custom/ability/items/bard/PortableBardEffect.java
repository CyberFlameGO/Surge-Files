package dev.lbuddyboy.samurai.custom.ability.items.bard;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.RomanUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public final class PortableBardEffect {

    private final ItemStack stack;
    private final PotionEffectType potionEffectType;
    private final int effectTime; // in seconds
    private final int boostedTime; // in seconds
    private final int amplifier; // in seconds
//    private final String niceName;

    public PortableBardEffect(Material material, PotionEffectType potionEffectType, int effectTime, int boostedTime, int amplifier) {
        this.stack = new ItemStack(material);
        this.potionEffectType = potionEffectType;
//        this.niceName = convertName(potionEffectType);
        this.effectTime = effectTime;
        this.boostedTime = boostedTime;
        this.amplifier = amplifier;
    }

    public PortableBardEffect(ItemStack stack, PotionEffectType potionEffectType, int effectTime, int boostedTime, int amplifier) {
        this.stack = stack;
        this.potionEffectType = potionEffectType;
//        this.niceName = convertName(potionEffectType);
        this.effectTime = effectTime;
        this.boostedTime = boostedTime;
        this.amplifier = amplifier - 1;
    }

    public ItemStack toItemStack() {
        return this.stack;
    }

    public PotionEffect getPotionEffect() {
        int duration = SOTWCommand.isPartnerPackageHour() ? this.boostedTime : this.effectTime;
        return new PotionEffect(potionEffectType, 20 * duration, this.amplifier);
    }

    public String convertName(PotionEffectType potionEffectType) {
        if (potionEffectType == PotionEffectType.INCREASE_DAMAGE) {
            return "Strength " + RomanUtil.mapInt.get(amplifier);
        } else if (potionEffectType == PotionEffectType.DAMAGE_RESISTANCE) {
            return "Resistance III";
        } else if (potionEffectType == PotionEffectType.SPEED) {
            return "Speed III";
        } else if (potionEffectType == PotionEffectType.JUMP) {
            return "Jump Boost VII";
        } else if (potionEffectType == PotionEffectType.REGENERATION) {
            return "Regeneration III";
        } else if (potionEffectType == PotionEffectType.FIRE_RESISTANCE) {
            return "Fire Resistance";
        } else if (potionEffectType == PotionEffectType.INVISIBILITY) {
            return "Invisibility";
        } else if (potionEffectType == PotionEffectType.FAST_DIGGING) {
            return "Haste";
        } else if (potionEffectType == PotionEffectType.SLOW_DIGGING) {
            return "Fatigue";
        } else if (potionEffectType == PotionEffectType.SLOW) {
            return "Slowness";
        } else {
            return potionEffectType.getName();
        }
    }

}