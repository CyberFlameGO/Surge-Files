package dev.lbuddyboy.samurai.custom.power.listener;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PowerListener implements Listener {

    public static Cooldown powerCooldown = new Cooldown();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) return;

        Player p = event.getPlayer();

        if (!p.isSneaking()) return;
        if (p.hasMetadata("modmode") || p.hasMetadata("invisible")) return;
        if (LandBoard.getInstance().getTeam(p.getLocation()) != null && LandBoard.getInstance().getTeam(p.getLocation()).hasDTRBitmask(DTRBitmask.SAFE_ZONE)) return;
        if (powerCooldown.onCooldown(p)) return;
        if (Feature.POWER.isDisabled()) {
            return;
        }

        if (Samurai.getInstance().getPowerHandler().getPower(p).equalsIgnoreCase("Strength")) {
            int random = new Random().nextInt(50);
            int time1 = 7;
            int time2 = 7;
            if (random < 25) {
                p.sendMessage(CC.translate("&aYou have just used your &4Aggressive &a power! &oYou now have Strength 2 for " + time1 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * time1, 1));

            } else {
                p.sendMessage(CC.translate("&aYou have just used your &4Aggressive &a power! &oYou now have Speed 3 for " + time2 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.SPEED, 20 * time2, 2));
            }
        } else if (Samurai.getInstance().getPowerHandler().getPower(p).equalsIgnoreCase("Trapper")) {
            int random = new Random().nextInt(50);
            int time1 = 5;
            int time2 = 8;
            if (random < 25) {
                p.sendMessage(CC.translate("&aYou have just used your &dTrapper &a power! &oYou now have Resistance 3 for " + time1 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * time1, 2));
            } else {
                p.sendMessage(CC.translate("&aYou have just used your &dTrapper &a power! &oYou now have Poison 1 for " + time2 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.POISON, 20 * time2, 0));

            }
        } else if (Samurai.getInstance().getPowerHandler().getPower(p).equalsIgnoreCase("Mixture")) {
            int random = new Random().nextInt(50);
            int time1 = 5;
            int time2 = 8;
            if (random < 25) {
                p.sendMessage(CC.translate("&aYou have just used your &3Mixture &a power! &oYou now have Absorption 3 for " + time1 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.ABSORPTION, 20 * time1, 2));
            } else {
                p.sendMessage(CC.translate("&aYou have just used your &3Mixture &a power! &oYou now have Strength 2 for " + time2 + " seconds."));

                PvPClass.setRestoreEffect(p, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * time2, 1));
            }
        }

        powerCooldown.applyCooldown(p, 120);
    }

}
