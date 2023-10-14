package dev.lbuddyboy.samurai.custom.pets.thread;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetThread extends Thread {

    private long lastErrored = System.currentTimeMillis(), lastCleaned;
    private final Cooldown update = new Cooldown();

    public PetThread() {
        super("Pet - Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!Feature.PETS.isDisabled()) {

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (this.update.onCooldown(player)) continue;

                        for (IPet pet : Samurai.getInstance().getPetHandler().getPets().values()) {
                            if (!pet.isEnabled()) continue;
                            if (pet.isClickable()) continue;

                            for (ItemStack content : player.getInventory().getContents()) {
                                if (content == null || content.getType() == Material.AIR) continue;
                                if (!pet.isThisPet(content)) continue;

                                Tasks.run(() -> pet.proc(player, pet.getLevel(content)));
                            }
                        }
                        this.update.applyCooldown(player, 5);
                    }
                    if (lastCleaned + 10_000L > System.currentTimeMillis()) continue;

                    lastCleaned = System.currentTimeMillis();
                    this.update.cleanUp();
                }
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
