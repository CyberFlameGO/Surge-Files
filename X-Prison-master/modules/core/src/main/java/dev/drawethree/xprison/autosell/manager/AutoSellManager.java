package dev.drawethree.xprison.autosell.manager;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.autosell.api.events.XPrisonAutoSellEvent;
import dev.drawethree.xprison.autosell.api.events.XPrisonSellAllEvent;
import dev.drawethree.xprison.autosell.model.AutoSellItemStack;
import dev.drawethree.xprison.autosell.utils.AutoSellContants;
import dev.drawethree.xprison.enchants.utils.EnchantUtils;
import dev.drawethree.xprison.multipliers.XPrisonMultipliers;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.economy.EconomyUtils;
import dev.drawethree.xprison.utils.inventory.InventoryUtils;
import dev.drawethree.xprison.utils.misc.MaterialUtils;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static jdk.tools.jlink.internal.plugins.PluginsResourceBundle.getMessage;

public class AutoSellManager {

    private static final CooldownMap<Player> INVENTORY_FULL_COOLDOWN_MAP = CooldownMap.create(Cooldown.of(2, TimeUnit.SECONDS));

    private final XPrisonAutoSell plugin;
    private final Map<UUID, Double> lastEarnings, lastGems, lastTokens;
    private final Map<UUID, Long> lastItems, lastBlocks;
    private final List<UUID> enabledAutoSellPlayers;
    @Getter private Map<CompMaterial, Double> sellPrices;

    public AutoSellManager(XPrisonAutoSell plugin) {
        this.plugin = plugin;
        this.enabledAutoSellPlayers = new ArrayList<>();
        this.lastEarnings = new HashMap<>();
        this.lastGems = new HashMap<>();
        this.lastTokens = new HashMap<>();
        this.lastItems = new HashMap<>();
        this.lastBlocks = new HashMap<>();
    }

    private void loadAutoSellRegions() {
        this.sellPrices = new HashMap<>();

        YamlConfiguration config = this.plugin.getAutoSellConfig().getYamlConfig();
        ConfigurationSection section = config.getConfigurationSection("sell-prices.items");

        if (section == null) {
            return;
        }

        for (String item : section.getKeys(false)) {
            CompMaterial type = CompMaterial.valueOf(item);
            double sellPrice = section.getDouble(item);
            this.sellPrices.put(type, sellPrice);
        }

        this.plugin.getCore().getLogger().info("Loaded auto-sell prices!");
    }

    public void reload() {
        this.load();
    }

    public void load() {
        this.loadAutoSellRegions();
    }

    public double getSellPriceFor(CompMaterial m) {
        return this.sellPrices.getOrDefault(m, 0.0);
    }

    public void sellAll(Player sender, boolean sendMessage) {
        this.plugin.getCore().debug("User " + sender.getName() + " ran /sellall", XPrisonAutoSell.getInstance());

        double totalPrice = 0;

        List<ItemStack> toRemove = new ArrayList<>();

        for (CompMaterial m : this.sellPrices.keySet()) {
            for (ItemStack item : Arrays.stream(sender.getInventory().getContents()).filter(i -> i != null && CompMaterial.fromItem(i) == m).collect(Collectors.toList())) {
                totalPrice += item.getAmount() * this.getSellPriceFor(m);
                toRemove.add(item);
            }
        }

        toRemove.forEach(i -> sender.getInventory().removeItem(i));

        if (XPrisonMultipliers.getInstance().isEnabled()) {
            totalPrice = (long) this.plugin.getCore().getMultipliers().getApi().getTotalToDeposit(sender, totalPrice, MultiplierType.SELL);
        }

        XPrisonSellAllEvent event = new XPrisonSellAllEvent(sender, sellPrices, totalPrice);

        Events.call(event);

        if (event.isCancelled()) {
            this.plugin.getCore().debug("UltraPrisonSellAllEvent was cancelled.", XPrisonAutoSell.getInstance());
            return;
        }

        this.plugin.getCore().getEconomy().depositPlayer(sender, event.getSellPrice());

        if (event.getSellPrice() > 0.0 && sendMessage) {
            PlayerUtils.sendMessage(sender, getMessage("sell_all_complete").replace("%price%", String.format("%,.0f", event.getSellPrice())));
        }
    }

