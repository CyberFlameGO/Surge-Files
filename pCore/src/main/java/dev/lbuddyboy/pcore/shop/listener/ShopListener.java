package dev.lbuddyboy.pcore.shop.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.menu.purchase.PurchaseMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ShopUtils;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ShopListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!(player.hasMetadata("change_amount"))) return;

        PurchaseMenu menu = (PurchaseMenu) player.getMetadata("change_amount").get(0).value();
        event.setCancelled(true);

        if (event.getMessage().contains("cancel")) {
            menu.openMenu(player);
            player.removeMetadata("change_amount", pCore.getInstance());
            player.sendMessage(CC.translate("&cProcess cancelled."));
            return;
        }

        try {
            String action = menu.sell ? "sell" : "purchase";

            if (menu.sell && event.getMessage().equalsIgnoreCase("all")) {
                menu.setAmount(Integer.MAX_VALUE);
                player.sendMessage(CC.translate("&aPlease type 'confirm' if you want to sell ALL " + menu.item.getDisplayName() + "."));
            } else {
                menu.setAmount(Integer.parseInt(event.getMessage()));
                player.sendMessage(CC.translate("&aPlease type 'confirm' if you want to " + action + " " + menu.amount + "x " + menu.item.getDisplayName() + "."));
            }

            player.removeMetadata("change_amount", pCore.getInstance());
            Tasks.run(() -> player.setMetadata("change_amount_confirm", new FixedMetadataValue(pCore.getInstance(), menu)));
        } catch (Exception ignored) {
            player.sendMessage(CC.translate("&cInvalid amount. Try again."));
        }

    }

    @EventHandler
    public void onChatConfirm(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!(player.hasMetadata("change_amount_confirm"))) return;

        PurchaseMenu menu = (PurchaseMenu) player.getMetadata("change_amount_confirm").get(0).value();
        event.setCancelled(true);

        if (event.getMessage().contains("cancel")) {
            menu.amount = -1;
            menu.openMenu(player);
            player.removeMetadata("change_amount_confirm", pCore.getInstance());
            player.sendMessage(CC.translate("&cProcess cancelled."));
            return;
        }

        Tasks.run(() -> {
            ShopUtils.process(player, menu.item, menu.amount, menu.sell);
            menu.amount = -1;
            menu.openMenu(player);
            player.removeMetadata("change_amount_confirm", pCore.getInstance());
        });
    }
}
