package dev.lbuddyboy.samurai.team;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.CC;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class TeamHandler {

    @Getter private static HashSet<Team> powerFactions = new HashSet<>();

    private final Map<String, Team> teamNameMap = new ConcurrentHashMap<>(); // Team Name -> Team
    private final Map<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>(); // Team UUID -> Team
    private final Map<UUID, Team> playerTeamMap = new ConcurrentHashMap<>(); // Player UUID -> Team
    private boolean rostersLocked = false;
    private MongoCollection<Document> playerCollection, teamCollection;
    private DBCollection teamsCollection;

    public TeamHandler() {
        powerFactions = new HashSet<>();
        playerCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("Players");
        teamCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("Teams");
        teamsCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Teams");

        Samurai.getInstance().runRedisCommand(redis -> {
            for (String key : redis.keys("fox_teams.*")) {
                String loadString = redis.get(key);

                try {
                    Team team = new Team(key.split("\\.")[1]);
                    team.load(loadString);

                    setupTeam(team);
                } catch (Exception e) {
                    e.printStackTrace();
                    Samurai.getInstance().getLogger().severe("Could not load team from raw string: " + loadString);
                }
            }

            rostersLocked = Boolean.valueOf(redis.get("RostersLocked"));
            return (null);
        });

        Bukkit.getLogger().info("Creating indexes...");

        playerCollection.createIndex(new BasicDBObject("Team", 1));

        teamCollection.createIndex(new BasicDBObject("Owner", 1));
        teamCollection.createIndex(new BasicDBObject("CoLeaders", 1));
        teamCollection.createIndex(new BasicDBObject("Captains", 1));
        teamCollection.createIndex(new BasicDBObject("Members", 1));
        teamCollection.createIndex(new BasicDBObject("Name", 1));
        teamCollection.createIndex(new BasicDBObject("PowerFaction", 1));

        Bukkit.getLogger().info("Creating indexes done.");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), this::brewCheck, 20, 20);

    }

    public static void addPowerFaction(Team t) {
        powerFactions.add(t);
    }

    public static void removePowerFaction(Team t) {
        powerFactions.remove(t);
    }

    public static boolean isPowerFaction(Team t) {
        return powerFactions.contains(t);
    }

    public List<Team> getTeams() {
        return (new ArrayList<>(teamNameMap.values()));
    }

    public Team getTeam(String teamName) {
        return (teamNameMap.get(teamName.toLowerCase()));
    }

    public Team getTeam(ObjectId teamUUID) {
        return (teamUUID == null ? null : teamUniqueIdMap.get(teamUUID));
    }

    public Team getTeam(UUID playerUUID) {
        return (playerUUID == null ? null : playerTeamMap.get(playerUUID));
    }

    public Team getTeam(Player player) {
        return (getTeam(player.getUniqueId()));
    }

    public void setTeam(UUID playerUUID, Team team, boolean update) {
        if (team == null) {
            playerTeamMap.remove(playerUUID);
        } else {
            playerTeamMap.put(playerUUID, team);
        }

        if (update) {
            Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
                // update their team in mongo
                DBCollection playersCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Players");
                BasicDBObject player = new BasicDBObject("_id", playerUUID.toString().replace("-", ""));

                if (team != null) {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", team.getUniqueId().toHexString())));
                } else {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", null)));
                }
            });
        }
    }

    public void setTeam(UUID playerUUID, Team team) {
        setTeam(playerUUID, team, true); // standard cases we do update mongo
    }

    public void setupTeam(Team team) {
        setupTeam(team, false);
    }

    public void setupTeam(Team team, boolean update) {
        teamNameMap.put(team.getName().toLowerCase(), team);
        teamUniqueIdMap.put(team.getUniqueId(), team);

        for (UUID member : team.getMembers()) {
            setTeam(member, team, update); // no need to update mongo!
        }
    }

    public void removeTeam(Team team) {
        teamNameMap.remove(team.getName().toLowerCase());
        teamUniqueIdMap.remove(team.getUniqueId());

        for (UUID member : team.getMembers()) {
            setTeam(member, null);
        }
    }

    public void recachePlayerTeams() {
        playerTeamMap.clear();

        for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
            for (UUID member : team.getMembers()) {
                setTeam(member, team);
            }
        }
    }

    public void brewCheck() {
        if (Feature.TEAM_BREW.isDisabled()) return;
        for (Team team : this.teamNameMap.values()) {
            if (team.isBrewing()) {
                if (team.getBrewingMaterial(Material.GLASS_BOTTLE) < 3) continue;
                if (team.getBrewingMaterial(Material.NETHER_WART) < 1) continue;

                if (team.getStartedBrewingFres() > 0) {
                    if (team.checkMaterialAmount(team.getBrewingMaterial(Material.REDSTONE))) {
                        if (team.checkMaterialAmount(team.getBrewingMaterial(Material.MAGMA_CREAM))) {
                            if (team.getStartedBrewingFres() < System.currentTimeMillis()) {
                                team.setStartedBrewingFres(System.currentTimeMillis() + 10_000L);
                                team.setFresBrewed(team.getBrewedFresPots() + 3);
                                team.setBrewingMaterials(Material.NETHER_WART, team.getBrewingMaterial(Material.NETHER_WART) - 1);
                                team.setBrewingMaterials(Material.MAGMA_CREAM, team.getBrewingMaterial(Material.MAGMA_CREAM) - 1);
                                team.setBrewingMaterials(Material.REDSTONE, team.getBrewingMaterial(Material.REDSTONE) - 1);
                                team.setBrewingMaterials(Material.GLASS_BOTTLE, team.getBrewingMaterial(Material.GLASS_BOTTLE) - 3);

                                team.sendMessage(CC.translate("&g&l[TEAM BREW] &fYour team has just brewed &43x Fire Resistance Potions&f! &7Access using /team brew&f!"));
                            }
                        }
                    }
                }

                if (team.getStartedBrewingInvis() > 0) {
                    if (team.checkMaterialAmount(team.getBrewingMaterial(Material.GOLDEN_CARROT))) {
                        if (team.checkMaterialAmount(team.getBrewingMaterial(Material.FERMENTED_SPIDER_EYE))) {
                            if (team.checkMaterialAmount(team.getBrewingMaterial(Material.REDSTONE))) {
                                if (team.getStartedBrewingInvis() < System.currentTimeMillis()) {
                                    team.setStartedBrewingInvis(System.currentTimeMillis() + 10_000L);
                                    team.setInvisBrewed(team.getBrewedInvisPots() + 3);
                                    team.setBrewingMaterials(Material.NETHER_WART, team.getBrewingMaterial(Material.NETHER_WART) - 1);
                                    team.setBrewingMaterials(Material.GOLDEN_CARROT, team.getBrewingMaterial(Material.GOLDEN_CARROT) - 1);
                                    team.setBrewingMaterials(Material.FERMENTED_SPIDER_EYE, team.getBrewingMaterial(Material.FERMENTED_SPIDER_EYE) - 1);
                                    team.setBrewingMaterials(Material.REDSTONE, team.getBrewingMaterial(Material.REDSTONE) - 1);
                                    team.setBrewingMaterials(Material.GLASS_BOTTLE, team.getBrewingMaterial(Material.GLASS_BOTTLE) - 3);

                                    team.sendMessage(CC.translate("&g&l[TEAM BREW] &fYour team has just brewed &c3x Invisibility Potions&f! &7Access using /team brew&f!"));
                                }
                            }
                        }
                    }
                }

                if (team.getStartedBrewingSpeeds() > 0) {
                    if (team.checkMaterialAmount(team.getBrewingMaterial(Material.SUGAR))) {
                        if (team.checkMaterialAmount(team.getBrewingMaterial(Material.GLOWSTONE_DUST))) {
                            if (team.getStartedBrewingSpeeds() < System.currentTimeMillis()) {
                                team.setStartedBrewingSpeeds(System.currentTimeMillis() + 10_000L);
                                team.setSpeedBrewed(team.getBrewedSpeedPots() + 3);
                                team.setBrewingMaterials(Material.NETHER_WART, team.getBrewingMaterial(Material.NETHER_WART) - 1);
                                team.setBrewingMaterials(Material.SUGAR, team.getBrewingMaterial(Material.SUGAR) - 1);
                                team.setBrewingMaterials(Material.GLOWSTONE_DUST, team.getBrewingMaterial(Material.GLOWSTONE_DUST) - 1);
                                team.setBrewingMaterials(Material.GLASS_BOTTLE, team.getBrewingMaterial(Material.GLASS_BOTTLE) - 3);

                                team.sendMessage(CC.translate("&g&l[TEAM BREW] &fYour team has just brewed &b3x Speed Potions&f! &7Access using /team brew&f!"));
                            }
                        }
                    }
                }

                if (team.getStartedBrewingHealths() > 0) {
                    if (!team.checkMaterialAmount(team.getBrewingMaterial(Material.GLISTERING_MELON_SLICE))) continue;
                    if (!team.checkMaterialAmount(team.getBrewingMaterial(Material.GLOWSTONE_DUST))) continue;
                    if (!team.checkMaterialAmount(team.getBrewingMaterial(Material.GUNPOWDER))) continue;

                    if (team.getStartedBrewingHealths() < System.currentTimeMillis()) {
                        team.setStartedBrewingHealths(System.currentTimeMillis() + 10_000L);
                        team.setHealthPotsBrewed(team.getBrewedHealthPots() + 3);
                        team.setBrewingMaterials(Material.NETHER_WART, team.getBrewingMaterial(Material.NETHER_WART) - 1);
                        team.setBrewingMaterials(Material.GLISTERING_MELON_SLICE, team.getBrewingMaterial(Material.GLISTERING_MELON_SLICE) - 1);
                        team.setBrewingMaterials(Material.GUNPOWDER, team.getBrewingMaterial(Material.GUNPOWDER) - 1);
                        team.setBrewingMaterials(Material.GLOWSTONE_DUST, team.getBrewingMaterial(Material.GLOWSTONE_DUST) - 1);
                        team.setBrewingMaterials(Material.GLASS_BOTTLE, team.getBrewingMaterial(Material.GLASS_BOTTLE) - 3);

                        team.sendMessage(CC.translate("&g&l[TEAM BREW] &fYour team has just brewed &c3x Health Potions&f! &7Access using /team brew&f!"));
                    }

                }

            }
        }
    }

}