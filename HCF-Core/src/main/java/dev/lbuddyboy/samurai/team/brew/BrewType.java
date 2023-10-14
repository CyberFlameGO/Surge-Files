package dev.lbuddyboy.samurai.team.brew;

import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/08/2021 / 9:46 PM
 * HCTeams / dev.lbuddyboy.samurai.team.brew
 */

@AllArgsConstructor
@Getter
public enum BrewType {

    HEALTHII("Instant Splash Health Potion II", new ItemBuilder(Material.SPLASH_POTION, 1).hideAttributes().build(),
            Arrays.asList(Material.NETHER_WART, Material.GLISTERING_MELON_SLICE, Material.GLOWSTONE_DUST, Material.GUNPOWDER)
            , new PotionData(PotionType.INSTANT_HEAL, false, true)),
    INVISI("Invisibility Potion I", new ItemBuilder(Material.POTION, 1).hideAttributes().build(),
            Arrays.asList(Material.NETHER_WART, Material.GOLDEN_CARROT, Material.FERMENTED_SPIDER_EYE, Material.REDSTONE)
            , new PotionData(PotionType.INVISIBILITY, true, false)),
    FRES("Fire Resistance Potion I", new ItemBuilder(Material.POTION, 1).hideAttributes().build(),
            Arrays.asList(Material.NETHER_WART, Material.MAGMA_CREAM, Material.REDSTONE)
            , new PotionData(PotionType.FIRE_RESISTANCE, true, false)),
    SPEEDII("Speed Potion II", new ItemBuilder(Material.POTION, 1).hideAttributes().build(),
            Arrays.asList(Material.NETHER_WART, Material.SUGAR, Material.GLOWSTONE_DUST)
            , new PotionData(PotionType.SPEED, false, true));

    private final String id;
    private final ItemStack displayItem;
    private final List<Material> requiredMaterials;
    private final PotionData potionData;

}
