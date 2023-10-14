package dev.aurapvp.samurai.enchants.model;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.enchants.lore.CustomLoreType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CustomLoreLine {

    private final String value;
    private final CustomLoreType type;
    private final int index, priority;

    private CustomEnchant enchant;
    private int level;
    private boolean holyWhiteScroll, whiteScroll, extraEnchant, armorSet;

}
