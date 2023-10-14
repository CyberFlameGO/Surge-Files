package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Levitator extends AbilityItem implements Listener {

    // attacker uuid -> victim id , total hits
    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

    public Levitator() {
        super("Levitator");

        this.name = "levitator";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player attacker) {

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

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < 3) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 110, 2));
            setCooldown(attacker);
            consume(attacker, item);

            MessageConfiguration.LEVITATOR_ATTACKER.sendListMessage(attacker
                    , "%ability-name%", this.getName()
                    , "%target%", entity.getName()
            );

            MessageConfiguration.LEVITATOR_TARGET.sendListMessage(entity
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
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}
