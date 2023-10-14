package dev.lbuddyboy.samurai.map.killstreaks.velttypes;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed2 extends PersistentKillstreak {

    public Speed2() {
        super("Speed 2", 12);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 1));
    }
    
}
