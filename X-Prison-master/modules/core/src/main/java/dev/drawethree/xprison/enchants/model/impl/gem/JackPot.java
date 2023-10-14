package dev.drawethree.xprison.enchants.model.impl.gem;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class JackPot extends XPrisonEnchantment {

    private double chance;
    private List<String> commandsToExecute;

    public JackPot(XPrisonEnchants instance) {
        super(instance, 25);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.commandsToExecute = plugin.getEnchantsConfig().getYamlConfig().getStringList("enchants." + id + ".Commands");

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

        String randomCmd = this.getRandomCommandToExecute();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), randomCmd.replace("%player%", e.getPlayer().getName()));

    }

    private String getRandomCommandToExecute() {
        return this.commandsToExecute.get(ThreadLocalRandom.current().nextInt(commandsToExecute.size()));
    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return chance * enchantLevel;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.commandsToExecute = plugin.getEnchantsConfig().getYamlConfig().getStringList("enchants." + id + ".Commands");
    }



}
