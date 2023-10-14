package dev.lbuddyboy.samurai.custom.teamwar.model;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
@Getter
public enum WarKit {

    DIAMOND("Diamond", Material.DIAMOND_CHESTPLATE),
    ARCHER("Archer", Material.LEATHER_CHESTPLATE),
    BARD("Bard", Material.GOLDEN_CHESTPLATE),
    HUNTER("Hunter", Material.TURTLE_HELMET),
    ROGUE("Rogue", Material.GOLDEN_SWORD);

    private final String kitName;
    private final Material material;

    public void loadKit(Player player) {
        DefaultKit pvpKit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(this.kitName);
        InventoryUtils.resetInventoryNow(player);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        pvpKit.apply(player);
    }

}
