package dev.lbuddyboy.samurai.custom.deepdark.entity.impl;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkAbility;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkEntity;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ShockwaveAbility extends DarkAbility {

    private long usedAt = System.currentTimeMillis();

    @Override
    public String getName() {
        return "shockwave";
    }

    @Override
    public double getChance() {
        return 56;
    }

    @Override
    public void activate(Location location) {
        if (usedAt - System.currentTimeMillis() > 0) return;

        Warden warden = Samurai.getInstance().getDeepDarkHandler().getDarkEntity().getWarden();
        warden.setVelocity(new Vector(
                warden.getLocation().getDirection().getX() * 2.15,
                warden.getLocation().getY() * 2.50,
                warden.getLocation().getDirection().getZ() * 2.15));

        for (Location l : getCircle(location, 10, 100)) {
            int random = ThreadLocalRandom.current().nextInt(100);
            if (random > 50) {
                l.getWorld().playEffect(location,Effect.STEP_SOUND, Material.SCULK);
            } else {
                l.getWorld().spawnParticle(Particle.FLAME, location, 16);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(location) > 15) continue;

            player.damage(ThreadLocalRandom.current().nextDouble(2, 5.5), warden);
            player.setVelocity(new Vector(
                    player.getLocation().getDirection().getX() * 1.35,
                    player.getLocation().getY() * 2.15,
                    player.getLocation().getDirection().getZ() * 1.35));

            player.sendMessage(CC.translate("&3&l[DARK SHOCKWAVE] &fThe dark entity has used it's shockwave ability!"));
        }
        usedAt = System.currentTimeMillis() + 8_000L;
    }

    public List<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));

            locations.add(new Location(world, x, center.getY(), z));
        }

        return locations;
    }
}
