package dev.lbuddyboy.samurai.custom.ability.items.exotic;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.Getter;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
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

import java.util.*;

@Getter
public final class RestraintBone extends AbilityItem implements Listener {

    @Getter private static final Cooldown antiBuild = new Cooldown();

    private static final int HITS = 3; // how many hits it takes for the item to activate
    private static final int ANTI_TIME = 30; // how long the victim is anti-d for in seconds
    private static final int ANTI_BOOST_TIME = 20; // how long the victim is anti-d for in seconds

    private final List<String> materials = Arrays.asList(
            "_GATE",
            "_DOOR",
            "_CHEST",
            "_LEVER",
            "_BUTTON"
    );

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

    public RestraintBone() {
        super("RestraintBone");

        this.name = "restraint_bone";
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

    // returns true if they are currently anti-d
    private boolean refreshAnti(UUID uuid) {

        if (antiBuild.onCooldown(uuid)) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                MessageConfiguration.EXOTIC_BONE_CANT_BUILD.sendListMessage(player, "%remaining%", antiBuild.getRemaining(player));
            }
            return true;
        }

        return false;
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

            if (!canUse(attacker, event)) return;

            if (antiBuild.onCooldown(entity)) {
                attacker.sendMessage(ChatColor.RED + "That player is already under an anti build effect!");
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            antiBuild.applyCooldown(entity, getAntiTime());
            setCooldown(attacker);
            consume(attacker, item);

            MessageConfiguration.EXOTIC_BONE_ATTACKER.sendListMessage(attacker
                    , "%ability-name%", this.getName()
                    , "%target%", entity.getName()
            );

            MessageConfiguration.EXOTIC_BONE_TARGET.sendListMessage(entity
                    , "%ability-name%", this.getName()
                    , "%attacker%", attacker.getName()
            );
        }
    }

    // anti-d listeners
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockMultiPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlaceBucket(PlayerBucketEmptyEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            if (event.getClickedBlock() != null) {
                for (String material : this.materials) {
                    if (event.getClickedBlock().getType().name().contains(material)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler // cleanup attack map
    public void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 45L : 90L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(this.material)
                .name(getName())
                .setLore(CC.translate(this.lore))
                .modelData(6).build();
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}
