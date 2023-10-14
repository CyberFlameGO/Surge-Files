package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cobwebs extends Killstreak {

    @Override
    public String getName() {
        return "Cobwebs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                20
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.COBWEB, 1));
    }

}
