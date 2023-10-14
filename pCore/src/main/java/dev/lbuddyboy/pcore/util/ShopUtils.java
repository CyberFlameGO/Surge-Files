package dev.lbuddyboy.pcore.util;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

    public static void process(Player player, ShopItem item, int amount, boolean sell) {
        ItemStack stack = new ItemStack(item.getData().getItemType(), amount, item.getData().getData());

        if (!sell) {
            boolean success = pCore.getInstance().getEconomyHandler().getEconomy().removeMoney(
                    player.getUniqueId(),
                    item.getPrice(amount, false),
                    IEconomy.EconomyChange.builder()
                            .predicate(() -> player.getInventory().firstEmpty() != -1)
                            .forced(false).build());

            if (!success) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(CC.translate("&cInvalid inventory space. Free up some slots."));
                    return;
                }
                player.sendMessage(CC.translate("&cInsufficient funds."));
                return;
            }

            ItemUtils.tryFit(player, stack, false);
            return;
        }
        boolean success = player.getInventory().contains(stack, stack.getAmount());
        stack.setAmount(1);

        if (amount == Integer.MAX_VALUE) {
            success = true;
            amount = 0;
            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null || content.getType() == Material.AIR) continue;
                if (!content.isSimilar(stack)) continue;

                amount += content.getAmount();
            }
        }

        boolean finalSuccess = success;
        pCore.getInstance().getEconomyHandler().getEconomy().addMoney(
                player.getUniqueId(),
                item.getPrice(amount, true),
                IEconomy.EconomyChange.builder()
                        .predicate(() -> finalSuccess)
                        .forced(true).build());

        if (!success) {
            player.sendMessage(CC.translate("&cYou do not have that many " + item.getDisplayName() + "."));
            return;
        }

        int i = -1;

        if (amount == Integer.MAX_VALUE) {
            for (ItemStack content : player.getInventory().getContents()) {
                i++;
                if (content == null || content.getType() == Material.AIR) continue;
                if (!content.isSimilar(stack)) continue;

                player.getInventory().setItem(i, null);
            }
            return;
        }

        player.getInventory().removeItem(stack);
    }

}
