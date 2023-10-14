package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed2 extends PersistentKillstreak {

    public Speed2() {
        super("Speed 2", 25);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
}
