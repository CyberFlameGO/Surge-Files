package dev.lbuddyboy.pcore.pets.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetUpdateThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    @Override
    public void run() {
        while (pCore.getInstance().isEnabled()) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (IPet pet : pCore.getInstance().getPetHandler().getPets().values()) {
                        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) continue;
                        if (!pet.getCooldown().onCooldown(player) && !ItemUtils.getName(player.getItemInHand()).equalsIgnoreCase(CC.translate(pet.getDisplayName())) && pet.isThisPet(player.getItemInHand())) {
                            player.setItemInHand(new ItemBuilder(player.getItemInHand())
                                    .setName(pet.getDisplayName())
                                    .create());
                            player.updateInventory();
                            continue;
                        }
                        if (!pet.getCooldown().onCooldown(player)) continue;
                        if (!pet.isThisPet(player.getItemInHand())) continue;

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
