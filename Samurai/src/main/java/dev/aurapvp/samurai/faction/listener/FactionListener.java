package dev.aurapvp.samurai.faction.listener;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.SettingsConfiguration;
import dev.aurapvp.samurai.api.event.faction.FactionDisbandEvent;
import dev.aurapvp.samurai.api.event.faction.FactionJoinEvent;
import dev.aurapvp.samurai.api.event.faction.FactionLeaveEvent;
import dev.aurapvp.samurai.api.event.faction.FactionSetHomeEvent;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.common.value.qual.EnumVal;

import java.util.Collections;

public class FactionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (faction == null) return;

        faction.sendMessage(CC.translate(FactionConfiguration.MEMBER_JOINED_BROADCAST.getString(
                FactionConfiguration.MEMBER_FACTION_PLACEHOLDERS(faction.getFactionMember(player.getUniqueId()), faction)
        )));
        faction.info(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (faction == null) return;

        faction.sendMessage(CC.translate(FactionConfiguration.MEMBER_QUIT_BROADCAST.getString(
                FactionConfiguration.MEMBER_FACTION_PLACEHOLDERS(faction.getFactionMember(player.getUniqueId()), faction)
        )));
    }

    @EventHandler
    public void onEntityDamageSafeZone(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            /*
            Damager --> Spawn (Victim)
             */
            Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(victim.getLocation());
            if (factionAt != null) {
                if (factionAt.getType() == FactionType.SPAWN) {
                    event.setCancelled(true);
                    return;
                }
            }
            /*
            Warzone --> Spawn (Melee)
             */
            if (event.getDamager() instanceof Player damager) {
                factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(damager.getLocation());
                if (factionAt == null) return;
                if (factionAt.getType() == FactionType.SPAWN) {
                    event.setCancelled(true);
                    return;
                }
            }
            /*
            Warzone --> Spawn (Projectile)
             */
            if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
                factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(shooter.getLocation());
                if (factionAt == null) return;
                if (factionAt.getType() == FactionType.SPAWN) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(victim.getUniqueId());
        if (faction.getFactionMember(damager.getUniqueId()) == null) return;

        event.setCancelled(true);
        faction.sendMessage(CC.translate(FactionConfiguration.MEMBER_DAMAGE_CANCELLED.getString(
                FactionConfiguration.MEMBER_FACTION_PLACEHOLDERS(faction.getFactionMember(damager.getUniqueId()), faction)
        )));
    }

    @EventHandler
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile instanceof Player shooter)) return;
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(victim.getUniqueId());
        if (faction.getFactionMember(shooter.getUniqueId()) == null) return;

        event.setCancelled(true);
        faction.sendMessage(CC.translate(FactionConfiguration.MEMBER_DAMAGE_CANCELLED.getString(
                FactionConfiguration.MEMBER_FACTION_PLACEHOLDERS(faction.getFactionMember(shooter.getUniqueId()), faction)
        )));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        SamuraiPlayer victimPlayer = Samurai.getInstance().getPlayerHandler().loadPlayer(player.getUniqueId(), true);
        Faction victimFaction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(player.getUniqueId());
        Player killer = player.getKiller();

        if (victimFaction != null) {
            victimFaction.setDtrRegen(System.currentTimeMillis() + (SettingsConfiguration.FACTION_SETTINGS_REGEN_MINUTES.getInt() * 1000L));
            if (victimFaction.getDtr() > -0.99) victimFaction.setDtr(victimFaction.getDtr() - 1);
            victimFaction.triggerUpdate();
        }

        if (killer != null) {
            Faction killerFaction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(killer.getUniqueId());
            SamuraiPlayer killerPlayer = Samurai.getInstance().getPlayerHandler().loadPlayer(killer.getUniqueId(), true);

            if (killerFaction != null) {
                killerFaction.setKills(killerFaction.getKills() + 1);
                killerFaction.triggerUpdate();
            }

            killerPlayer.setKills(killerPlayer.getKills() + 1);
            killerPlayer.updated();
        }

        victimPlayer.setDeaths(victimPlayer.getDeaths() + 1);
        victimPlayer.updated();
    }

    @EventHandler
    public void onFactionJoin(FactionJoinEvent event) {
        Samurai.getInstance().getNameTagHandler().updatePlayersForViewers(Collections.singletonList(event.getSender()), event.getFaction().getOnlinePlayers());
        Samurai.getInstance().getNameTagHandler().updatePlayersForViewers(event.getFaction().getOnlinePlayers(), Collections.singletonList(event.getSender()));
    }

    @EventHandler
    public void onFactionLeave(FactionLeaveEvent event) {
        Samurai.getInstance().getNameTagHandler().updatePlayersForViewers(Collections.singletonList(event.getSender()), event.getFaction().getOnlinePlayers());
        Samurai.getInstance().getNameTagHandler().updatePlayersForViewers(event.getFaction().getOnlinePlayers(), Collections.singletonList(event.getSender()));
    }

    @EventHandler
    public void onFactionSetHome(FactionSetHomeEvent event) {
        Faction faction = event.getFaction();

        Samurai.getInstance().getDynMapImpl().getFactionLayer().upsertMarker(faction);
    }

    @EventHandler
    public void onDisband(FactionDisbandEvent event) {
        Faction faction = event.getFaction();

        if (faction.getHome() != null) {
            Samurai.getInstance().getDynMapImpl().getFactionLayer().deleteMarker(faction);
        }

    }

}
