package dev.lbuddyboy.samurai.custom.ability.offhand.type;

import dev.lbuddyboy.samurai.custom.ability.offhand.OffHand;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Debuff extends OffHand {

    private final Cooldown cooldown;
    private int slownessSeconds, poisonSeconds;

    public Debuff() {
        super("Debuff", false, "debuff");
        this.cooldown = new Cooldown();
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (this.cooldown.onCooldown(victim)) return;

        if (!(event.getDamager() instanceof Player)) {
            if (!(event.getDamager() instanceof Projectile projectile)) return;
            if (!(projectile.getShooter() instanceof Player shooter)) return;
            if (ThreadLocalRandom.current().nextInt(100) > 8) return;

            shooter.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * this.poisonSeconds, 0));
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * this.slownessSeconds, 0));
            this.cooldown.applyCooldown(victim, this.cooldownSeconds);
            victim.sendMessage(CC.translate("&6&lOFFHAND ITEMS &7" + CC.UNICODE_ARROWS_RIGHT + " &fYour offhand debuff item has just affected " + shooter.getName() + "."));
            return;
        }
        if (!(event.getDamager() instanceof Player damager)) return;
        if (ThreadLocalRandom.current().nextInt(100) > 8) return;

        damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * this.poisonSeconds, 0));
        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * this.slownessSeconds, 0));
        this.cooldown.applyCooldown(victim, this.cooldownSeconds);
        victim.sendMessage(CC.translate("&6&lOFFHAND ITEMS &7" + CC.UNICODE_ARROWS_RIGHT + " &fYour offhand debuff item has just affected " + damager.getName() + "."));
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        this.poisonSeconds = this.config.getInt("poison-seconds");
        this.slownessSeconds = this.config.getInt("slowness-seconds");
    }
}
