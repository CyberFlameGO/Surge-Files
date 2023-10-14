package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandAlias("sp|speed")
@CommandPermission("foxtrot.speed")
public class SpeedCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        if (sender.hasPotionEffect(PotionEffectType.SPEED)) {
            sender.removePotionEffect(PotionEffectType.SPEED);
        } else {
            sender.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        }
    }

}
