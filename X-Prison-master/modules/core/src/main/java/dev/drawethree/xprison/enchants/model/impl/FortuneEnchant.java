package dev.drawethree.xprison.enchants.model.impl;

import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.misc.MineUtils;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.util.CC;
import me.lucko.helper.time.Time;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FortuneEnchant extends XPrisonEnchantment {

    private static List<CompMaterial> blackListedBlocks;

    public FortuneEnchant(XPrisonEnchants instance) {
        super(instance, 3);
        blackListedBlocks = plugin.getEnchantsConfig().getYamlConfig().getStringList("enchants." + id + ".Blacklist").stream().map(CompMaterial::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {
        ItemMeta meta = pickAxe.getItemMeta();
        meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
        pickAxe.setItemMeta(meta);
    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        long startTime = Time.nowMillis();
        final Player player = e.getPlayer();
        final Block b = e.getBlock();

        PrivateMine mine = MineUtils.getPrivateMineAt(e.getBlock().getLocation());

        if (mine == null) {
            return;
        }

        if (!mine.getMinePit().contains(b)) {
            player.sendMessage(CC.translate("&cYou can only break blocks inside of your mine."));
            return;
        }

    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return 100.0;
    }

    @Override
    public void reload() {
        super.reload();
        blackListedBlocks = plugin.getEnchantsConfig().getYamlConfig().getStringList("enchants." + id + ".Blacklist").stream().map(CompMaterial::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }

    public static boolean isBlockBlacklisted(Block block) {
        CompMaterial blockMaterial = CompMaterial.fromBlock(block);
        return blackListedBlocks.contains(blockMaterial);
    }
}
