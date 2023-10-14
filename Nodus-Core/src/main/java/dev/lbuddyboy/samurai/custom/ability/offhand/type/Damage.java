package dev.lbuddyboy.samurai.custom.ability.offhand.type;

import dev.lbuddyboy.samurai.custom.ability.offhand.OffHand;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Damage extends OffHand {

    private int strengthSeconds, regenerationSeconds;

    public Damage() {
        super("Damage", true, "damage");
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (this.cooldown.onCooldown(victim)) return;
        if (ThreadLocalRandom.current().nextInt(100) > 8) return;

        victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * regenerationSeconds, 1));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * strengthSeconds, 1));
        this.cooldown.applyCooldown(victim, this.cooldownSeconds);
        victim.sendMessage(CC.translate("&6&lOFFHAND ITEMS &7" + CC.UNICODE_ARROWS_RIGHT + " &fYour offhand damage item has just proced."));
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        this.strengthSeconds = this.config.getInt("strength-seconds");
        this.regenerationSeconds = this.config.getInt("regen-seconds");
    }
}
