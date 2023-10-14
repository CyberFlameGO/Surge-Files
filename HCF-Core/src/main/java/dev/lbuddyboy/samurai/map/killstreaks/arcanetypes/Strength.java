package dev.lbuddyboy.samurai.map.killstreaks.arcanetypes;

import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Strength extends PersistentKillstreak {

    public Strength() {
        super("Strength", 75);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
    }
    
}