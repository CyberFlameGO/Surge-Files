package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public final class SupportGoats extends AbilityItem implements Listener {

    private final Map<UUID, List<UUID>> supportGoats = new HashMap<>();
    private int DESPAWN_DELAY = 30;

    public SupportGoats() {
        super("SupportGoats");

        this.name = "support-goats";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());
        MessageConfiguration.SUPPORT_GOATS_CLICKER.sendListMessage(player
                , "%ability-name%", this.getName()
        );

        Entity strength = player.getWorld().spawnEntity(player.getLocation(), EntityType.GOAT);
        Entity regen = player.getWorld().spawnEntity(player.getLocation(), EntityType.GOAT);
        Entity resistance = player.getWorld().spawnEntity(player.getLocation(), EntityType.GOAT);

        strength.setCustomName(CC.translate("&cStrength Goat"));
        regen.setCustomName(CC.translate("&dRegeneration Goat"));
        resistance.setCustomName(CC.translate("&bResistance Goat"));

        strength.setMetadata("owner", new FixedMetadataValue(Samurai.getInstance(), player.getUniqueId()));
        regen.setMetadata("owner", new FixedMetadataValue(Samurai.getInstance(), player.getUniqueId()));
        resistance.setMetadata("owner", new FixedMetadataValue(Samurai.getInstance(), player.getUniqueId()));

        List<UUID> bees = Arrays.asList(
                strength.getUniqueId(),
                regen.getUniqueId(),
                resistance.getUniqueId()
        );

        this.supportGoats.put(player.getUniqueId(), bees);

        Bukkit.getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
            List<Player> players = new ArrayList<>();

            players.add(player);

            for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                if (!(entity instanceof Player target)) continue;
                Team team = Samurai.getInstance().getTeamHandler().getTeam(target);
                if (team == null) continue;
                if (!team.getMembers().contains(player.getUniqueId())) continue;

                if (target.isOnline()) {
                    players.add(target);
                }
            }

            for (Player p : players) {
                if (!strength.isDead()) {
                    if (p.isOnline()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0));
                    }
                }
                if (!regen.isDead()) {
                    if (p.isOnline()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0));
                    }
                }
                if (!resistance.isDead()) {
                    if (p.isOnline()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0));
                    }
                }
            }
        }, 5, 20 * 5);

        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
            if (!strength.isDead()) {
                strength.remove();
            }
            if (!regen.isDead()) {
                regen.remove();
            }
            if (!resistance.isDead()) {
                resistance.remove();
            }

            this.supportGoats.remove(player.getUniqueId());
        }, 20L * DESPAWN_DELAY);

        player.setCooldown(this.material, (int) (20 * getCooldownTime()));

        return true;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() != event.getView().getBottomInventory()) return;
        if (!(event.getClickedInventory() instanceof PlayerInventory inventory)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (!(event.getCurrentItem().isSimilar(getPartnerItem()))) return;
        if (player.getItemOnCursor() == null) return;
        if (player.getItemOnCursor().getType() == Material.AIR) return;
        if (!(player.getItemOnCursor().isSimilar(getPartnerItem()))) return;
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
        ItemStack current = event.getCurrentItem();

        current.setAmount(current.getAmount() + player.getItemOnCursor().getAmount());
        player.setItemOnCursor(null);
        event.setCurrentItem(current);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.BEE) return;

        for (Map.Entry<UUID, List<UUID>> entry : this.supportGoats.entrySet()) {

            if (!entry.getValue().contains(event.getEntity().getUniqueId())) continue;

            Player target = Bukkit.getPlayer(entry.getKey());

            if (target == null) continue;
            if (event.getEntity().getCustomName() == null) continue;

            if (event.getEntity().getCustomName().contains("Strength")) {
                target.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            } else if (event.getEntity().getCustomName().contains("Regen")) {
                target.removePotionEffect(PotionEffectType.REGENERATION);
            } else if (event.getEntity().getCustomName().contains("Resistance")) {
                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Goat goat)) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!goat.hasMetadata("owner")) return;

        MetadataValue value = goat.getMetadata("owner").get(0);
        if (value == null) return;

        UUID uuid = (UUID) value.value();
        if (player.getUniqueId() != uuid) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cYou cannot hit your own goats!"));
    }

    @Override
    public ShapedRecipe getRecipe() {
        NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(this.name.toLowerCase().replace("'", "").replace(" ", "_")));
        ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

        recipe.shape("ABC", "DDD", "ABC");
        recipe.setIngredient('A', Material.GHAST_TEAR);
        recipe.setIngredient('B', Material.IRON_INGOT);
        recipe.setIngredient('C', Material.BLAZE_POWDER);
        recipe.setIngredient('D', Material.GOLD_INGOT);

        return recipe;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return Arrays.asList(
                Material.GHAST_TEAR,
                Material.IRON_INGOT,
                Material.BLAZE_POWDER,

                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,

                Material.GHAST_TEAR,
                Material.IRON_INGOT,
                Material.BLAZE_POWDER
        );
    }

}
