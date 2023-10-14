package dev.lbuddyboy.samurai.custom.deepdark.entity.impl;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkAbility;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class LevitateAbility extends DarkAbility {

    private long usedAt = System.currentTimeMillis();

    @Override
    public String getName() {
        return "levitate";
    }

    @Override
    public double getChance() {
        return 35;
    }

    @Override
    public void activate(Location location) {
        if (usedAt - System.currentTimeMillis() > 0) return;
        Warden warden = Samurai.getInstance().getDeepDarkHandler().getDarkEntity().getWarden();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(location) > 15) continue;

            player.damage(ThreadLocalRandom.current().nextDouble(2, 5), warden);
            player.getWorld().createExplosion(player.getLocation(), 2.0f, false);
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 25, 0));
            player.sendMessage(CC.translate("&3&l[DARK LEVITATOR] &fThe dark entity has used it's levitation ability!"));
        }

        usedAt = System.currentTimeMillis() + 6_000L;
    }

}
