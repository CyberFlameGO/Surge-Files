package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class RageMode extends AbilityItem implements Listener {

    public Cooldown raging = new Cooldown();
    private Map<UUID, Integer> hits = new HashMap<>();
    public int procTime = 10;

    public RageMode() {
        super("RageMode");

        this.name = "ragemode";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());
        raging.applyCooldown(player.getUniqueId(), this.procTime);
        MessageConfiguration.RAGE_MODE_CLICKER.sendListMessage(player, "%ability-name%", this.getName());

        Tasks.runLater(() -> {
            PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * this.hits.getOrDefault(player.getUniqueId(), 1), 1));
            PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * this.hits.getOrDefault(player.getUniqueId(), 1), 1));
            raging.cleanUp();
        }, 20L * this.procTime);
        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!raging.onCooldown(victim)) return;
        if (this.hits.getOrDefault(victim.getUniqueId(), 1) >= 10) return;

        this.hits.put(victim.getUniqueId(), this.hits.getOrDefault(victim.getUniqueId(), 0) + 1);
        int hits = this.hits.get(victim.getUniqueId());
        MessageConfiguration.RAGE_MODE_PROCED.sendListMessage(victim, "%ability-name%", this.getName(), "%hits%", hits);
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        this.procTime = config.getInt("procTime", 10);
    }
}
