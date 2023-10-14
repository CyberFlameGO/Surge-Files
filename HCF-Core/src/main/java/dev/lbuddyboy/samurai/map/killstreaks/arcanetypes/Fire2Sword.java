package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Fire2Sword extends Killstreak {

    @Override
    public String getName() {
        return "Fire II sword";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                100
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 1).name("&b&c100 Killstreak Sword").build());
    }

}
