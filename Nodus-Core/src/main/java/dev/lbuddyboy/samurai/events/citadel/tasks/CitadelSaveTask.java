package dev.lbuddyboy.samurai.events.citadel.tasks;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelSaveTask extends BukkitRunnable {

    public void run() {
        Samurai.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}