package dev.drawethree.xprison.enchants.model.impl.gem;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class DoubleOrNothing extends XPrisonEnchantment {

    private double chance;

    public DoubleOrNothing(XPrisonEnchants instance) {
        super(instance, 26);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
    }

    @Override
    public String getAuthor() {
        return "LBuddyBoy";
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {

        if (!this.plugin.getCore().isModuleEnabled(XPrisonAutoSell.MODULE_NAME)) {
            return;
        }

        double chance = getChanceToTrigger(enchantLevel);
        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }



    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return chance * enchantLevel;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
    }
}
