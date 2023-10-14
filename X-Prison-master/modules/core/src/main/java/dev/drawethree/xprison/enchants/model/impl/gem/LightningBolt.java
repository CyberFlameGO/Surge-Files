package dev.drawethree.xprison.enchants.model.impl.gem;

import dev.drawethree.ultrabackpacks.api.UltraBackpacksAPI;
import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.api.events.LightningTriggerEvent;
import dev.drawethree.xprison.enchants.api.events.MeteorTriggerEvent;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.enchants.model.impl.FortuneEnchant;
import dev.drawethree.xprison.enchants.utils.EnchantUtils;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.misc.MineUtils;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import me.lucko.helper.Events;
import me.lucko.helper.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LightningBolt extends XPrisonEnchantment {

    private double chance;

    public LightningBolt(XPrisonEnchants instance) {
        super(instance, 27);
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
            this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> Module disabled", this.plugin);
            return;
        }

        double chance = getChanceToTrigger(enchantLevel);
        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> Not a chance", this.plugin);
            return;
        }

        Location location = e.getBlock().getLocation();
        World world = location.getWorld();

        if (world == null) return;

        Block highest = this.getHighestSolid(location);

        long startTime = Time.nowMillis();
        final Player player = e.getPlayer();
        final Block b = e.getBlock();

        PrivateMine mine = MineUtils.getPrivateMineAt(e.getBlock().getLocation());

        if (mine == null) {
            this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> Not a mine", this.plugin);
            return;
        }

        this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> WG Region used: " + mine.getOwner(), this.plugin);

        world.strikeLightningEffect(location);
        List<Block> blocksAffected = getAffectedBlocks(b, ThreadLocalRandom.current().nextInt(9, (int) (highest.getLocation().distance(b.getLocation()) + 10)), mine);
        LightningTriggerEvent event = callLightningTriggerEvent(e.getPlayer(), mine, e.getBlock(), blocksAffected);

        if (event.isCancelled() || event.getBlocksAffected().isEmpty()) {
            plugin.getCore().debug("LightningEnchant::onBlockBreak >> MeteorTriggerEvent was cancelled. (Blocks affected size: " + event.getBlocksAffected().size(), plugin);
            return;
        }

        blocksAffected = event.getBlocksAffected();
        for (Block block : blocksAffected) {
            mine.increaseProgress(player.getUniqueId(), -1);
        }

        mine.setBlocksLeft(mine.getBlocksLeft() - blocksAffected.size());

        if (!plugin.getCore().isUltraBackpacksEnabled()) {
            handleAffectedBlocks(player, blocksAffected);
        } else {
            UltraBackpacksAPI.handleBlocksBroken(player, blocksAffected);
        }

        plugin.getCore().getTokens().getTokensManager().handleBlockBreak(player, blocksAffected, false);

        long timeEnd = Time.nowMillis();
        plugin.getCore().debug("LightningEnchant::onBlockBreak >> Took " + (timeEnd - startTime) + " ms.", plugin);

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

    private void handleAffectedBlocks(Player p, List<Block> blocksAffected) {
        double totalDeposit = 0.0;
        int fortuneLevel = EnchantUtils.getItemFortuneLevel(p.getItemInHand());
        boolean autoSellPlayerEnabled = this.plugin.isAutoSellModuleEnabled() && plugin.getCore().getAutoSell().getManager().hasAutoSellEnabled(p);

        for (Block block : blocksAffected) {

            int amplifier = fortuneLevel;
            if (FortuneEnchant.isBlockBlacklisted(block)) {
                amplifier = 1;
            }

            if (autoSellPlayerEnabled) {
                totalDeposit += ((plugin.getCore().getAutoSell().getManager().getPriceForBlock(block) + 0.0) * amplifier);
            } else {
                ItemStack itemToGive = CompMaterial.fromBlock(block).toItem(amplifier);
                p.getInventory().addItem(itemToGive);
            }
            this.plugin.getCore().getNmsProvider().setBlockInNativeDataPalette(block.getWorld(), block.getX(), block.getY(), block.getZ(), 0, (byte) 0, true);
        }
        this.giveEconomyRewardToPlayer(p, totalDeposit);
    }

    private void giveEconomyRewardToPlayer(Player p, double totalDeposit) {
        double total = this.plugin.isMultipliersModuleEnabled() ? plugin.getCore().getMultipliers().getApi().getTotalToDeposit(p, totalDeposit, MultiplierType.SELL) : totalDeposit;

        plugin.getCore().getEconomy().depositPlayer(p, total);

        if (plugin.isAutoSellModuleEnabled()) {
            plugin.getCore().getAutoSell().getManager().addToCurrentEarnings(p, total);
        }
    }

    private LightningTriggerEvent callLightningTriggerEvent(Player player, PrivateMine mine, Block originBlock, List<Block> blocksAffected) {
        LightningTriggerEvent event = new LightningTriggerEvent(player, mine, originBlock, blocksAffected);
        Events.callSync(event);
        this.plugin.getCore().debug("LightningEnchant::callLightningTriggerEvent >> LightningTriggerEvent called.", this.plugin);
        return event;
    }

    private List<Block> getAffectedBlocks(Block startBlock, int depth, PrivateMine mine) {
        List<Block> blocksAffected = new ArrayList<>();

        for (Location location : getCylinder(startBlock, 2, depth, mine)) {
            if (!mine.getMinePit().contains(location)) continue;

            blocksAffected.add(location.getBlock());
        }

        return blocksAffected;
    }

    public Block getHighestSolid(Location origin) {
        for (int i = origin.getBlockY(); i < 256; i++) {
            Location modified = origin.clone().add(0, i - origin.getBlockY(), 0);
            if (modified.getBlock().getType() != Material.AIR) continue;

            return modified.getBlock();
        }

        return origin.getBlock();
    }

    public List<Location> getCylinder(Block center, int radius, int height, PrivateMine mine) {
        List<Location> locations = new ArrayList<>();

        for (int currentheight = 0; currentheight < height; currentheight++) { //loop through all the y values(height)
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    Block block = center.getRelative(x, currentheight, z);
                    if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK ) continue;
                    if (!mine.getMinePit().contains(block)) continue;

                    locations.add(block.getLocation());
                }
            }
        }

        return locations;
    }

}
