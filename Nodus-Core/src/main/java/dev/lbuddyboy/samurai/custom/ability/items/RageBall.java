package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 17/02/2022 / 9:55 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.partner.impl
 */
public class RageBall extends AbilityItem implements Listener {

    public RageBall() {
        super("RageBall");

        this.name = "rage-ball";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onSnowBallLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg egg && event.getEntity().getShooter() instanceof Player player) {

            if (!isPartnerItem(player.getItemInHand()))
                return;

            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                player.sendMessage(CC.RED + "You cannot use this in spawn!");
                event.setCancelled(true);
                return;
            }

            if (Samurai.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                if (!Samurai.getInstance().getMapHandler().isKitMap()) {

                    event.setCancelled(true);
                    player.sendMessage(CC.translate("&cYou cannot use ability items in the warzone."));
                    return;
                }
            }

            if (isOnCooldown(player)) {
                player.sendMessage(getCooldownMessage(player));
                event.setCancelled(true);
                return;
            }

            setCooldown(player);
            egg.setMetadata("rageball", new FixedMetadataValue(Samurai.getInstance(), true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onSnowBallHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg egg))
            return;

        if (!(event.getEntity() instanceof Egg) && (!(egg.getShooter() instanceof Player))) return;
        Player shooter = (Player) egg.getShooter();
        if (shooter == null) return;

        if (!egg.hasMetadata("rageball"))
            return;

        Team team = Samurai.getInstance().getTeamHandler().getTeam(shooter);

        shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 1));
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 1));

        setGlobalCooldown(shooter);

        MessageConfiguration.RAGE_BALL_ATTACKER.sendListMessage(shooter
                , "%ability-name%", this.getName()
        );

        for (Entity entity : egg.getNearbyEntities(7, 0, 7)) {
            if (entity instanceof Player target) {

                if (target == shooter) continue;

                if (team != null && team.isMember(target.getUniqueId())) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 1));
                    continue;
                }

                if (!canUse(target, null)) continue;

                MessageConfiguration.RAGE_BALL_TARGET.sendListMessage(target
                        , "%ability-name%", this.getName()
                        , "%attacker%", shooter.getName()
                );

                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 8, 1));
            }
        }
    }

}
