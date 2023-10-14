package dev.lbuddyboy.samurai.map.killstreaks.velttypes;

import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Killstreak {

    @Override
    public String getName() {
        return "God Apple";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                21
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
    }

}
