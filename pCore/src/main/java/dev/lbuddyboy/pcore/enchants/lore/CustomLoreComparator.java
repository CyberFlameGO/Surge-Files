package dev.lbuddyboy.pcore.enchants.lore;

import dev.lbuddyboy.pcore.enchants.model.CustomLoreLine;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@NoArgsConstructor
public class CustomLoreComparator implements Comparator<CustomLoreLine> {

    private boolean sortEnchants = false;

    public CustomLoreComparator(boolean sortEnchants) {
        this.sortEnchants = sortEnchants;
    }

    @Override
    public int compare(CustomLoreLine o1, CustomLoreLine o2) {
        int o1Priority = o1.getPriority(), o2Priority = o2.getPriority();

        if (sortEnchants) {
            if (o1.getEnchant() != null) {
                o1Priority = o1.getType().getPriority(o1.getEnchant());
            }

            if (o2.getEnchant() != null) {
                o2Priority = o2.getType().getPriority(o2.getEnchant());
            }
        }

        return Integer.compare(o1Priority, o2Priority);
    }

}
