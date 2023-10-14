package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.items.exotic.RestraintBone;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public final class AntiBuildStick extends AbilityItem implements Listener {

    private final int HITS = 3; // how many hits it takes for the item to activate
    private final int ANTI_TIME = 15; // how long the victim is anti-d for in seconds
    private final int ANTI_BOOST_TIME = 20; // how long the victim is anti-d for in seconds

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

    public AntiBuildStick() {
        super("AntiBuildStick");

        this.name = "antibuildstick";
    }

    @Override
    public String getName() {
        return CC.translate(this.displayName);
    }

    @Override
    public int getAmount() {
        return this.defaultAmount;
    }

    public int getAntiTime() {
        return SOTWCommand.isPartnerPackageHour() ? ANTI_BOOST_TIME : ANTI_TIME;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player attacker) {
            if (attacker.getWorld().getEnvironment() == World.Environment.THE_END || attacker.getWorld().getEnvironment() == World.Environment.NETHER)
                return;

            Pair<UUID, UUID> key = new Pair<>(attacker.getUniqueId(), entity.getUniqueId());

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (attackMap.containsKey(key) && !partnerItem) {
                attackMap.remove(key);
                return;
            }

            if (!partnerItem) {
                return;
            }

            if (RestraintBone.getAntiBuild().onCooldown(entity)) {
                attacker.sendMessage(ChatColor.RED + "That player is already anti-d!");
                return;
            }

            if (!canUse(attacker, event)) return;

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            RestraintBone.getAntiBuild().applyCooldown(entity, getAntiTime());
            setCooldown(attacker);
            consume(attacker, item);

            MessageConfiguration.ANTI_BUILD_STICK_ATTACKER.sendListMessage(attacker
                    , "%ability-name%", this.getName()
                    , "%target%", entity.getName()
            );

            MessageConfiguration.ANTI_BUILD_STICK_TARGET.sendListMessage(entity
                    , "%ability-name%", this.getName()
                    , "%attacker%", attacker.getName()
            );
        }
    }

    @EventHandler // cleanup attack map
    public void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return this.cooldownSeconds;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(this.material)
                .name(getName())
                .setLore(CC.translate(this.lore))
                .modelData(6).build();
    }

    @Override
    public ShapedRecipe getRecipe() {
        NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(this.name.toLowerCase().replace("'", "").replace(" ", "_")));
        ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

        recipe.shape("ABC", "GHG", "DEF");
        recipe.setIngredient('A', Material.STONE);
        recipe.setIngredient('B', Material.DIAMOND_PICKAXE);
        recipe.setIngredient('C', Material.OAK_FENCE_GATE);
        recipe.setIngredient('D', Material.REDSTONE);
        recipe.setIngredient('E', Material.OAK_TRAPDOOR);
        recipe.setIngredient('F', Material.OAK_PRESSURE_PLATE);
        recipe.setIngredient('G', Material.CHEST);
        recipe.setIngredient('H', Material.STICK);

        return recipe;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return Arrays.asList(
                Material.STONE,
                Material.DIAMOND_PICKAXE,
                Material.OAK_FENCE_GATE,

                Material.CHEST,
                Material.STICK,
                Material.CHEST,

                Material.REDSTONE,
                Material.OAK_TRAPDOOR,
                Material.OAK_PRESSURE_PLATE
        );
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}
