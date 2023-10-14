package dev.lbuddyboy.samurai.server.threads;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearLagTask extends BukkitRunnable {

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClasses(Item.class)) {
                if (!(entity instanceof Item item)) continue;
                if (item.getTicksLived() < (20 * 30)) continue;

                item.remove();
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) continue;
                if (!(entity instanceof Monster) && !(entity instanceof Animals)) continue;
                if (entity.getTicksLived() < (20 * 30)) continue;
                if (entity instanceof Warden) continue;

                entity.remove();
            }
        }
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCooldown().cleanUp();
    }
}
