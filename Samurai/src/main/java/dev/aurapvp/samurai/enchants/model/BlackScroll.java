package dev.aurapvp.samurai.enchants.model;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.Samurai;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class BlackScroll {

    private int success, destroy;
    private boolean blackScroll;
    private ItemStack stack;

    public BlackScroll(ItemStack stack) {
        if (stack == null) return;
        if (!Samurai.getInstance().getEnchantHandler().isBlackScroll(stack)) return;
        NBTItem stackNBT = new NBTItem(stack);

        if (!stackNBT.hasTag("success")) return;
        if (!stackNBT.hasTag("destroy")) return;

        this.success = stackNBT.getInteger("success");
        this.destroy = stackNBT.getInteger("destroy");
        this.blackScroll = true;
        this.stack = stack;
    }

}