    public void sellAll(Player sender) {
        sellAll(sender, false);
    }

    private double sellItems(Player player, Map<AutoSellItemStack, Double> itemsToSell) {

        double totalAmount = itemsToSell.values().stream().mapToDouble(Double::doubleValue).sum();

        if (this.plugin.isMultipliersModuleEnabled()) {
            totalAmount = (long) this.plugin.getCore().getMultipliers().getApi().getTotalToDeposit(player, totalAmount, MultiplierType.SELL);
        }

        EconomyUtils.deposit(player, totalAmount);
        return totalAmount;
    }

    private XPrisonAutoSellEvent callAutoSellEvent(Player player, Map<AutoSellItemStack, Double> itemsToSell) {
        XPrisonAutoSellEvent event = new XPrisonAutoSellEvent(player, itemsToSell);

        Events.call(event);

        if (event.isCancelled()) {
            this.plugin.getCore().debug("XPrisonAutoSellEvent was cancelled.", this.plugin);
        }

        return event;
    }

    public void resetLastEarnings() {
        this.lastEarnings.clear();
        this.lastTokens.clear();
        this.lastGems.clear();
    }

    public void resetLastItems() {
        this.lastItems.clear();
    }

    public void resetLastBlocks() {
        this.lastBlocks.clear();
    }

    public double getPlayerLastEarnings(Player p) {
        return this.lastEarnings.getOrDefault(p.getUniqueId(), 0.0D);
    }

    public double getPlayerLastTokens(Player p) {
        return this.lastTokens.getOrDefault(p.getUniqueId(), 0.0D);
    }

    public double getPlayerLastGems(Player p) {
        return this.lastGems.getOrDefault(p.getUniqueId(), 0.0D);
    }

    public long getPlayerLastItemsAmount(Player p) {
        return this.lastItems.getOrDefault(p.getUniqueId(), 0L);
    }

    public long getPlayerLastBlocks(Player p) {
        return this.lastBlocks.getOrDefault(p.getUniqueId(), 0L);
    }

    public double getCurrentEarnings(Player player) {
        return lastEarnings.getOrDefault(player.getUniqueId(), 0.0);
    }

    public double getPriceForItem(ItemStack item) {
        return getSellPriceFor(CompMaterial.fromItem(item));
    }

    public boolean hasAutoSellEnabled(Player p) {
        return true;
//        return enabledAutoSellPlayers.contains(p.getUniqueId());
    }

    public void toggleAutoSell(Player player) {
        boolean removed = enabledAutoSellPlayers.remove(player.getUniqueId());

        if (removed) {
            PlayerUtils.sendMessage(player, this.plugin.getAutoSellConfig().getMessage("autosell_disable"));
        } else {
            PlayerUtils.sendMessage(player, this.plugin.getAutoSellConfig().getMessage("autosell_enable"));
            enabledAutoSellPlayers.add(player.getUniqueId());
        }
    }

    public boolean canPlayerEnableAutosellOnJoin(Player player) {
        if (!this.plugin.getAutoSellConfig().isEnableAutosellAutomatically()) {
            return false;
        }
        return player.hasPermission(AutoSellContants.AUTOSELL_PERMISSION) && !hasAutoSellEnabled(player);
    }

    public boolean givePlayerItem(Player player, Block block) {

        if (!InventoryUtils.hasSpace(player.getInventory())) {
            this.notifyInventoryFull(player);
            return true;
        }

        player.getInventory().addItem(createItemStackToGive(player, block));
        return true;
    }

