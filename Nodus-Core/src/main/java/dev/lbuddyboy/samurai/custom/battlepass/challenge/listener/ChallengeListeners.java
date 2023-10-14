package dev.lbuddyboy.samurai.custom.battlepass.challenge.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowHandler;
import dev.lbuddyboy.samurai.persist.maps.event.SyncPlaytimeEvent;
import dev.lbuddyboy.samurai.server.event.PlayerIncreaseKillEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ChallengeListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerIncreaseKillEvent(PlayerIncreaseKillEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getKiller());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Samurai.getInstance().getBattlePassHandler().useProgress(event.getPlayer(), progress -> {
            if (progress.isTrackingBlock(event.getBlock().getType())) {
                progress.incrementBlocksMined(event.getBlock().getType());
                Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSyncPlaytimeEvent(SyncPlaytimeEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportWorldEvent(PlayerTeleportEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                Samurai.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                    progress.setVisitedNether(true);
                    progress.requiresSave();

                    Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                });
            } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                Samurai.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                    progress.setVisitedEnd(true);
                    progress.requiresSave();

                    Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getTo());
        if (team != null) {
            if (Samurai.getInstance().getGlowHandler() != null) {
                if (team.getName().equals(GlowHandler.getGlowTeamName())) {
                    Samurai.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                        progress.setVisitGlowstoneMountain(true);
                        progress.requiresSave();

                        Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                    });

                    return;
                }
            }

            if (team.hasDTRBitmask(DTRBitmask.KOTH)) {
                for (Event gameEvent : Samurai.getInstance().getEventHandler().getEvents()) {
                    if (gameEvent.isActive() && gameEvent.getType() == EventType.KOTH && gameEvent.getName().equalsIgnoreCase(team.getName())) {
                        Samurai.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                            progress.setVisitActiveKoth(true);
                            progress.requiresSave();

                            Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                        });
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                if (progress.isTrackingKillsForEntity(event.getEntity().getType())) {
                    progress.incrementEntitiesKilled(event.getEntityType());
                    Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                }
            });
        }
    }

}
