package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PotionRefillToken extends Killstreak {

    @Override
    public String getName() {
        return "Potion Refill Token";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                15, 35
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.NETHER_STAR).name("&c&lPotion Refill Token").setUnbreakable(true).setLore(ImmutableList.of("&cRight click this to fill your inventory with potions!")).build());
    }

}