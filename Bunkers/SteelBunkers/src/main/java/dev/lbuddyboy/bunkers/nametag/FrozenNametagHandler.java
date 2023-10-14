package dev.lbuddyboy.bunkers.nametag;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.nametag.packet.ScoreboardTeamPacketMod;
import dev.lbuddyboy.bunkers.nametag.thread.NametagThread;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FrozenNametagHandler {

    private static final Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<>();
    @Getter
    private static final List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private static int teamCreateIndex = 1;
    @Getter
    private static final List<NametagProvider> providers = new ArrayList<>();
    @Getter
    private static boolean initiated = false;
    @Getter
    private static boolean async = true;
    @Getter
    private static int updateInterval = 15;
    @Getter
    @Setter
    private static NametagInfo INVISIBLE;

    private FrozenNametagHandler() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;

        Bunkers.getInstance().getServer().getPluginManager().registerEvents(new NametagListener(), Bunkers.getInstance());
        Bukkit.getServer().getScheduler().runTaskLater(Bunkers.getInstance(), () -> setINVISIBLE(FrozenNametagHandler.getOrCreate("INVIS", "", EnumChatFormat.b("WHITE"))), 10);

        new NametagThread().start();
    }

    public static NametagInfo getInvisible(EnumChatFormat color) {
        return FrozenNametagHandler.getOrCreate("INVIS", "", color);
    }

    public static void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);
        providers.sort((a, b) -> Ints.compare(b.getWeight(), a.getWeight()));
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            FrozenNametagHandler.applyUpdate(update);
        }
    }

    public static void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Bunkers.getInstance().getServer().getOnlinePlayers()) {
            FrozenNametagHandler.reloadPlayer(toRefresh, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            FrozenNametagHandler.applyUpdate(update);
        }
    }

    public static void applyUpdate(NametagUpdate nametagUpdate) {
        Player toRefreshPlayer = Bunkers.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());
        if (toRefreshPlayer == null) {
            return;
        }
        if (nametagUpdate.getRefreshFor() == null) {
            for (Player refreshFor : Bunkers.getInstance().getServer().getOnlinePlayers()) {
                FrozenNametagHandler.reloadPlayerInternal(toRefreshPlayer, refreshFor);
            }
        } else {
            Player refreshForPlayer = Bunkers.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());
            if (refreshForPlayer != null) {
                FrozenNametagHandler.reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
        NametagThread.getPendingUpdates().remove(nametagUpdate);
    }

    protected static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("qLibNametag-LoggedIn")) {
            return;
        }
        NametagInfo provided = null;
        int providerIndex = 0;
        while (provided == null) {
            provided = providers.get(providerIndex++).fetchNametag(toRefresh, refreshFor);
        }

        Map<String, NametagInfo> teamInfoMap = new HashMap<>();
        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }
        new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    public static void initiatePlayer(Player player) {
        for (NametagInfo teamInfo : registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }
    }

    public static NametagInfo getOrCreate(String prefix, String suffix, EnumChatFormat color) {
        for (NametagInfo teamInfo : registeredTeams) {
            if (!teamInfo.getPrefix().equals(prefix) || !teamInfo.getSuffix().equals(suffix)) continue;
            return teamInfo;
        }
        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix, color);
        registeredTeams.add(newTeam);
        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        for (Player player : Bunkers.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }
        return newTeam;
    }

    public static Map<String, Map<String, NametagInfo>> getTeamMap() {
        return teamMap;
    }

}

