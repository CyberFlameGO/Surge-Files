package dev.lbuddyboy.samurai.server.threads;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class PreSOTWTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!Feature.PEARL_STASIS.isDisabled()) {
            World world = Bukkit.getWorlds().get(0);
            for (Entity entity : world.getEntitiesByClasses(EnderPearl.class)) {
                if (entity.getTicksLived() >= 15 * 20) {
                    try {
                        entity.remove();
                    } catch (Exception ignored) {

                    }
                }
            }
        }
    }
}
