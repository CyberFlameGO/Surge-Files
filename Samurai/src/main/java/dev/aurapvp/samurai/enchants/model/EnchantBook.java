package dev.aurapvp.samurai.enchants.model;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.Samurai;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
public class EnchantBook {

    private ItemStack stack;
    private int level, success, destroy;
    private CustomEnchant enchant;

    public EnchantBook(ItemStack stack) {
        if (stack == null) return;
        if (stack.getType() == Material.AIR) return;
        NBTItem stackNBT = new NBTItem(stack);

        if (!stackNBT.hasTag("custom-enchant")) return;
        if (!stackNBT.hasTag("level")) return;
        if (!stackNBT.hasTag("success")) return;
        if (!stackNBT.hasTag("destroy")) return;

        Optional<CustomEnchant> enchantOptional = Samurai.getInstance().getEnchantHandler().getCustomEnchant(stackNBT.getString("custom-enchant"));
        if (!enchantOptional.isPresent()) return;

        this.enchant = enchantOptional.get();
        this.level = stackNBT.getInteger("level");
        this.success = stackNBT.getInteger("success");
        this.destroy = stackNBT.getInteger("destroy");
        this.stack = stack;
    }

}
