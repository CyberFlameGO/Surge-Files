package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandAlias("fres|fireres|fr")
@CommandPermission("foxtrot.fres")
public class FresCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        if (sender.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            sender.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        } else {
            sender.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
        }
    }

}
