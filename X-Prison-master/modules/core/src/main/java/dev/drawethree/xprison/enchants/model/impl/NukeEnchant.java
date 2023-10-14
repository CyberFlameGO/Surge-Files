package dev.drawethree.xprison.enchants.model.impl;

import dev.drawethree.ultrabackpacks.api.UltraBackpacksAPI;
import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.enchants.XPrisonEnchants;
import dev.drawethree.xprison.enchants.api.events.MeteorTriggerEvent;
import dev.drawethree.xprison.enchants.api.events.NukeTriggerEvent;
import dev.drawethree.xprison.enchants.model.XPrisonEnchantment;
import dev.drawethree.xprison.enchants.utils.EnchantUtils;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.misc.MathUtils;
import dev.drawethree.xprison.utils.misc.MineUtils;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import dev.drawethree.xprison.utils.text.TextUtils;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.util.Cuboid;
import me.lucko.helper.Events;
import me.lucko.helper.time.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class NukeEnchant extends XPrisonEnchantment {

    private double chance;
    private boolean countBlocksBroken;
    private boolean removeBlocks;
    private String message;

    public NukeEnchant(XPrisonEnchants instance) {
        super(instance, 21);
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.countBlocksBroken = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".Count-Blocks-Broken");
        this.removeBlocks = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".Remove-Blocks");
        this.message = TextUtils.applyColor(plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Message"));
    }

    @Override
    public void onEquip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onUnequip(Player p, ItemStack pickAxe, int level) {

    }

    @Override
    public void onBlockBreak(BlockBreakEvent e, int enchantLevel) {
        double chance = getChanceToTrigger(enchantLevel);

        if (chance < ThreadLocalRandom.current().nextDouble(100)) {
            this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> Not a chance", this.plugin);
            return;
        }

        long startTime = Time.nowMillis();
        final Player p = e.getPlayer();
        final Block b = e.getBlock();

        PrivateMine mine = MineUtils.getPrivateMineAt(e.getBlock().getLocation());

        if (mine == null) {
            this.plugin.getCore().debug("LightningEnchant::onBlockBreak >> No mine found", this.plugin);
            return;
        }

        this.plugin.getCore().debug("NukeEnchant::onBlockBreak >> WG Region used: " + mine.getOwner(), this.plugin);

        List<Block> blocks = getSphere(mine.getMinePit().getCenter().clone().add(0, 15, 0), 5, false);
        List<ArmorStand> stands = new ArrayList<>();

        for (Block block : blocks) {
            ArmorStand stand = p.getWorld().spawn(block.getLocation(), ArmorStand.class);

            stand.setArms(false);
            stand.setBasePlate(false);
            stand.setMetadata("nuke", new FixedMetadataValue(XPrison.getInstance(), true));

            int random = ThreadLocalRandom.current().nextInt(100);
            if (random <= 25) {
                stand.setHelmet(new ItemStack(Material.IRON_BLOCK));
            } else if (random <= 50) {
                stand.setHelmet(new ItemStack(Material.GRAVEL));
            } else if (random <= 75) {
                stand.setHelmet(new ItemStack(Material.SAND));
            } else if (random <= 98) {
                stand.setHelmet(new ItemStack(Material.TNT));
            }

            stand.setVisible(false);
            stands.add(stand);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand stand : stands) {
                    if (stand.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) continue;

                    for (ArmorStand other : stands) {
                        other.remove();
                    }

                    List<Block> blocksAffected = getAffectedBlocks(b, mine);

                    NukeTriggerEvent event = callNukeTriggerEvent(e.getPlayer(), mine, e.getBlock(), blocksAffected);

                    if (event.isCancelled() || event.getBlocksAffected().isEmpty()) {
                        plugin.getCore().debug("NukeEnchant::onBlockBreak >> NukeTriggerEvent was cancelled. (Blocks affected size: " + event.getBlocksAffected().size(), plugin);
                        return;
                    }

                    blocksAffected = event.getBlocksAffected();


                    if (!plugin.getCore().isUltraBackpacksEnabled()) {
                        handleAffectedBlocks(p, mine, blocksAffected);
                    } else {
                        UltraBackpacksAPI.handleBlocksBroken(p, blocksAffected);
                    }

                    if (countBlocksBroken) {
                        plugin.getEnchantsManager().addBlocksBrokenToItem(p, blocksAffected.size());
                    }

                    plugin.getCore().getTokens().getTokensManager().handleBlockBreak(p, blocksAffected, countBlocksBroken);
                    mine.reset();
                    long timeEnd = Time.nowMillis();
                    plugin.getCore().debug("NukeEnchant::onBlockBreak >> Took " + (timeEnd - startTime) + " ms.", plugin);

                    new ArrayList<>(stands).forEach(stands::remove);
                    cancel();
                    break;
                }
            }
        }.runTaskTimer(XPrison.getInstance(), 5, 5);
    }

    private void handleAffectedBlocks(Player p, PrivateMine mine, List<Block> blocksAffected) {
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

            if (this.removeBlocks) {
                this.plugin.getCore().getNmsProvider().setBlockInNativeDataPalette(block.getWorld(), block.getX(), block.getY(), block.getZ(), 0, (byte) 0, true);
            }

        }
        this.giveEconomyRewardsToPlayer(p, totalDeposit);
    }

    private List<Block> getAffectedBlocks(Block b, PrivateMine mine) {
        List<Block> blocksAffected = new ArrayList<>();
        Cuboid cuboid = mine.getMinePit();
        for (Block block : cuboid) {
            if (block.getType() == Material.AIR) {
                continue;
            }
            blocksAffected.add(block);
        }
        return blocksAffected;
    }

    @Override
    public double getChanceToTrigger(int enchantLevel) {
        return chance * enchantLevel;
    }

    private void giveEconomyRewardsToPlayer(Player p, double totalDeposit) {
        double total = this.plugin.isMultipliersModuleEnabled() ? plugin.getCore().getMultipliers().getApi().getTotalToDeposit(p, totalDeposit, MultiplierType.SELL) : totalDeposit;

        plugin.getCore().getEconomy().depositPlayer(p, total);

        if (plugin.isAutoSellModuleEnabled()) {
            plugin.getCore().getAutoSell().getManager().addToCurrentEarnings(p, total);
        }

        if (message != null || !message.isEmpty()) {
            PlayerUtils.sendMessage(p,message.replace("%money%", MathUtils.formatNumber(total)));
        }
    }

    private NukeTriggerEvent callNukeTriggerEvent(Player p, PrivateMine mine, Block startBlock, List<Block> affectedBlocks) {
        NukeTriggerEvent event = new NukeTriggerEvent(p, mine, startBlock, affectedBlocks);
        Events.callSync(event);
        this.plugin.getCore().debug("NukeEnchant::callNukeTriggerEvent >> NukeTriggerEvent called.", this.plugin);
        return event;
    }

    @Override
    public void reload() {
        super.reload();
        this.chance = plugin.getEnchantsConfig().getYamlConfig().getDouble("enchants." + id + ".Chance");
        this.countBlocksBroken = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".Count-Blocks-Broken");
        this.removeBlocks = plugin.getEnchantsConfig().getYamlConfig().getBoolean("enchants." + id + ".Remove-Blocks");
        this.message = TextUtils.applyColor(plugin.getEnchantsConfig().getYamlConfig().getString("enchants." + id + ".Message"));

    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }

    public List<Block> getSphere(Location location, int radius, boolean empty) {
        List<Block> blocks = new ArrayList<>();

        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y));
                    if (distance < radius * radius && (!empty && distance < (radius - 1) * (radius - 1))) {
                        blocks.add(new Location(location.getWorld(), x, y, z).getBlock());
                    }
                }
            }
        }

        return blocks;
    }

}
