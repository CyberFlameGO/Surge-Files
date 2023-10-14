package dev.lbuddyboy.samurai.map.killstreaks.velttypes;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FireRes extends PersistentKillstreak {

    public FireRes() {
        super("Fire Resistance", 6);
    }

    public void apply(Player player) {
        player.getInventory().addItem(ItemBuilder.of(Material.POTION).data((short) 8227).build());
    }
    
}
