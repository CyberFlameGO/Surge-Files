package dev.lbuddyboy.samurai.events.koth.listeners;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.events.citadel.commands.CitadelCommand;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.events.koth.commands.koth.KOTHCommand;
import dev.lbuddyboy.samurai.events.koth.events.EventControlTickEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        if (event.getKOTH().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = event.getKOTH();
        if (koth.getRemainingCapTime() % 180 == 0 && koth.getRemainingCapTime() <= (koth.getCapTime() - 30)) {
            if (koth.getName().equals(VaultHandler.TEAM_NAME)) {
                Samurai.getInstance().getServer().broadcastMessage(CC.translate(VaultHandler.PREFIX) + " " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " is trying to be controlled.");
            } else {
                Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " is trying to be controlled.");
            }
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()));
        }
    }

/*    @EventHandler
    public void onCapture(EventCapturedEvent event) {
        if (event.getEvent().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = (KOTH) event.getEvent();
        if (!koth.getName().equalsIgnoreCase("EOTW")) return;

        EOTWCommand.ffa(Bukkit.getConsoleSender(), 5);
    }*/

    @EventHandler
    public void onCaptureCitadel(EventCapturedEvent event) {
        if (event.getEvent().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = (KOTH) event.getEvent();
        if (!koth.getName().equalsIgnoreCase("Citadel")) return;

        if (Samurai.getInstance().getCitadelHandler().getCitadelChests().isEmpty()) {
            CitadelCommand.citadelRescanChests(Bukkit.getConsoleSender());
        }
        CitadelCommand.citadelRespawnChests(Bukkit.getConsoleSender());
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        ItemStack stack = event.getItem();
        Player player = event.getPlayer();
        if (!stack.isSimilar(KOTHCommand.kothTicket)) return;

        KOTH active = null;
        for (Event otherKoth : Samurai.getInstance().getEventHandler().getEvents()) {
            if (otherKoth instanceof KOTH && otherKoth.isActive()) {
                active = (KOTH) otherKoth;
            }
        }

        if (active == null) {
            player.sendMessage(ChatColor.RED + "There is no active KoTH.");
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
        Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
        Collection<Player> players = (team != null ? team.getOnlineMembers() : List.of(player));

        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && teamAt != null && !teamAt.getOnlineMembers().contains(player)) {
            player.sendMessage(CC.translate("&aYour team needs to be in a safe zone."));
            return;
        }

        player.sendMessage(CC.translate("&7 "));
        player.sendMessage(CC.translate("&4&lKoTH Ticket"));
        player.sendMessage(CC.translate("&7 "));
        player.sendMessage(CC.translate("&7If your team moves. The teleport will"));
        player.sendMessage(CC.translate("&7be cancelled."));
        player.sendMessage(CC.translate("&7 "));
        player.sendMessage(CC.translate("&7Teleporting in: &g30 seconds"));
        player.sendMessage(CC.translate("&7 "));

        for (Player p : players) {
            locations.put(p.getUniqueId(), p.getLocation());
            countdown.applyCooldown(p, 30);
        }

        InventoryUtils.removeAmountFromInventory(player.getInventory(), stack, 1);

        final int[] ticks = {30};
        KOTH finalActive = active;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticks[0] <= 0) {
                    for (Player p : players) {
                        p.teleport(new Location(Bukkit.getWorld(finalActive.getWorld()), finalActive.getCapLocation().getBlockX(), finalActive.getCapLocation().getBlockY(), finalActive.getCapLocation().getBlockZ()));
                        locations.remove(p.getUniqueId());
                    }
                    cancel();
                    return;
                }
                for (Player p : players) {
                    Location location = locations.get(p.getUniqueId());
                    if (location == null) continue;
                    if (location.getBlockX() == p.getLocation().getBlockX()
                            && location.getBlockZ() == p.getLocation().getBlockZ()
                            && location.getBlockY() == p.getLocation().getBlockY()) continue;

                    cancel();
                    players.forEach(other -> {
                        other.sendMessage(CC.translate("&cKoTH Ticket teleportation has been cancelled due to " + p.getName() + " moving."));
                        countdown.removeCooldown(other);
                    });
                    KOTHCommand.givekothticket(Bukkit.getConsoleSender(), new OnlinePlayer(player), 1);
                    break;
                }
                ticks[0]--;
            }
        }.runTaskTimer(Samurai.getInstance(), 20, 20);


    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        locations.remove(event.getPlayer().getUniqueId());
    }

    public static final Cooldown countdown = new Cooldown();
    private static final Map<UUID, Location> locations = new HashMap<>();
}