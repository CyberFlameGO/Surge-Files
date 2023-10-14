package dev.lbuddyboy.vouchers.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.vouchers.lVouchers;
import dev.lbuddyboy.vouchers.object.Voucher;
import dev.lbuddyboy.vouchers.util.CC;
import dev.lbuddyboy.vouchers.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:44 PM
 * GKits / me.lbuddyboy.gkits.listener
 */
public class VoucherListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        Player player = event.getPlayer();

        if (stack == null) return;
        if (stack.getAmount() < 1) return;
        if (stack.getType() == Material.AIR) return;
        if (player.getItemInHand() == null) return;
        if (!player.getItemInHand().isSimilar(stack)) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("voucher")) return;
//        if (event.getHand() != EquipmentSlot.HAND) return;

        Voucher voucher = lVouchers.getInstance().getVoucher().get(item.getString("voucher"));

        if (voucher == null) {
            player.sendMessage(CC.translate("&cThis voucher does not exist anymore, if this is a bug contact an admin."));
            return;
        }

        if (!lVouchers.getInstance().getApi().attemptUse(player, voucher)) return;

        for (String command : voucher.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%voucher%", voucher.getName())
            );
        }

        player.getInventory().setItemInHand(ItemUtils.takeItem(stack));
    }

}
