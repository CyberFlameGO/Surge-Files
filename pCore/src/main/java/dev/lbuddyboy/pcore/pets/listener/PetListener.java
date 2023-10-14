package dev.lbuddyboy.pcore.pets.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.api.event.PetActivateEvent;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PetListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        for (IPet pet : pCore.getInstance().getPetHandler().getPets().values()) {
            if (!pet.isClickable()) continue;
            if (!pet.isThisPet(item)) continue;

            event.setCancelled(true);

            if (pet.getCooldown().onCooldown(player)) continue;

            Bukkit.getPluginManager().callEvent(new PetActivateEvent(player, pet));
            pet.proc(player, pet.getLevel(item));
            player.setItemInHand(pet.addXP(item, ThreadLocalRandom.current().nextInt(100, 500)));
            player.sendMessage(CC.translate("&6&lPETS &7" + CC.UNICODE_ARROW_RIGHT + " &aYou have just used the " + pet.getDisplayName() + "&a!"));
            break;
        }
    }

}
