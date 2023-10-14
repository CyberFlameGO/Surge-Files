package dev.lbuddyboy.hub.item;

import dev.lbuddyboy.hub.lHub;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 9:05 PM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item
 */
public class ItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() == null) return;

        if (event.getItem().getType() == Material.LEGACY_INK_SACK && player.hasMetadata("pvpmode")) {
            player.chat("/pvpmode");
            return;
        }

        for (Item item : lHub.getInstance().getItemHandler().getItems()) {
            if (!item.getStack().isSimilar(event.getItem())) continue;
            if (!item.getClicks().contains(event.getAction())) continue;

            lHub.getInstance().getCustomMenuHandler().performAction(item.getAction(), player, item.getVal());

            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

}
