/*
package dev.lbuddyboy.samurai.custom.pets.thread;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetUpdateThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    public PetUpdateThread() {
        super("PetUpdate - Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (IPet pet : Samurai.getInstance().getPetHandler().getPets().values()) {
                        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)
                            continue;
                        if (!pet.getCooldown().onCooldown(player) && !ItemUtils.getName(player.getItemInHand()).equalsIgnoreCase(CC.translate(pet.getDisplayName())) && pet.isThisPet(player.getItemInHand())) {
                            player.getInventory().setit(new ItemBuilder(player.getItemInHand())
                                    .setName(pet.getDisplayName())
                                    .create());
                            player.updateInventory();
                            continue;
                        }
                        if (!pet.getCooldown().onCooldown(player)) continue;
                        if (!pet.isThisPet(player.getItemInUse())) continue;

                        player.setItemInHand(new ItemBuilder(player.getItemInHand())
                                .setName(pet.getDisplayName() + " &7(" + pet.getCooldown().getRemaining(player) + ")")
                                .create());
                        player.updateInventory();
                    }
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
*/
