package dev.aurapvp.samurai.enchants.lore;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CustomLoreType {

    ARMOR_SET("PCORE_ASET", 10000),
    CUSTOM_ENCHANT("PCORE_CE", 9000),
    PROTECTED("PCORE_PROT", 8000),
    HOLY_PROTECTED("PCORE_HOLY", 7000),
    EXTRA_ENCHANT("PCORE_EXTRA_CE", 6000),
    CORRUPTED("PCORE_CORRUPT", 6000);

    final String invisibleId;
    final int priority;

    public int getPriority(CustomEnchant enchant) {
        if (enchant == null) return this.priority;

        return this.priority + enchant.getRarity().getWeight();
    }

    public static CustomLoreType getByInvisibleId(String id) {
        for (CustomLoreType type : values()) {
            if (type.getInvisibleId().equals(id)) return type;
        }
        return null;
    }

}
