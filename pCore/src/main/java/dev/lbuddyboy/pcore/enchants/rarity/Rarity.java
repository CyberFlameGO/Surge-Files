package dev.lbuddyboy.pcore.enchants.rarity;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.util.IntRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class Rarity {

    private String name, displayName, color;
    private ItemStack openItem;
    private int weight, xpNeeded;
    private IntRange successRange, destroyRange;

    public List<CustomEnchant> getEnchants() {
        return pCore.getInstance().getEnchantHandler().getEnchants().stream().filter(enchant -> enchant.getRarity() == this).collect(Collectors.toList());
    }

}
