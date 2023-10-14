package dev.aurapvp.samurai.enchants.rarity;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IntRange;
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
        return Samurai.getInstance().getEnchantHandler().getEnchants().stream().filter(enchant -> enchant.getRarity() == this).collect(Collectors.toList());
    }

}
