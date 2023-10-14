package dev.lbuddyboy.samurai.custom.arena.listener;

import dev.lbuddyboy.flash.FlashLanguage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.arena.ArenaConstants;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.net.http.WebSocket;
import java.util.Objects;
import java.util.UUID;

public class ArenaListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity();

        for (Entity entity : victim.getWorld().getEntities()) {
            if (!(entity instanceof EnderPearl pearl)) continue;

            pearl.remove();
        }

        if (killer == null) return;

        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(event.getEntity().getUniqueId())) return;

        Samurai.getInstance().getDeathbanMap().reduce(killer.getUniqueId(), (300_000L));
        long seconds = (Samurai.getInstance().getDeathbanMap().getDeathban(killer.getUniqueId()) - System.currentTimeMillis()) / 1000;

        if (seconds <= 0) {
            Samurai.getInstance().getDeathbanMap().set(killer.getUniqueId(), 0);
            Samurai.getInstance().getDeathbanMap().revive(killer.getUniqueId());
            killer.sendMessage(CC.translate("&aYour deathban has expired due to killing a player."));
            return;
        }

        killer.sendMessage(CC.translate("&aYour deathban has been reduced by 5 minutes due to you getting a kill."));
        killer.spigot().respawn();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(event.getPlayer().getUniqueId())) return;

        event.setRespawnLocation(Samurai.getInstance().getArenaHandler().getSpawn());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (Samurai.getInstance().getArenaHandler().getSafeZone() == null) return;
        if (Samurai.getInstance().getArenaHandler().getSafeZone().contains(Objects.requireNonNull(event.getTo()))) {
            if (!SpawnTagHandler.isTagged(player)) return;

            EnderpearlCooldownHandler.clearEnderpearlTimer(player);
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot do that whilst spawn tagged."));
        }
    }

    @EventHandler
    public void onTeleportOut(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (Samurai.getInstance().getArenaHandler().getArenaTeam() == null) return;
        if (Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().isEmpty()) return;
        if (Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().get(0).contains(player)) return;
        if (Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().get(0).contains(event.getTo())) return;
        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) return;

        event.setTo(event.getFrom());
        player.sendMessage(CC.translate("&cYou must stay in the deathban arena until your deathban is over."));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (Samurai.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            for (String s : ArenaConstants.ALLOWED_COMMANDS) {
                if (event.getMessage().replaceAll("/", "").equalsIgnoreCase(s)) {
                    return;
                }
            }

            event.setCancelled(true);
            player.sendMessage(CC.translate("&cThat command is not allowed here."));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) {
            if (Samurai.getInstance().getArenaHandler().wasDeathbanned(player.getUniqueId())) {
                Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                    Samurai.getInstance().getDeathbanMap().revive(player.getUniqueId());
                });
                Samurai.getInstance().getArenaHandler().getUuids().remove(player.getUniqueId());
            }
        }

        if (Samurai.getInstance().getArenaHandler().getArenaTeam() == null) return;
        if (Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().isEmpty()) return;
        if (!Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().get(0).contains(player)) return;

        Samurai.getInstance().getDeathbanMap().revive(player.getUniqueId());
    }
}
