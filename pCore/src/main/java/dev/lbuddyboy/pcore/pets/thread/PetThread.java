package dev.lbuddyboy.pcore.pets.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetThread extends Thread {

    private long lastErrored = System.currentTimeMillis();
    private final Cooldown update = new Cooldown();

    @Override
    public void run() {
        while (pCore.getInstance().isEnabled()) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (this.update.onCooldown(player)) continue;

                    for (IPet pet : pCore.getInstance().getPetHandler().getPets().values()) {
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
