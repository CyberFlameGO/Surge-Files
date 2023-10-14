package dev.lbuddyboy.pcore.enchants.model;

import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.lore.CustomLoreType;
import dev.lbuddyboy.pcore.enchants.set.ArmorSet;
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
