package dev.lbuddyboy.samurai.map.stats;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import dev.lbuddyboy.libs.lLib;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.map.stats.command.LeaderboardCommand;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.ftop.FTopHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class StatsHandler implements Listener {

    private final Map<UUID, StatsEntry> stats = Maps.newConcurrentMap();
    private final Map<Location, Integer> leaderboardSigns = Maps.newHashMap();
    private final Map<Location, Integer> leaderboardHeads = Maps.newHashMap();
    private final Map<Location, LeaderboardCommand.StatsObjective> objectives = Maps.newHashMap();
    private final Map<Integer, UUID> topKills = Maps.newConcurrentMap();

    private boolean firstUpdateComplete = false;

    public StatsHandler() {

        try (Jedis redis = lLib.getInstance().getJedisPool().getResource()) {
            for (String key : redis.keys(Samurai.getMONGO_DB_NAME() + ":" + "stats:*")) {
                UUID uuid = UUID.fromString(key.split(":")[2]);
                StatsEntry entry = Samurai.PLAIN_GSON.fromJson(redis.get(key), StatsEntry.class);

                stats.put(uuid, entry);
            }

            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + stats.size() + " stats.");

            try {
                List<String> serializedSigns = Samurai.PLAIN_GSON.fromJson(redis.get(Samurai.getMONGO_DB_NAME() + ":" + "leaderboardSigns"), new TypeToken<List<String>>() {
                }.getType());

                for (String sign : serializedSigns) {
                    Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(sign.split("----")[0]));
                    int place = Integer.parseInt(sign.split("----")[1]);

                    leaderboardSigns.put(location, place);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCTeams] Loaded " + leaderboardSigns.size() + " leaderboard signs.");
            } catch (Exception e) {
                System.out.println("[HCTeams] Error loading head signs");
            }

            try {
                List<String> serializedHeads = Samurai.PLAIN_GSON.fromJson(redis.get(Samurai.getMONGO_DB_NAME() + ":" + "leaderboardHeads"), new TypeToken<List<String>>() {
                }.getType());

                for (String sign : serializedHeads) {
                    Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(sign.split("----")[0]));
                    int place = Integer.parseInt(sign.split("----")[1]);

                    leaderboardHeads.put(location, place);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + leaderboardHeads.size() + " leaderboard heads.");
            } catch (Exception e) {
                System.out.println("[HCTeams] Error loading leaderboard heads");
            }

            try {
                List<String> serializedObjectives = Samurai.PLAIN_GSON.fromJson(redis.get(Samurai.getMONGO_DB_NAME() + ":" + "objectives"), new TypeToken<List<String>>() {
                }.getType());

                for (String objective : serializedObjectives) {
                    Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(objective.split("----")[0]));
                    LeaderboardCommand.StatsObjective obj = LeaderboardCommand.StatsObjective.valueOf(objective.split("----")[1]);

                    objectives.put(location, obj);
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + objectives.size() + " objectives.");
            } catch (Exception e) {
                System.out.println("[HCTeams] Error loading head signs");
            }
        }

        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Samurai.getInstance(), this::save, 30 * 20L, 30 * 20L);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Samurai.getInstance(), this::updateTopKillsMap, 30 * 20L, 30 * 20L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Samurai.getInstance(), this::updatePhysicalLeaderboards, 60 * 20L, 60 * 20L);
    }

    public void save() {
        try (Jedis jedis = lLib.getInstance().getJedisPool().getResource()) {
            List<String> serializedSigns = leaderboardSigns.entrySet().stream().map(entry -> LocationSerializer.serialize(entry.getKey()).toString() + "----" + entry.getValue()).collect(Collectors.toList());
            List<String> serializedHeads = leaderboardHeads.entrySet().stream().map(entry -> LocationSerializer.serialize(entry.getKey()).toString() + "----" + entry.getValue()).collect(Collectors.toList());
            List<String> serializedObjectives = objectives.entrySet().stream().map(entry -> LocationSerializer.serialize(entry.getKey()).toString() + "----" + entry.getValue().name()).collect(Collectors.toList());

            jedis.set(Samurai.getMONGO_DB_NAME() + ":" + "leaderboardSigns", Samurai.PLAIN_GSON.toJson(serializedSigns));
            jedis.set(Samurai.getMONGO_DB_NAME() + ":" + "leaderboardHeads", Samurai.PLAIN_GSON.toJson(serializedHeads));
            jedis.set(Samurai.getMONGO_DB_NAME() + ":" + "objectives", Samurai.PLAIN_GSON.toJson(serializedObjectives));

            StatsEntry newFirst = get(LeaderboardCommand.StatsObjective.KILLS, 1);
            StatsEntry newSecond = get(LeaderboardCommand.StatsObjective.KILLS, 2);
            StatsEntry newThird = get(LeaderboardCommand.StatsObjective.KILLS, 3);

            if (newFirst != null) {
                jedis.set("firstKills", String.valueOf(FrozenUUIDCache.name(newFirst.getOwner()) + ":" + newFirst.getKills()) + ":" + newFirst.getOwner().toString());
            }
            if (newSecond != null) {
                jedis.set("secondKills", String.valueOf(FrozenUUIDCache.name(newSecond.getOwner()) + ":" + newSecond.getKills() + ":" + newSecond.getOwner().toString()));
            }
            if (newThird != null) {
                jedis.set("thirdKills", String.valueOf(FrozenUUIDCache.name(newThird.getOwner()) + ":" + newThird.getKills() + ":" + newThird.getOwner().toString()));
            }

            FTopHandler top = Samurai.getInstance().getTopHandler();
            ArrayList<Team> teams = new ArrayList<>(Samurai.getInstance().getTeamHandler().getTeams());
            teams.removeIf(team -> team.getOwner() == null);
            teams.sort(Collections.reverseOrder(Comparator.comparingInt(top::getTotalPoints)));

            for (int i = 0; i < teams.size(); i++) {
                if (i > 4) break;
                Team team = teams.get(i);
                jedis.set("ftop" + i, team.getName() + ":" + top.getTotalPoints(team) + ":" + team.getOwner().toString());
            }

            // stats
            for (StatsEntry entry : stats.values()) {
                jedis.set(Samurai.getMONGO_DB_NAME() + ":" + "stats:" + entry.getOwner().toString(), Samurai.PLAIN_GSON.toJson(entry));
            }
        }
    }

    public StatsEntry getStats(Player player) {
        return getStats(player.getUniqueId());
    }

    public StatsEntry getStats(String name) {
        return getStats(UUIDUtils.uuid(name));
    }

    public StatsEntry getStats(UUID uuid) {
        stats.putIfAbsent(uuid, new StatsEntry(uuid));
        return stats.get(uuid);
    }

    private void updateTopKillsMap() {
        UUID oldFirstPlace = this.topKills.get(1);
        UUID oldSecondPlace = this.topKills.get(2);
        UUID oldThirdPlace = this.topKills.get(3);

        StatsEntry newFirst = get(LeaderboardCommand.StatsObjective.KILLS, 1);
        StatsEntry newSecond = get(LeaderboardCommand.StatsObjective.KILLS, 2);
        StatsEntry newThird = get(LeaderboardCommand.StatsObjective.KILLS, 3);

        UUID newFirstPlace = newFirst == null ? null : newFirst.getOwner();
        UUID newSecondPlace = newSecond == null ? null : newSecond.getOwner();
        UUID newThirdPlace = newThird == null ? null : newThird.getOwner();

        List<UUID> toUpdate = new ArrayList<>();
        if (!SOTWCommand.isSOTWTimer()) {
            if (firstUpdateComplete) {
                if (newFirstPlace != null && oldFirstPlace != null && newFirstPlace != oldFirstPlace) {
                    Bukkit.broadcastMessage(CC.translate("&g" + UUIDUtils.name(newFirstPlace) + "&f has surpassed &g" + UUIDUtils.name(oldFirstPlace) + "&f for &g#1&f in kills!"));
                    toUpdate.add(newFirstPlace);
                    toUpdate.add(oldFirstPlace);
                }

                if (newSecondPlace != null && oldSecondPlace != null && newSecondPlace != oldSecondPlace) {
                    Bukkit.broadcastMessage(CC.translate("&g" + UUIDUtils.name(newSecondPlace) + "&f has surpassed &g" + UUIDUtils.name(oldSecondPlace) + "&f for &g#2&f in kills!"));
                    toUpdate.add(newSecondPlace);
                    toUpdate.add(oldSecondPlace);
                }

                if (newThirdPlace != null && oldThirdPlace != null && newThirdPlace != oldThirdPlace) {
                    Bukkit.broadcastMessage(CC.translate("&g" + UUIDUtils.name(newThirdPlace) + "&f has surpassed &g" + UUIDUtils.name(oldThirdPlace) + "&f for &g#3&f in kills!"));
                    toUpdate.add(newThirdPlace);
                    toUpdate.add(oldThirdPlace);
                }
            }
        }

        updateTop(1, newFirstPlace);
        updateTop(2, newSecondPlace);
        updateTop(3, newThirdPlace);

        this.firstUpdateComplete = true;

        for (UUID uuid : toUpdate) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                    FrozenNametagHandler.reloadPlayer(player);
                });
            }
        }

    }

    private void updateTop(int i, UUID id) {
        if (id == null)
            this.topKills.remove(i);
        else
            this.topKills.put(i, id);
    }

    public void updatePhysicalLeaderboards() {
        Iterator<Map.Entry<Location, Integer>> iterator = leaderboardSigns.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, Integer> entry = iterator.next();

            StatsEntry stats = get(objectives.get(entry.getKey()), entry.getValue());

            if (stats == null) {
                continue;
            }

            if (!(entry.getKey().getBlock().getState() instanceof Sign)) {
                iterator.remove();
                continue;
            }

            Sign sign = (Sign) entry.getKey().getBlock().getState();

            sign.setLine(0, trim(ChatColor.RED.toString() + ChatColor.BOLD + (beautify(entry.getKey()))));
            sign.setLine(1, trim(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + UUIDUtils.name(stats.getOwner())));

            sign.setLine(3, ChatColor.DARK_GRAY.toString() + stats.get(objectives.get(entry.getKey())));

            sign.update();
        }

        Iterator<Map.Entry<Location, Integer>> headIterator = leaderboardHeads.entrySet().iterator();

        while (headIterator.hasNext()) {
            Map.Entry<Location, Integer> entry = headIterator.next();

            StatsEntry stats = get(objectives.get(entry.getKey()), entry.getValue());

            if (stats == null) {
                continue;
            }

            if (!(entry.getKey().getBlock().getState() instanceof Skull)) {
                headIterator.remove();
                continue;
            }

            Skull skull = (Skull) entry.getKey().getBlock().getState();

            skull.setOwner(UUIDUtils.name(stats.getOwner()));
            skull.update();
        }
    }

    private String beautify(Location location) {
        LeaderboardCommand.StatsObjective objective = objectives.get(location);

        switch (objective) {
            case DEATHS:
                return "Top Deaths";
            case HIGHEST_KILLSTREAK:
                return "Top KillStrk";
            case KD:
                return "Top KDR";
            case KILLS:
                return "Top Kills";
            default:
                return "Error";

        }
    }

    private String trim(String name) {
        return name.length() <= 15 ? name : name.substring(0, 15);
    }

    public StatsEntry get(LeaderboardCommand.StatsObjective objective, int place) {
        Map<StatsEntry, Number> base = Maps.newHashMap();

        for (StatsEntry entry : stats.values()) {
            base.put(entry, entry.get(objective));
        }

        TreeMap<StatsEntry, Number> ordered = new TreeMap<>((Comparator<StatsEntry>) (first, second) -> {
            if (first.get(objective).doubleValue() >= second.get(objective).doubleValue()) {
                return -1;
            }
            return 1;
        });

        ordered.putAll(base);

        Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

        int index = 0;
        for (Map.Entry<StatsEntry, Number> entry : ordered.entrySet()) {

            if (entry.getKey().getDeaths() < 10 && objective == LeaderboardCommand.StatsObjective.KD) {
                continue;
            }

            leaderboards.put(entry.getKey(), entry.getValue() + "");

            index++;

            if (index == place + 1) {
                break;
            }
        }

        try {
            return Iterables.get(leaderboards.keySet(), place - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearAll() {
        stats.clear();
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), this::save);
    }

    public void clear() {
        for (Map.Entry<UUID, StatsEntry> entry : stats.entrySet()) {
            Samurai.getInstance().getDeathbanMap().revive(entry.getKey());
        }
        stats.clear();
    }

    public void clearLeaderboards() {
        leaderboardHeads.clear();
        leaderboardSigns.clear();
        objectives.clear();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Samurai.getInstance(), this::save);
    }

    public Map<StatsEntry, String> getLeaderboards(LeaderboardCommand.StatsObjective objective, int range) {
        if (objective != LeaderboardCommand.StatsObjective.KD) {
            Map<StatsEntry, Number> base = Maps.newHashMap();

            for (StatsEntry entry : stats.values()) {
                base.put(entry, entry.get(objective));
            }

            TreeMap<StatsEntry, Number> ordered = new TreeMap<>((first, second) -> {
                if (first.get(objective).doubleValue() >= second.get(objective).doubleValue()) {
                    return -1;
                }

                return 1;
            });
            ordered.putAll(base);

            Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

            int index = 0;
            for (Map.Entry<StatsEntry, Number> entry : ordered.entrySet()) {
                leaderboards.put(entry.getKey(), entry.getValue() + "");

                index++;

                if (index == range) {
                    break;
                }
            }

            return leaderboards;
        } else {
            Map<StatsEntry, Double> base = Maps.newHashMap();

            for (StatsEntry entry : stats.values()) {
                base.put(entry, entry.getKD());
            }

            TreeMap<StatsEntry, Double> ordered = new TreeMap<>((Comparator<StatsEntry>) (first, second) -> {
                if (first.getKD() > second.getKD()) {
                    return -1;
                }

                return 1;
            });
            ordered.putAll(base);

            Map<StatsEntry, String> leaderboards = Maps.newHashMap();

            int index = 0;
            for (Map.Entry<StatsEntry, Double> entry : ordered.entrySet()) {
                if (entry.getKey().getDeaths() < 10) {
                    continue;
                }

                String kd = Team.DTR_FORMAT.format((double) entry.getKey().getKills() / (double) entry.getKey().getDeaths());

                leaderboards.put(entry.getKey(), kd);

                index++;

                if (index == range) {
                    break;
                }
            }

            return leaderboards;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (leaderboardHeads.containsKey(event.getBlock().getLocation())) {
            leaderboardHeads.remove(event.getBlock().getLocation());
            player.sendMessage(ChatColor.YELLOW + "Removed this skull from leaderboards.");

            Bukkit.getScheduler().scheduleAsyncDelayedTask(Samurai.getInstance(), this::save);
        }

        if (leaderboardSigns.containsKey(event.getBlock().getLocation())) {
            leaderboardSigns.remove(event.getBlock().getLocation());
            player.sendMessage(ChatColor.YELLOW + "Removed this sign from leaderboards.");

            Bukkit.getScheduler().scheduleAsyncDelayedTask(Samurai.getInstance(), this::save);
        }
    }

}
