package dev.lbuddyboy.samurai.map.shards.menu.upgrades;

import dev.lbuddyboy.samurai.map.kits.upgrades.Upgrades;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Getter
public class UpgradableKit {
    private final String kitName;
    private final ItemStack icon;
    private final Map<Material, Upgrades> upgrades;
}
