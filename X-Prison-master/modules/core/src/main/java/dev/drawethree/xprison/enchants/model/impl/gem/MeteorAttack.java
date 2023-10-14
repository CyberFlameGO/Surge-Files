package dev.drawethree.xprison.enchants.model.impl.gem;

import dev.drawethree.ultrabackpacks.api.UltraBackpacksAPI;
import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.api.events.LayerTriggerEvent;
import dev.drawethree.xprison.enchants.api.events.MeteorTriggerEvent;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.enchants.model.impl.FortuneEnchant;
import dev.drawethree.xprison.enchants.utils.EnchantUtils;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.misc.MineUtils;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.util.Cuboid;
import me.lucko.helper.Events;
import me.lucko.helper.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MeteorAttack extends XPrisonEnchantment {

    private double chance;

    public MeteorAttack(XPrisonEnchants instance) {
        super(instance, 24);
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

        long startTime = Time.nowMillis();
        final Player player = e.getPlayer();
        final Block b = e.getBlock();

        PrivateMine mine = MineUtils.getPrivateMineAt(e.getBlock().getLocation());

        if (mine == null) {
            return;
        }

        this.plugin.getCore().debug("MeteorAttack::onBlockBreak >> WG Region used: " + mine.getOwner(), this.plugin);
        List<Block> blocks = getSphere(player.getLocation().clone().add(0, 25, 0), 5, false, mine);
        List<ArmorStand> stands = new ArrayList<>();

        for (Block block : blocks) {
            ArmorStand stand = player.getWorld().spawn(block.getLocation(), ArmorStand.class);

            stand.setArms(false);
            stand.setBasePlate(false);
            stand.setMetadata("meteor", new FixedMetadataValue(XPrison.getInstance(), true));

            int random = ThreadLocalRandom.current().nextInt(100);
            if (random <= 25) {
                stand.setHelmet(new ItemStack(Material.COAL_BLOCK));
            } else if (random <= 50) {
                stand.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
            } else if (random <= 75) {
                stand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
            } else if (random <= 98) {
                stand.setHelmet(new ItemStack(Material.IRON_BLOCK));
            }

            stand.setVisible(false);
            stands.add(stand);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand stand : stands) {
                    if (stand.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && stand.getLocation().getY() > 0) continue;

                    for (ArmorStand other : stands) {
                        other.remove();
                    }

                    List<Block> blocksAffected = getAffectedBlocks(b, mine);
                    MeteorTriggerEvent event = callLayerTriggerEvent(e.getPlayer(), mine, e.getBlock(), blocksAffected);

                    if (event.isCancelled() || event.getBlocksAffected().isEmpty()) {
                        plugin.getCore().debug("MeteorAttack::onBlockBreak >> MeteorTriggerEvent was cancelled. (Blocks affected size: " + event.getBlocksAffected().size(), plugin);
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
                    plugin.getCore().debug("MeteorEnchant::onBlockBreak >> Took " + (timeEnd - startTime) + " ms.", plugin);

                    new ArrayList<>(stands).forEach(stands::remove);
                    cancel();
                    break;
                }
            }
        }.runTaskTimer(XPrison.getInstance(), 5, 5);
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

    private MeteorTriggerEvent callLayerTriggerEvent(Player player, PrivateMine mine, Block originBlock, List<Block> blocksAffected) {
        MeteorTriggerEvent event = new MeteorTriggerEvent(player, mine, originBlock, blocksAffected);
        Events.callSync(event);
        this.plugin.getCore().debug("MeteorEnchant::callMeteorTriggerEvent >> MeteorTriggerEvent called.", this.plugin);
        return event;
    }

    private List<Block> getAffectedBlocks(Block startBlock, PrivateMine mine) {
        List<Block> blocksAffected = new ArrayList<>();
        for (Block block : getSphere(startBlock.getLocation(), ThreadLocalRandom.current().nextInt(5, 25), false, mine)) {
            if (!mine.getMinePitBox().contains(startBlock)) continue;

            blocksAffected.add(block);
        }
        
        return blocksAffected;
    }

    public List<Block> getSphere(Location location, int radius, boolean empty, PrivateMine mine) {
        List<Block> blocks = new ArrayList<>();

        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y));
                    if (distance < radius * radius && (!empty && distance < (radius - 1) * (radius - 1))) {
                        Block block = new Location(location.getWorld(), x, y, z).getBlock();

                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

}
