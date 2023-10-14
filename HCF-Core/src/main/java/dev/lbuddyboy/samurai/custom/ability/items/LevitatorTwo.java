package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public final class LevitatorTwo extends AbilityItem implements Listener {

    public Cooldown levitation = new Cooldown();
    public int procTime = 10;

    public LevitatorTwo() {
        super("LevitatorTwo");

        this.name = "levitator-two";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());
        levitation.applyCooldown(player.getUniqueId(), this.procTime);
        MessageConfiguration.LEVITATOR_TWO_CLICKER.sendListMessage(player, "%ability-name%", this.getName());

        Tasks.runLater(() -> {
            levitation.cleanUp();
        }, 20L * this.procTime);
        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!levitation.onCooldown(attacker)) return;
        if (ThreadLocalRandom.current().nextInt(15) < 4) return;

        victim.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 35, 1));
        MessageConfiguration.LEVITATOR_TWO_PROCED.sendListMessage(attacker, "%ability-name%", this.getName());
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        this.procTime = config.getInt("procTime", 4);
    }
}
