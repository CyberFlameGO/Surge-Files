package dev.lbuddyboy.samurai.map.killstreaks.velttypes;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Strength extends PersistentKillstreak {

    public Strength() {
        super("Strength", 18);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 45*20, 1));
    }
    
}