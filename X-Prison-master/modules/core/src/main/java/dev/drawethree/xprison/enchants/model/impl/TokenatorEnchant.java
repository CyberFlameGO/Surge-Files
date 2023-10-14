package dev.drawethree.xprison.enchants.model.impl;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.api.enums.ReceiveCause;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.tokens.XPrisonTokens;
import dev.drawethree.xprison.utils.misc.MineUtils;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class TokenatorEnchant extends XPrisonEnchantment {

    private double chance;
    private String amountToGiveExpression;


    public TokenatorEnchant(XPrisonEnchants instance) {
        super(instance, 14);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.amountToGiveExpression = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Amount-To-Give");
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        if (!this.plugin.getCore().isModuleEnabled(XPrisonTokens.MODULE_NAME)) {
            return;
        }

        double chance = getChanceToTrigger(enchantLevel);

        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            return;
        }

        long randAmount = (long) createExpression(enchantLevel).evaluate();
        PrivateMine mine = MineUtils.getPrivateMineAt(e.getBlock().getLocation());

        if (mine == null) {
            return;
        }

        if (e.getPlayer().getUniqueId() == mine.getOwner()) {
            plugin.getCore().getTokens().getTokensManager().giveTokens(e.getPlayer(), randAmount, null, ReceiveCause.MINING);

            if (this.plugin.isAutoSellModuleEnabled()) {
                XPrison.getInstance().getAutoSell().getManager().addToCurrentTokens(e.getPlayer(), randAmount);
            }
        } else {
            long randTaxed = (long) mine.getTaxed((int) randAmount);
            long diff = randAmount - randTaxed;

            mine.setTokensTaxed(mine.getTokensTaxed() + diff);
            plugin.getCore().getTokens().getTokensManager().giveTokens(e.getPlayer(), randTaxed, null, ReceiveCause.MINING);

            if (this.plugin.isAutoSellModuleEnabled()) {
                XPrison.getInstance().getAutoSell().getManager().addToCurrentTokens(e.getPlayer(), randTaxed);
            }
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
        this.amountToGiveExpression = plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Amount-To-Give");
    }

    private Expression createExpression(int level) {
        return new ExpressionBuilder(this.amountToGiveExpression)
                .variables("level")
                .build()
                .setVariable("level", level);
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }
}
