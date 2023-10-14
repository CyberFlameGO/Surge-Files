package dev.lbuddyboy.samurai.events.conquest.game;

import dev.lbuddyboy.samurai.events.conquest.ConquestHandler;
import dev.lbuddyboy.samurai.events.conquest.enums.ConquestCapzone;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.events.koth.events.EventControlTickEvent;
import dev.lbuddyboy.samurai.events.koth.events.KOTHControlLostEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConquestGame implements Listener {

    @Getter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

    public ConquestGame() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(this, Samurai.getInstance());

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (event.getType() != EventType.KOTH) continue;
            KOTH koth = (KOTH) event;
            if (koth.getName().startsWith(ConquestHandler.KOTH_PREFIX)) {
                if (!koth.isHidden()) {
                    koth.setHidden(true);
                }

                if (koth.getCapTime() != ConquestHandler.TIME_TO_CAP) {
                    koth.setCapTime(ConquestHandler.TIME_TO_CAP);
                }

                koth.activate();
            }
        }

        Samurai.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
        Samurai.getInstance().getConquestHandler().setGame(this);
    }

    public void endGame(final Team winner) {
        if (winner == null) {
            Samurai.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
        } else {
            Samurai.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
        }

        for (Event koth : Samurai.getInstance().getEventHandler().getEvents()) {
            if (koth.getName().startsWith(ConquestHandler.KOTH_PREFIX)) {
                koth.deactivate();
            }
        }

        HandlerList.unregisterAll(this);
        Samurai.getInstance().getConquestHandler().setGame(null);
    }

    @EventHandler
    public void onKOTHCaptured(final EventCapturedEvent event) {

        if (!event.getEvent().getName().startsWith(ConquestHandler.KOTH_PREFIX)) {
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getEvent().getName().replace(ConquestHandler.KOTH_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        teamPoints = sortByValues(teamPoints);
        Samurai.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + team.getName() + ChatColor.GOLD + " captured " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + " and earned a point!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) +
                "/" + ConquestHandler.getPointsToWin() + ")");

        if (teamPoints.get(team.getUniqueId()) >= ConquestHandler.getPointsToWin()) {
            endGame(team);
            ItemStack conquestKey = InventoryUtils.generateKOTHRewardKey("Conquest");
            conquestKey.setAmount(6);
            event.getPlayer().getInventory().addItem(conquestKey);
            if (!event.getPlayer().getInventory().contains(conquestKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), conquestKey);
            }

            team.setConquestsCapped(team.getConquestCapped() + 1);

        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (Samurai.getInstance().getConquestHandler().getGame() != null) {
                        event.getEvent().activate();
                    }
                }

            }.runTaskLater(Samurai.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {

        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_PREFIX)) {
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper() + " was knocked off of " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!");
    }
    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {

        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_PREFIX)) {
            return;
        }


        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_PREFIX, "").toUpperCase());

        Player capper = Samurai.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

        if (capper != null) {
            capper.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!" + ChatColor.AQUA + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (team == null || !teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        teamPoints.put(team.getUniqueId(), Math.max(0, teamPoints.get(team.getUniqueId()) - ConquestHandler.POINTS_DEATH_PENALTY));
        teamPoints = sortByValues(teamPoints);
        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " Your team has lost " + ConquestHandler.POINTS_DEATH_PENALTY + " points because of " + event.getEntity().getName() + "'s death!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) + "/" + ConquestHandler.getPointsToWin() + ")");
    }

    private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();
        Iterator<Map.Entry<ObjectId, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<ObjectId, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

}