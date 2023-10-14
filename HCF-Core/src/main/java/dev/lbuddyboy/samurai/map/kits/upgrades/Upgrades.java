package dev.lbuddyboy.samurai.map.kits.upgrades;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Upgrades {
    private final List<Enchantment> enchantmentList = new ArrayList<>();
    private final List<String> customEnchantmentList = new ArrayList<>();

    public Upgrades protection() {
        enchantmentList.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        return this;
    }

    public Upgrades sharpness() {
        enchantmentList.add(Enchantment.DAMAGE_ALL);
        return this;
    }

    public Upgrades power() {
        enchantmentList.add(Enchantment.ARROW_DAMAGE);
        return this;
    }

    public Upgrades piercing() {
        enchantmentList.add(Enchantment.PIERCING);
        return this;
    }

    public Upgrades impaling() {
        enchantmentList.add(Enchantment.IMPALING);
        return this;
    }

    public Upgrades riptide() {
        enchantmentList.add(Enchantment.RIPTIDE);
        return this;
    }

    public Upgrades depth() {
        enchantmentList.add(Enchantment.RIPTIDE);
        return this;
    }

    public Upgrades infinity() {
        enchantmentList.add(Enchantment.ARROW_INFINITE);
        return this;
    }

    public Upgrades efficiency() {
        enchantmentList.add(Enchantment.DIG_SPEED);
        return this;
    }

    public Upgrades fireResistance() {
        customEnchantmentList.add("Inferno");
        return this;
    }

    public Upgrades repair() {
        customEnchantmentList.add("Repair");
        return this;
    }

    public Upgrades saturation() {
        customEnchantmentList.add("Replenish");
        return this;
    }

    public Upgrades recover() {
        customEnchantmentList.add("Recover");
        return this;
    }

    public Upgrades speed() {
        customEnchantmentList.add("Speed");
        return this;
    }
}
