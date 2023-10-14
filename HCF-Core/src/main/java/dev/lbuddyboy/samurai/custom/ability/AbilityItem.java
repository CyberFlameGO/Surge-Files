package dev.lbuddyboy.samurai.custom.ability;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseStatisticMap;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbilityItem extends BaseStatisticMap {

    public Cooldown cooldown = new Cooldown();
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    public Config config;
    public String name, displayName;
    public List<String> lore;
    public Material material;
    public int defaultAmount;
    public int cooldownSeconds;
    public int customModelData;
    public boolean consumable, exclusive;

    public AbilityItem(String statistic) {
        super(statistic);
    }

    protected static void setGlobalCooldown(Player player) {

    }

    public static boolean isOnGlobalPackageCooldown(Player player) {
        return Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().onCooldown(player);
    }

    public static String getGlobalCooldownTimeFormatted(Player player) {
        return Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().getRemaining(player);
    }

    public void reload(File folder) {
        this.config = new Config(Samurai.getInstance(), this.name, folder);

        this.displayName = config.getString("display-name");
        this.lore = config.getStringList("lore");
        this.material = Material.getMaterial(config.getString("material"));
        this.defaultAmount = config.getInt("default-amount");
        this.cooldownSeconds = config.getInt("cooldown-seconds");
        this.customModelData = config.getInt("custom-model-data");
        this.partnerItem = partnerItem();
        this.consumable = config.getBoolean("consumable", true);
        this.exclusive = config.getBoolean("exclusive", false);
    }

    protected ItemStack partnerItem;

    public boolean isPartnerItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;
        if (!itemStack.hasItemMeta())
            return false;
        if (itemStack.getType() != material) return false;

        ItemMeta meta = itemStack.getItemMeta();
        ItemMeta partnerMeta = partnerItem.getItemMeta();

        if (meta == null) return false;
        if (partnerMeta == null) return false;

        return partnerMeta.getDisplayName().equals(meta.getDisplayName());
    }

    public ShapedRecipe getRecipe() {
        return null;
    }

    public boolean isOnCooldown(Player player) {
        return isOnGlobalPackageCooldown(player) || cooldown.onCooldown(player);
    }

    public boolean isOnCooldownReal(Player player) {
        return cooldown.onCooldown(player);
    }

    public void setCooldown(Player player) {
        cooldown.applyCooldown(player, getCooldownTime());
        Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().applyCooldown(player.getUniqueId(), 10);

        incrementStatistic(player.getUniqueId(), 1);
    }

    public void resetCooldown(Player player) {
        Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().removeCooldown(player);
        cooldown.removeCooldown(player);

        cooldown.cleanUp();
        Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().cleanUp();
    }

    public String getCooldownTime(Player player) {
        Cooldown GLOBAL = Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN();

        if (GLOBAL.onCooldown(player)) {
            return GLOBAL.getRemaining(player);
        }

        return getRealCooldown(player);
    }

    public String getCooldownTimeFancy(Player player) {
        Cooldown GLOBAL = Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN();

        if (GLOBAL.onCooldown(player)) {
            return GLOBAL.getRemainingFancy(player);
        }

        return getRealCooldown(player);
    }

    public String getRealCooldown(Player player) {
        return this.cooldown.getRemaining(player);
    }

    public String getRealCooldownFancy(Player player) {
        return this.cooldown.getRemainingFancy(player);
    }

    public String getCooldownMessage(Player player) {
        return CC.MAIN + CC.BOLD + getName() + " " + CC.RED + "is on cooldown for another " + CC.BOLD +
                getCooldownTimeFancy(player) + CC.RED + "!";
    }

    protected void sendActivationMessages(Player attacker, String[] attackContent, Player victim, String[] victimContent) {
        attacker.sendMessage(" ");
        attacker.sendMessage(
                CC.MAIN + CC.STAR + " " + CC.WHITE +
                        attackContent[0]
        );
        if (attackContent.length > 1) {
            for (int i = 1; i < attackContent.length; i++) {
                attacker.sendMessage(
                        CC.MAIN + CC.STAR + " " + CC.WHITE +
                                attackContent[i]
                );
            }
        }

        attacker.sendMessage(
                CC.MAIN + CC.STAR + " " + CC.MAIN +
                        "Cooldown: " + CC.WHITE + getCooldownTime() + "s"
        );
        attacker.sendMessage(" ");

        // target messages
        if (victim != null) {
            victim.sendMessage(" ");
            victim.sendMessage(
                    CC.MAIN + CC.STAR + " " + CC.WHITE + victimContent[0]
            );
            if (victimContent.length > 1) {
                for (int i = 1; i < victimContent.length; i++) {
                    victim.sendMessage(
                            CC.MAIN + CC.STAR + " " + CC.WHITE + victimContent[i]
                    );
                }
            }
            victim.sendMessage(" ");
        }

    }

    public boolean isExclusive() {
        return this.exclusive;
    }

    protected abstract boolean onUse(PlayerInteractEvent event);

    // cool down time in seconds
    public long getCooldownTime() {
        if (SOTWCommand.isPartnerPackageHour()) return this.cooldownSeconds / 2;
        return this.cooldownSeconds;
    }

    public ItemStack getPartnerItem() {

        ItemStack stack = partnerItem().clone();
        stack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        stack.setAmount(getAmount());

        return stack;
    }

    protected ItemStack partnerItem() {
        return ItemBuilder.of(this.material)
                .name(CC.translate(this.displayName))
                .setLore(CC.translate(this.lore))
                .modelData(this.customModelData)
                .build();
    }

    public String getName() {
        return CC.translate(this.displayName);
    }

    public int getAmount() {
        return this.defaultAmount;
    }

    protected void consume(Player player, ItemStack partnerItem) {
        if (!consumable) return;
        if (partnerItem == null) return;

        if (partnerItem.isSimilar(player.getInventory().getItemInMainHand())) {
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        } else {
            if (player.getInventory().getItemInOffHand().getAmount() > 1) {
                player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        }
    }

    public boolean canUse(Player player, Cancellable event) {
        Location location = player.getLocation();

        if (player.hasMetadata("modmode")) {
            if (event != null) event.setCancelled(true);

            player.sendMessage(CC.translate("&cYou cannot use ability items in mod mode."));
            return false;
        }

        if (player.getWorld().getEnvironment() == World.Environment.THE_END || player.getWorld().getEnvironment() == World.Environment.NETHER) {
            if (event != null) event.setCancelled(true);

            player.sendMessage(CC.translate("&cYou cannot use ability items in the end/nether."));
            return false;
        }

        if (DTRBitmask.KOTH.appliesAt(location) || DTRBitmask.CITADEL.appliesAt(location) || DTRBitmask.CONQUEST.appliesAt(location)) {
            if (event != null) event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Ability items cannot be used in koths/citadels/conquests!");
            return false;
        }

        if (Samurai.getInstance().getServerHandler().isWarzone(player.getLocation())) {
            if (event != null) event.setCancelled(true);

            player.sendMessage(CC.translate("&cYou cannot use ability items in the warzone."));
            return false;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(location)) {
            if (event != null) event.setCancelled(true);

            player.sendMessage(ChatColor.RED + "Ability items cannot be used in safe zones!");
            return false;
        }

        if (isOnCooldown(player)) {
            if (event != null) event.setCancelled(true);

            player.sendMessage(getCooldownMessage(player));
            return false;
        }

        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Samurai.getInstance().getAbilityItemHandler().getGLOBAL_COOLDOWN().cleanUp();
        this.cooldown.cleanUp();
    }

    public List<Material> getRecipeDisplay() {
        return null;
    }

}