    private ItemStack createItemStackToGive(Player player, Block block) {
        int amount = EnchantUtils.getFortuneBlockCount(player.getItemInHand(), block);

        ItemStack toGive;

        if (this.plugin.getAutoSellConfig().isAutoSmelt()) {
            toGive = MaterialUtils.getSmeltedFormAsItemStack(block);
        } else {
            toGive = CompMaterial.fromBlock(block).toItem();
        }
        toGive.setAmount(amount);
        return toGive;
    }

    private void notifyInventoryFull(Player player) {

        if (!this.plugin.getAutoSellConfig().isInventoryFullNotificationEnabled() || !INVENTORY_FULL_COOLDOWN_MAP.test(player)) {
            return;
        }

        List<String> inventoryFullTitle = this.plugin.getAutoSellConfig().getInventoryFullNotificationTitle();
        String inventoryFullNotificationMessage = this.plugin.getAutoSellConfig().getInventoryFullNotificationMessage();

        if (!inventoryFullTitle.isEmpty()) {
            PlayerUtils.sendTitle(player, inventoryFullTitle.get(0), inventoryFullTitle.get(1));
        } else {
            PlayerUtils.sendMessage(player, inventoryFullNotificationMessage);
        }
    }

    public boolean autoSellBlock(Player player, Block block) {

        Map<AutoSellItemStack, Double> itemsToSell = previewItemsSell(Arrays.asList(createItemStackToGive(player, block)));
        XPrisonAutoSellEvent event = this.callAutoSellEvent(player, itemsToSell);

        if (event.isCancelled()) {
            return false;
        }

        this.lastBlocks.put(player.getUniqueId(), this.lastBlocks.getOrDefault(player.getUniqueId(), 0L) + 1);
        itemsToSell = event.getItemsToSell();

        int amountOfItems = itemsToSell.keySet().stream().mapToInt(item -> item.getItemStack().getAmount()).sum();
        double moneyEarned = this.sellItems(player, itemsToSell);

        this.updateCurrentEarningsAndLastItems(player, moneyEarned, amountOfItems);

        return true;
    }

    private void updateCurrentEarningsAndLastItems(Player player, double moneyEarned, int amountOfItems) {
        this.addToCurrentEarnings(player, moneyEarned);
        this.addToLastItems(player, amountOfItems);
    }

    public void addToCurrentEarnings(Player player, double amount) {
        double current = this.lastEarnings.getOrDefault(player.getUniqueId(), 0.0);
        this.lastEarnings.put(player.getUniqueId(), current + amount);
    }

    public void addToCurrentGems(Player player, double amount) {
        double current = this.lastGems.getOrDefault(player.getUniqueId(), 0.0);
        this.lastGems.put(player.getUniqueId(), current + amount);
    }

    public void addToCurrentTokens(Player player, double amount) {
        double current = this.lastTokens.getOrDefault(player.getUniqueId(), 0.0);
        this.lastTokens.put(player.getUniqueId(), current + amount);
    }

    public void addToLastItems(Player player, int amountOfItems) {
        long current = this.lastItems.getOrDefault(player.getUniqueId(), 0L);
        this.lastItems.put(player.getUniqueId(), current + amountOfItems);
    }

    public double getPriceForBlock(Block block) {
        CompMaterial material = CompMaterial.fromBlock(block);

        return getSellPriceFor(material);
    }

    public void sellBlocks(Player player, List<Block> blocks) {
        blocks.forEach(block -> autoSellBlock(player, block));
    }

    public Map<AutoSellItemStack, Double> previewItemsSell(Collection<ItemStack> items) {
        Map<AutoSellItemStack, Double> itemsToSell = new HashMap<>();

        for (ItemStack item : items) {

            if (item == null) {
                continue;
            }

            double priceForItem = this.getPriceForItem(item);

            if (priceForItem <= 0.0) {
                continue;
            }

            itemsToSell.put(new AutoSellItemStack(item), priceForItem);
        }

        return itemsToSell;
    }

    public Set<CompMaterial> getSellingMaterialsSorted(Comparator<CompMaterial> comparator) {
        return this.sellPrices.keySet().stream().sorted(comparator).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
