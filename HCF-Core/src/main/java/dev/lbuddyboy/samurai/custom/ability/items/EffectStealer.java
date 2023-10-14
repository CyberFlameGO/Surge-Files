package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.profile.AbilityProfile;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EffectStealer extends AbilityItem {

    public EffectStealer() {
        super("EffectStealer");

        this.name = "effect-stealer";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        AbilityProfile abilityProfile = AbilityProfile.byUUID(player.getUniqueId());
        Player attacker = Bukkit.getPlayer(abilityProfile.getLastDamagerName());

        if (abilityProfile.getLastDamagerName() == null || attacker == null) {
            player.sendMessage(ChatColor.RED + "The last person who attacked you could not be found!");
            return false;
        }

        Team attackerTeam = Samurai.getInstance().getTeamHandler().getTeam(attacker);
        if (attackerTeam != null && attackerTeam.isMember(player.getUniqueId())) {
            return false;
        }

        if (abilityProfile.getLastDamagedMillis() + 10_000L < System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "It has been more than 10 seconds since that player attacked you!");
            return false;
        }

        attacker.getActivePotionEffects()
                .forEach(potionEffect -> {
                    player.addPotionEffect(new PotionEffect(potionEffect.getType(), 15 * 20, potionEffect.getAmplifier()));
                });

        setCooldown(player);

        MessageConfiguration.EFFECT_STEALER_ATTACKER.sendListMessage(player
                , "%ability-name%", this.getName()
                , "%target%", attacker.getName()
        );

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));

        return true;
    }

}
