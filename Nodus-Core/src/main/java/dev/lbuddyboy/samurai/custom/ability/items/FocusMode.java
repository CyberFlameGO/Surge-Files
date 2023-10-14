package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class FocusMode extends AbilityItem implements Listener {

    private final int HITS = 3; // how many hits it takes for the item to activate
    private final int FOCUS_TIME = 15; // how long the victim is anti-d for in seconds
    private final Cooldown focus = new Cooldown();

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
    private final Map<UUID, UUID> focusedAttackerMap = new HashMap<>(), focusedVictimMap = new HashMap<>();

    public FocusMode() {
        super("FocusMode");

        this.name = "focus-mode";
    }

    @Override
    public String getName() {
        return CC.translate(this.displayName);
    }

    @Override
    public int getAmount() {
        return this.defaultAmount;
    }

    public int getFocusTime() {
        return FOCUS_TIME;
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

            if (this.focusedVictimMap.containsKey(entity.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "That player is already under a focus mode!");
                return;
            }

            if (ArcherClass.isMarked(entity)) {
                attacker.sendMessage(ChatColor.RED + "That player is archer marked and you cannot focus mode them!");
                return;
            }

            if (!canUse(attacker, event)) return;

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            this.focus.applyCooldown(entity, getFocusTime());
            this.focusedAttackerMap.put(attacker.getUniqueId(), entity.getUniqueId());
            this.focusedVictimMap.put(entity.getUniqueId(), attacker.getUniqueId());
            setCooldown(attacker);
            consume(attacker, item);

            MessageConfiguration.FOCUS_MODE_ATTACKER.sendListMessage(attacker
                    , "%ability-name%", this.getName()
                    , "%target%", entity.getName()
            );

            MessageConfiguration.FOCUS_MODE_TARGET.sendListMessage(entity
                    , "%ability-name%", this.getName()
                    , "%attacker%", attacker.getName()
            );

            Tasks.runLater(() -> {
                focus.cleanUp();
                focusedAttackerMap.remove(attacker.getUniqueId());
                focusedVictimMap.remove(entity.getUniqueId());
            }, 20 * getFocusTime());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!focusedAttackerMap.containsKey(attacker.getUniqueId())) return;
        if (!focus.onCooldown(attacker.getUniqueId())) return;
        if (!focusedAttackerMap.get(attacker.getUniqueId()).equals(victim.getUniqueId())) return;
        if (ArcherClass.isMarked(victim)) return;

        event.setDamage(event.getDamage() * 1.20);
        System.out.println("Focus mode attacked");
    }

    @EventHandler // cleanup attack map
    public void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
        this.cooldown.cleanUp();
        this.focusedAttackerMap.remove(event.getPlayer().getUniqueId());
        this.focusedVictimMap.remove(event.getPlayer().getUniqueId());
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
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}
