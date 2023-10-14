package dev.lbuddyboy.samurai.custom.teamwar.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.command.TeamWarCommand;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarState;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

import java.util.Arrays;
import java.util.List;

public class TeamWarListener implements Listener {

    private static final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "msg",
            "m",
            "message",
            "tell",
            "r",
            "f c p",
            "reply",
            "teamwar classes",
            "teamwar end",
            "teamwar leave",
            "tpm",
            "togglemessages"
    );

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (event.getBlock().getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (event.getPlayer().hasMetadata("Build")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBuild(BlockBreakEvent event) {
        if (event.getBlock().getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (event.getPlayer().hasMetadata("Build")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (event.getPlayer().hasMetadata("Build")) return;

        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();

        if (teamA == null || teamB == null) {
            event.setCancelled(true);
            return;
        }

        if (state == WarState.FIGHTING) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (victim.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;

        process(victim, damager, event);
    }

    @EventHandler
    public void onDamageProj(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (victim.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;

        process(victim, damager, event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity(), killer = victim.getKiller();
        if (victim.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        WarTeam victimTeam = Samurai.getInstance().getTeamWarHandler().getWarTeam(victim.getUniqueId());
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();

        if (victimTeam != null && teamA != null && teamB != null) {
            if (victimTeam == teamA || victimTeam == teamB) {
                victimTeam.death(victim, killer);
                return;
            }
        }

        event.getDrops().clear();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player victim = event.getPlayer();
        if (victim.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        WarTeam victimTeam = Samurai.getInstance().getTeamWarHandler().getWarTeam(victim.getUniqueId());
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();

        if (victimTeam != null && teamA != null && teamB != null) {
            if (victimTeam == teamA || victimTeam == teamB) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        WarTeam victimTeam = Samurai.getInstance().getTeamWarHandler().getWarTeam(player.getUniqueId());
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();

        if (victimTeam != null && teamA != null && teamB != null) {
            if (victimTeam == teamA || victimTeam == teamB) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        if (Samurai.getInstance().getTeamWarHandler().getState() == WarState.NOT_STARTED) return;
        if (event.getFrom() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (event.getPlayer().getWorld() == Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (event.getPlayer().hasMetadata("Build")) return;
        if (event.getPlayer().hasMetadata("leaving")) return;

        event.getPlayer().teleport(event.getFrom().getSpawnLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;

        Samurai.getInstance().getTeamWarHandler().handleQuit(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (player.isOp()) return;

        for (String s : ALLOWED_COMMANDS) {
            if (event.getMessage().replaceAll("/", "").equalsIgnoreCase(s)) {
                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cThat command is not allowed here."));
    }

    @EventHandler
    public void onLoss(FoodLevelChangeEvent event) {
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();
        HumanEntity player = event.getEntity();

        if (teamA == null || teamB == null) {
            event.setCancelled(true);
            return;
        }

        if (state != WarState.FIGHTING) {
            event.setCancelled(true);
            return;
        }

        if (teamA.getMembers().containsKey(player.getUniqueId()) || teamB.getMembers().containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();
        HumanEntity player = event.getWhoClicked();

        if (teamA == null || teamB == null) {
            event.setCancelled(true);
            return;
        }

        if (state != WarState.FIGHTING) {
            event.setCancelled(true);
            return;
        }

        if (teamA.getMembers().containsKey(player.getUniqueId()) || teamB.getMembers().containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();
        Player player = event.getPlayer();

        if (player.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (state == WarState.NOT_STARTED) return;
        if (event.getItem() == null) return;
        if (!event.getItem().isSimilar(Samurai.getInstance().getTeamWarHandler().getCLASS_EDITOR_ITEM())) return;

        TeamWarCommand.classes(player);
    }

    @EventHandler
    public void onPlayerInteractLeave(PlayerInteractEvent event) {
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();
        Player player = event.getPlayer();

        if (player.getWorld() != Samurai.getInstance().getTeamWarHandler().getWorld()) return;
        if (state == WarState.NOT_STARTED) return;
        if (event.getItem() == null) return;
        if (!event.getItem().isSimilar(Samurai.getInstance().getTeamWarHandler().getLEAVE_ITEM())) return;

        TeamWarCommand.leave(player);
    }

    public void process(Player victim, Player damager, EntityDamageByEntityEvent event) {
        WarTeam teamA = Samurai.getInstance().getTeamWarHandler().getFightingA();
        WarTeam teamB = Samurai.getInstance().getTeamWarHandler().getFightingB();
        WarState state = Samurai.getInstance().getTeamWarHandler().getState();

        if (teamA == null || teamB == null) {
            event.setCancelled(true);
            return;
        }

        if (state != WarState.FIGHTING) {
            event.setCancelled(true);
            return;
        }

        if (teamA.getMembers().containsKey(damager.getUniqueId())) {
            if (teamB.getMembers().containsKey(victim.getUniqueId())) return;
        }

        if (teamB.getMembers().containsKey(damager.getUniqueId())) {
            if (teamA.getMembers().containsKey(victim.getUniqueId())) return;
        }

        event.setCancelled(true);
    }

}
