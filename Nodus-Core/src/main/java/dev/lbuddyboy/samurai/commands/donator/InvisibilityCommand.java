package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandAlias("invisibility|invis")
@CommandPermission("foxtrot.invis")
public class InvisibilityCommand extends BaseCommand {

    @Default
    public void invis(Player sender) {
        if (sender.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            sender.removePotionEffect(PotionEffectType.INVISIBILITY);
        } else {
            sender.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, true, false));
        }
    }

}
