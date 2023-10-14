package dev.lbuddyboy.samurai.custom.deepdark.entity.impl;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.deepdark.DeepDarkHandler;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkAbility;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LightningAbility extends DarkAbility {

    private long usedAt = System.currentTimeMillis();

    @Override
    public String getName() {
        return "lightning";
    }

    @Override
    public double getChance() {
        return 68;
    }

    @Override
    public void activate(Location location) {
        if (usedAt - System.currentTimeMillis() > 0) return;
        Warden warden = Samurai.getInstance().getDeepDarkHandler().getDarkEntity().getWarden();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(location) > 15) continue;

            player.damage(ThreadLocalRandom.current().nextDouble(2, 5), warden);
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 0));
            player.sendMessage(CC.translate("&3&l[DARK LIGHTNING] &fThe dark entity has used it's lightning ability!"));
        }

        usedAt = System.currentTimeMillis() + 4_000L;
    }

}
