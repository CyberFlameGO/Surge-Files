package dev.lbuddyboy.samurai.pvpclasses.pvpclasses.mage;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class MageEffect {

    @Getter private PotionEffect potionEffect;
    @Getter private int energy;

    // For the message we send when you select the (de)buff in your hotbar.
    @Getter private Map<String, Long> lastMessageSent = new HashMap<>();

    public static MageEffect fromPotion(PotionEffect potionEffect) {
        return (new MageEffect(potionEffect, -1));
    }

    public static MageEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
        return (new MageEffect(potionEffect, energy));
    }

    public static MageEffect fromEnergy(int energy) {
        return (new MageEffect(null, energy));
    }

    private MageEffect(PotionEffect potionEffect, int energy) {
        this.potionEffect = potionEffect;
        this.energy = energy;
    }

}