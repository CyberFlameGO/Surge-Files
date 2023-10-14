package dev.lbuddyboy.samurai.team;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.model.Filters;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.FlashPlayer;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.events.region.cavern.CavernHandler;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowHandler;
import dev.lbuddyboy.samurai.lunar.LunarListener;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.persist.maps.DeathbanMap;
import dev.lbuddyboy.samurai.team.allies.AllySetting;
import dev.lbuddyboy.samurai.team.boosters.TeamBoosterType;
import dev.lbuddyboy.samurai.team.brew.menu.BrewMenu;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.claims.Subclaim;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.team.dtr.DTRHandler;
import dev.lbuddyboy.samurai.team.event.TeamRaidableEvent;
import dev.lbuddyboy.samurai.team.logs.TeamLog;
import dev.lbuddyboy.samurai.team.roster.Roster;
import dev.lbuddyboy.samurai.team.track.TeamActionTracker;
import dev.lbuddyboy.samurai.team.track.TeamActionType;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

@Getter
public class Team {

    // Constants //

    @Getter
    public static List<Material> BREWING_MATERIALS = Arrays.asList(

    );

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final DecimalFormat DTR_FORMAT2 = new DecimalFormat("0.0");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 55);
    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
    public static final int MAX_CLAIMS = 2;
    public static final int MAX_FORCE_INVITES = 5;

    // Internal //

    private boolean needsSave = false;
    private boolean loading = false;

    // Persisted //

    @Setter
    private ObjectId uniqueId;
    private String name;
    private Location HQ;
    private double balance;
    private int DTR;
    private long DTRCooldown;
    private final List<Claim> claims = new ArrayList<>();
    private final List<Subclaim> subclaims = new ArrayList<>();
    private UUID owner = null;
    private Roster roster;
    private final Map<TeamBoosterType, Long> boosters = new HashMap<>();
    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> captains = new HashSet<>();
    private final Set<UUID> coleaders = new HashSet<>();
    private final Set<UUID> invitations = new HashSet<>();
    private final Set<ObjectId> allies = new HashSet<>();
    private final Set<ObjectId> requestedAllies = new HashSet<>();
    private String announcement;
    private int maxOnline = -1;
    private boolean powerFaction = false;
    private int kills = 0;
    private int timesWentRaidable = 0;
    private int raidableTeams = 0;
    private int addedPoints = 0;
    private int kothCaptures = 0;
    private int vaultCaptures = 0;
    private int diamondsMined = 0;
    private int deaths = 0;
    private int citadelsCapped = 0;
    private int conquestCapped = 0;
    private int reducedDTR = 0;
    private boolean claimLocked = false;
    private int forceInvites = MAX_FORCE_INVITES;
    private final Set<UUID> historicalMembers = new HashSet<>(); // this will store all players that were once members
    private final List<AllySetting> allySettings = new ArrayList<>();

    // Not persisted //

    @Setter
    private Location teamRally;
    @Setter
    private UUID focused;
    @Setter
    private Team focusedTeam;
    @Setter
    private long lastRequestReport;
    @Setter
    private Player rallyPlayer;
    @Setter
    private int bards;
    @Setter
    private int archers;
    @Setter
    private int rogues;

    @Setter
    private UUID bard, archer, rogue, mage;

    private boolean brewing;
    @Getter
    private int brewedInvisPots = 0, brewedFresPots, brewedSpeedPots = 0, brewedHealthPots = 0;
    @Setter
    private long startedBrewingInvis, startedBrewingFres, startedBrewingSpeeds, startedBrewingHealths;

    @Getter @Setter private LCWaypoint homeWaypoint;
    @Getter @Setter private LCWaypoint focusWaypoint;
    @Getter @Setter private LCWaypoint rallyWaypoint;

    @Getter
    private final Map<Material, Integer> brewingMats = new HashMap<>();

    public List<TeamBoosterType> getActiveBoosters() {
        List<TeamBoosterType> boosterTypes = new ArrayList<>();
        this.boosters.forEach((key, value) -> {
            if (value - System.currentTimeMillis() > 0) {
                boosterTypes.add(key);
            }
        });
        return boosterTypes;
    }

    public Team(String name) {
        this.name = name;
    }

    public void setDTR(int newDTR) {
        setDTR(newDTR, null);
    }

    public void setDTR(int newDTR, Player actor) {
        if (DTR == newDTR) {
            return;
        }

        if (DTR <= 0 && newDTR > 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of());
        }

        if (0 < DTR && newDTR <= 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, actor == null ? ImmutableMap.of() : ImmutableMap.of("actor", actor.getName()));
        }

        if (!isLoading()) {
            if (actor != null) {
                Samurai.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + ChatColor.STRIKETHROUGH + "--" + ">" + newDTR + ". Actor: " + actor.getName());
            } else {
                Samurai.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + ChatColor.STRIKETHROUGH + "--" + ">" + newDTR);
            }
        }

        this.DTR = newDTR;

        for (Player p : getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
            });
        }

        flagForSave();
    }

    public void setName(String name) {
        this.name = name;

        for (Player p : getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
            });
        }

        flagForSave();
    }

    public String getName(Player player) {
        if (name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
            return CC.translate("&x&f&2&f&b&0&eGlowstone Mountain"); // override team name
        } else if (name.equals(CavernHandler.getCavernTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.AQUA + "Cavern";
        } else if (name.equals("LootHill") && this.getMembers().size() == 0) {
            return CC.translate("&x&f&b&9&e&0&9Loot Hill");
        } else if (name.equals("DeepDark") && this.getMembers().size() == 0) {
            return CC.translate("&3Deep Dark");
        } else if (owner == null) {
            if (hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                return switch (player.getWorld().getEnvironment()) {
                    case NETHER -> (ChatColor.GREEN + "Nether Spawn");
                    case THE_END -> (ChatColor.GREEN + "The End Safezone");
                    default -> (ChatColor.GREEN + "Spawn");
                };

            } else if (hasDTRBitmask(DTRBitmask.KOTH)) {
                if (name.equals(VaultHandler.TEAM_NAME)) {
                    return CC.GOLD + CC.BOLD + "Vault Post";
                }
                return CC.translate("&e" + name + " KoTH");
            } else if (hasDTRBitmask(DTRBitmask.KOTH)) {
                return CC.translate("&e" + name + " KoTH");
            } else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (ChatColor.DARK_PURPLE + "Citadel");
            } else if (hasDTRBitmask(DTRBitmask.ROAD)) {
                return (ChatColor.GOLD + getName().replace("Road", " Road"));
            } else if (hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (ChatColor.YELLOW + "Conquest");
            }
        }

        if (isMember(player.getUniqueId())) {
            return (ChatColor.GREEN + getName());
        } else if (isAlly(player.getUniqueId())) {
            return (Team.ALLY_COLOR + getName());
        } else {
            return (ChatColor.RED + getName());
        }
    }

    public void addMember(UUID member) {
        if (members.add(member)) {
            historicalMembers.add(member);

            if (this.loading) return;
            TeamActionTracker.logActionAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of(
                    "playerId", member
            ));

            flagForSave();
        }

        Player p = Bukkit.getPlayer(member);
        if (p != null) {
            LunarListener.updateWaypoints(p);
        }

    }

    public void addCaptain(UUID captain) {
        if (captains.add(captain) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void enableAllySetting(UUID sender, AllySetting settings) {
        createLog(sender, "ALLY SETTING ENABLED", "Allies can now: " + settings.getDisplay());
        this.allySettings.add(settings);

        flagForSave();
    }

    public void disableAllySetting(UUID sender, AllySetting settings) {
        createLog(sender, "ALLY SETTING DISABLED", "Allies cannot: " + settings.getDisplay());
        this.allySettings.remove(settings);

        flagForSave();
    }

    public void rewardBooster(TeamBoosterType booster, long time) {
        this.boosters.put(booster, time);
        sendMessage(CC.translate("&3&l[TEAM BOOSTER] &fA " + booster.getDisplayName() + " &fteam booster has just been rewarded to your team for &3" + TimeUtils.formatIntoDetailedString((int) ((time - System.currentTimeMillis()) / 1000))));
        flagForSave();
    }

    public void addCoLeader(UUID co) {
        if (coleaders.add(co) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setBalance(double balance) {
        this.balance = balance;
        flagForSave();
    }

    public void setDTRCooldown(long dtrCooldown) {

        if (getActiveBoosters().contains(TeamBoosterType.TWENTY_MIN_DTR_REGEN)) {
            this.DTRCooldown = System.currentTimeMillis() + (60_000 * 20);
        } else {
            this.DTRCooldown = dtrCooldown;
        }

        for (Player p : getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
            });
        }

        flagForSave();
    }

    public void removeCaptain(UUID captain) {
        if (captains.remove(captain)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void removeCoLeader(UUID co) {
        if (coleaders.remove(co)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setOwner(UUID owner) {
        this.owner = owner;

        if (owner != null) {
            members.add(owner);
            coleaders.remove(owner);
            captains.remove(owner);
        }

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of(
                "playerId", owner
        ));

        flagForSave();
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;

        flagForSave();
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of(
                "newAnnouncement", announcement
        ));

        flagForSave();
    }

    public boolean isBrewing() {
        return this.startedBrewingFres > 0 || this.startedBrewingHealths > 0 || this.startedBrewingInvis > 0 || this.startedBrewingSpeeds > 0;
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of(
                "oldHq", oldHQ,
                "newHq", newHQ
        ));

        flagForSave();
    }

    public void setPowerFaction(boolean bool) {
        this.powerFaction = bool;
        if (bool) {
            TeamHandler.addPowerFaction(this);
        } else {
            TeamHandler.removePowerFaction(this);
        }

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.POWER_FAC_STATUS_CHANGED, ImmutableMap.of(
                "powerFaction", bool
        ));

        flagForSave();
    }

    public void disband() {
        try {
            if (owner != null) {
                double refund = balance;

                for (Claim claim : claims) {
                    refund += Claim.getPrice(claim, this, false);
                }

                FrozenEconomyHandler.deposit(owner, refund);
                Samurai.getInstance().getWrappedBalanceMap().setBalance(owner, FrozenEconomyHandler.getBalance(owner));
                Samurai.getInstance().getLogger().info("Economy Logger: Depositing " + refund + " into " + UUIDUtils.name(owner) + "'s account: Disbanded team");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ObjectId allyId : getAllies()) {
            Team ally = Samurai.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                ally.getAllies().remove(getUniqueId());
            }
        }

        for (Player member : getOnlineMembers()) {
            FrozenNametagHandler.reloadPlayer(member);
        }
        for (UUID uuid : members) {
            Samurai.getInstance().getChatModeMap().setChatMode(uuid, ChatMode.PUBLIC);
        }

        Samurai.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().runRedisCommand((redis) -> {
                    redis.del("fox_teams." + name.toLowerCase());
                    return null;
                });

                DBCollection teamsCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Teams");
                teamsCollection.remove(getJSONIdentifier());
            }

        }.runTaskAsynchronously(Samurai.getInstance());

        needsSave = false;
    }

    public void rename(String newName) {
        final String oldName = name;

        Samurai.getInstance().getTeamHandler().removeTeam(this);

        this.name = newName;

        for (Player p : getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
            });
        }

        Samurai.getInstance().getTeamHandler().setupTeam(this);

        Samurai.getInstance().runRedisCommand((redis) -> {
            redis.del("fox_teams." + oldName.toLowerCase());
            return (null);
        });

        // We don't need to do anything here as all we're doing is changing the name, not the Unique ID (which is what Mongo uses)
        // therefore, Mongo will be notified of this once the 'flagForSave()' down below gets processed.

        for (Claim claim : getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        flagForSave();
    }

    public void setForceInvites(int forceInvites) {
        this.forceInvites = forceInvites;
        flagForSave();
    }

    public void setKills(int kills) {
        this.kills = kills;
        if (getActiveBoosters().contains(TeamBoosterType.X2_KILL_POINT)) {
            this.kills++;
        }
        flagForSave();
    }

    public void setAddedPoints(int addedPoints) {
        this.addedPoints = addedPoints;
        flagForSave();
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        flagForSave();
    }

    public void setVaultCaptures(int vaultCaptures) {
        this.vaultCaptures = vaultCaptures;
        flagForSave();
    }

    public void setRaidableTeams(int raidableTeams) {
        this.raidableTeams = raidableTeams;
        flagForSave();
    }

    public void setTimesWentRaidable(int times) {
        this.timesWentRaidable = times;
        flagForSave();
    }

    public void setKothCaptures(int kothCaptures) {
        this.kothCaptures = kothCaptures;
        flagForSave();
    }

    public void setDiamondsMined(int diamondsMined) {
        this.diamondsMined = diamondsMined;
        flagForSave();
    }

    public void setConquestsCapped(int conquests) {
        this.conquestCapped = conquests;
        flagForSave();
    }

    public void setCitadelsCapped(int citadels) {
        this.citadelsCapped = citadels;
        flagForSave();
    }

    public void setClaimLocked(boolean claimLocked) {
        this.claimLocked = claimLocked;
        flagForSave();
    }

    public void flagForSave() {
        needsSave = true;
    }

    public boolean isOwner(UUID check) {
        return (check.equals(owner));
    }

    public boolean isMember(UUID check) {
        return members.contains(check);
    }

    public boolean isCaptain(UUID check) {
        return captains.contains(check);
    }

    public boolean isCoLeader(UUID check) {
        return coleaders.contains(check);
    }

    public boolean isAlly(UUID check) {
        Team checkTeam = Samurai.getInstance().getTeamHandler().getTeam(check);
        return (checkTeam != null && isAlly(checkTeam));
    }

    public boolean isAlly(Team team) {
        return (getAllies().contains(team.getUniqueId()));
    }

    public boolean ownsLocation(Location location) {
        return (LandBoard.getInstance().getTeam(location) == this);
    }

    public boolean ownsClaim(Claim claim) {
        return (claims.contains(claim));
    }

    public boolean removeMember(UUID member) {
        members.remove(member);
        captains.remove(member);
        coleaders.remove(member);

        // If the owner leaves (somehow)
        if (isOwner(member)) {
            Iterator<UUID> membersIterator = members.iterator();
            this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
        }

        try {
            for (Subclaim subclaim : subclaims) {
                if (subclaim.isMember(member)) {
                    subclaim.removeMember(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DTR > getMaxDTR()) {
            DTR = getMaxDTR();
        }

        if (this.loading) return false;
        TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of(
                "playerId", member
        ));

        for (Player p : getOnlineMembers()) {
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                FrozenNametagHandler.reloadPlayer(p);
                FrozenNametagHandler.reloadOthersFor(p);
                LunarListener.updateWaypoints(p);
            });
        }

        flagForSave();
        return (owner == null || members.size() == 0);
    }

    public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = DTR;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
    }

    public int getOnlineMemberAmount() {
        return getOnlineFlashMembers().size();
    }

    public void setFresBrewed(int value) {
        this.brewedFresPots = value;
        flagForSave();
    }

    public void setSpeedBrewed(int value) {
        this.brewedSpeedPots = value;
        flagForSave();
    }

    public void setHealthPotsBrewed(int value) {
        this.brewedHealthPots = value;
        flagForSave();
    }

    public void setInvisBrewed(int value) {
        this.brewedInvisPots = value;
        flagForSave();
    }

    public int getBrewingMaterial(Material material) {
        Map<Material, Integer> materials = this.brewingMats;

        if (materials.get(material) == null || !materials.containsKey(material))
            return 0;
        return materials.get(material);
    }

    public Roster getRoster() {
        if (this.roster != null) return this.roster;

        Document document = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("Rosters").find(Filters.eq("teamUUID", getUniqueId())).first();
        return (this.roster = Roster.fromDocument(getUniqueId(), document));

    }

    public void setBrewingMaterials(Material material, int amount) {
        brewingMats.put(material, amount);
        if (!this.isLoading()) {
            flagForSave();
        }
    }

    public boolean checkMaterialAmount(int amount) {
        return amount >= 1;
    }

    public Collection<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Samurai.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !ModUtils.isInvisible(exactPlayer)) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public Collection<FlashPlayer> getOnlineFlashMembers() {
        List<FlashPlayer> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            String name = FrozenUUIDCache.name(member);
            FlashPlayer exactPlayer = new FlashPlayer(member, name);

            if (Flash.getInstance().getSpoofHandler().getSpoofPlayers().containsKey(name)) {
                players.add(exactPlayer);
            } else {
                if (exactPlayer.getPlayer() != null && !ModUtils.isInvisible(exactPlayer.getPlayer())) {
                    players.add(exactPlayer);
                }
            }
        }

        return (players);
    }

    public Collection<UUID> getOfflineMembers() {
        List<UUID> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Samurai.getInstance().getServer().getPlayer(member);


            if (exactPlayer == null || ModUtils.isInvisible(exactPlayer)) {
                players.add(member);
            }
        }

        return (players);
    }

    public Collection<FlashPlayer> getOfflineFlashMembers() {
        List<FlashPlayer> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            FlashPlayer exactPlayer = new FlashPlayer(member, FrozenUUIDCache.name(member));

            if (exactPlayer.getPlayer() == null) {
                if (exactPlayer.getSpoofPlayer() != null) continue;

                players.add(exactPlayer);
                continue;
            }

            if (exactPlayer.getPlayer() != null && !ModUtils.isInvisible(exactPlayer.getPlayer())) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public Subclaim getSubclaim(String name) {
        for (Subclaim subclaim : subclaims) {
            if (subclaim.getName().equalsIgnoreCase(name)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public Subclaim getSubclaim(Location location) {
        for (Subclaim subclaim : subclaims) {
            if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public int getSize() {
        return (getMembers().size());
    }

    public boolean isRaidable() {
        return (DTR <= 0);
    }

    public void playerDeath(String playerName, double dtrBefore, int dtrLoss, Player killer) {
        if (Samurai.getInstance().getMapHandler().getDuelHandler().isInDuel(Bukkit.getPlayer(playerName))) {
            return;
        }

        if (Samurai.getInstance().inEvent(Bukkit.getPlayer(playerName))) {
            return;
        }

        if (!Samurai.getInstance().getMapHandler().isKitMap()) {
            if (Samurai.getInstance().getArenaHandler().getArenaTeam() == null
                    && Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().isEmpty()
                    && Samurai.getInstance().getArenaHandler().getArenaTeam().getClaims().get(0).contains(killer)) {
                DiscordLogger.logRetard("Deathban arena isn't setup (SET IT UP!!!!) - Code Error #1 - " + playerName);
                return;
            }
        }

        int newDTR = Math.max(DTR - dtrLoss, -1);

        if (Samurai.getInstance().getServerHandler().isReducedDTR()) {
            if (reducedDTR < 1) {
                newDTR = DTR;
                reducedDTR++;
            } else {
                reducedDTR = 0;
            }
        }

        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(CC.translate("&6&lTEAM DEATH &7" + CC.UNICODE_ARROWS_RIGHT + " &c" + playerName + " has died!"));
                player.sendMessage(CC.translate(CC.GOLD + CC.UNICODE_ARROWS_RIGHT + " &fDTR&7: &6" + DTR_FORMAT.format(newDTR)));
            }
        }

        if (killer == null) {
            createLog(UUIDUtils.uuid(playerName), "DEATH &7(" + dtrBefore + " -> " + newDTR+ ")", playerName + " died.");
        } else {
            createLog(killer.getUniqueId(), "DEATH &7(" + dtrBefore + " -> " + newDTR+ ")", killer.getName() + " killed " + playerName);
        }

        Samurai.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + playerName + "]");
        setDTR(newDTR);

        if (isRaidable() && dtrBefore > 0) {
            if (killer != null && Samurai.getInstance().getBattlePassHandler() != null) {
                Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);
                if (killerTeam == null) {
                    if (Samurai.getInstance().getBattlePassHandler() != null) {
                        Samurai.getInstance().getBattlePassHandler().useProgress(killer.getUniqueId(), progress -> {
                            progress.setMadeFactionRaidable(true);
                            progress.requiresSave();

                            Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(killer);
                        });
                    }
                } else {
                    killerTeam.setRaidableTeams(killerTeam.getRaidableTeams() + 1);
                    if (Samurai.getInstance().getBattlePassHandler() != null) {
                        for (Player teamPlayer : killerTeam.getOnlineMembers()) {
                            Samurai.getInstance().getBattlePassHandler().useProgress(teamPlayer.getUniqueId(), progress -> {
                                progress.setMadeFactionRaidable(true);
                                progress.requiresSave();

                                Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(teamPlayer);
                            });
                        }
                    }
                }

            }

            final TeamRaidableEvent teamRaidableEvent = new TeamRaidableEvent(Samurai.getInstance().getServer().getPlayer(playerName), killer, this, DTR, newDTR, UUIDUtils.uuid(playerName));
            Samurai.getInstance().getServer().getPluginManager().callEvent(teamRaidableEvent);

            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
            DTRCooldown = System.currentTimeMillis() + Samurai.getInstance().getMapHandler().getRegenTimeRaidable();
        } else {
            DTRCooldown = System.currentTimeMillis() + Samurai.getInstance().getMapHandler().getRegenTimeDeath();
        }

        DTRHandler.markOnDTRCooldown(this);
    }

    public int getDTRIncrement() {
        return 1;
    }

    public int getMaxDTR() {
        return (DTRHandler.getMaxDTR(getSize()));
    }

    public void load(BasicDBObject obj) {
        loading = true;

        setUniqueId(new ObjectId(obj.getString("_id")));
        setOwner(obj.getString("Owner") == null ? null : UUID.fromString(obj.getString("Owner")));

        if (obj.containsField("CoLeaders"))
            for (Object coLeader : (BasicDBList) obj.get("CoLeaders")) addCoLeader(UUID.fromString((String) coLeader));
        if (obj.containsField("Captains"))
            for (Object captain : (BasicDBList) obj.get("Captains")) addCaptain(UUID.fromString((String) captain));
        if (obj.containsField("Members"))
            for (Object member : (BasicDBList) obj.get("Members")) addMember(UUID.fromString((String) member));
        if (obj.containsField("Invitations")) for (Object invite : (BasicDBList) obj.get("Invitations"))
            getInvitations().add(UUID.fromString((String) invite));
        if (obj.containsField("DTR")) setDTR(obj.getInt("DTR"));
        if (obj.containsField("DTRCooldown")) setDTRCooldown(obj.getDate("DTRCooldown").getTime());
        if (obj.containsField("Balance")) setBalance(obj.getDouble("Balance"));
        if (obj.containsField("MaxOnline")) setMaxOnline(obj.getInt("MaxOnline"));
        if (obj.containsField("HQ"))
            setHQ(LocationSerializer.deserialize((BasicDBObject) obj.get("HQ")));
        if (obj.containsField("Announcement")) setAnnouncement(obj.getString("Announcement"));
        if (obj.containsField("PowerFaction")) setPowerFaction(obj.getBoolean("PowerFaction"));
        if (obj.containsField("Claims"))
            for (Object claim : (BasicDBList) obj.get("Claims")) getClaims().add(Claim.fromJson((BasicDBObject) claim));
        if (obj.containsField("Subclaims")) for (Object subclaim : (BasicDBList) obj.get("Subclaims"))
            getSubclaims().add(Subclaim.fromJson((BasicDBObject) subclaim));
        if (obj.containsField("HealthPotsBrewed")) setHealthPotsBrewed(obj.getInt("HealthPotsBrewed"));
        if (obj.containsField("InvisBrewed")) setInvisBrewed(obj.getInt("InvisBrewed"));
        if (obj.containsField("SpeedsBrewed")) setSpeedBrewed(obj.getInt("SpeedsBrewed"));
        if (obj.containsField("FresBrewed")) setFresBrewed(obj.getInt("FresBrewed"));

        for (TeamBoosterType booster : TeamBoosterType.values()) {
            if (obj.containsField(booster.name())) {
                getBoosters().put(booster, obj.getLong(booster.name()));
            }
        }

        for (AllySetting setting : AllySetting.values()) {
            if (obj.containsField(setting.name())) {
                this.allySettings.add(setting);
            }
        }

        for (Material material : BrewMenu.getBrewingMaterials()) {
            if (obj.containsField("MAT_" + material.name())) {
                brewingMats.put(material, obj.getInt("MAT_" + material.name()));
            }
        }


        loading = false;
    }

    public List<Team> getAlliedTeams() {
        return this.allies.stream().map(objectId -> Samurai.getInstance().getTeamHandler().getTeam(objectId)).toList();
    }

    public void load(String str) {
        load(str, false);
    }

    public void load(String str, boolean forceSave) {
        loading = true;
        String[] lines = str.split("\n");

        for (String line : lines) {
            if (line.indexOf(':') == -1) {
                System.out.println("Found an invalid line... `" + line + "`");
                continue;
            }

            String identifier = line.substring(0, line.indexOf(':'));
            String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

            if (identifier.equalsIgnoreCase("Owner")) {
                if (!lineParts[0].equals("null")) {
                    setOwner(UUID.fromString(lineParts[0].trim()));
                }
            } else if (identifier.equalsIgnoreCase("UUID")) {
                uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addMember(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("CoLeaders")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCoLeader(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Captains")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCaptain(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Invited")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getInvitations().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getHistoricalMembers().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MaxOnline")) {
                setMaxOnline(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ForceInvites")) {
                setForceInvites(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                setDTRCooldown(Long.parseLong(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
                for (String claim : lineParts) {
                    claim = claim.replace("[", "").replace("]", "");

                    if (claim.contains(":")) {
                        String[] split = claim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String world = split[7].trim();

                        Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                        claimObj.setName(name);

                        getClaims().add(claimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Allies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Samurai.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String ally : lineParts) {
                    ally = ally.replace("[", "").replace("]", "");

                    if (ally.length() != 0) {
                        allies.add(new ObjectId(ally.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Samurai.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String requestedAlly : lineParts) {
                    requestedAlly = requestedAlly.replace("[", "").replace("]", "");

                    if (requestedAlly.length() != 0) {
                        requestedAllies.add(new ObjectId(requestedAlly.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Subclaims")) {
                for (String subclaim : lineParts) {
                    subclaim = subclaim.replace("[", "").replace("]", "");

                    if (subclaim.contains(":")) {
                        String[] split = subclaim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String membersRaw = "";

                        if (split.length >= 8) {
                            membersRaw = split[7].trim();
                        }

                        Location location1 = new Location(Bukkit.getWorlds().get(0), x1, y1, z1);
                        Location location2 = new Location(Bukkit.getWorlds().get(0), x2, y2, z2);
                        List<UUID> members = new ArrayList<>();

                        for (String uuidString : membersRaw.split(", ")) {
                            if (uuidString.isEmpty()) {
                                continue;
                            }

                            members.add(UUID.fromString(uuidString.trim()));
                        }

                        Subclaim subclaimObj = new Subclaim(location1, location2, name);
                        subclaimObj.setMembers(members);

                        getSubclaims().add(subclaimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Announcement")) {
                setAnnouncement(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("PowerFaction")) {
                setPowerFaction(Boolean.parseBoolean(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Kills")) {
                setKills(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("AddedPoints")) {
                setAddedPoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Deaths")) {
                setDeaths(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("KothCaptures")) {
                setKothCaptures(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("VaultCaptures")) {
                setVaultCaptures(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                setDiamondsMined(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("CitadelsCaptured")) {
                setCitadelsCapped(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ConquestsCaptured")) {
                this.conquestCapped = (Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ClaimLocked")) {
                setClaimLocked(Boolean.parseBoolean(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("TimesWentRaidable")) {
                setTimesWentRaidable(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("HealthPotsBrewed")) {
                setHealthPotsBrewed(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("InvisBrewed")) {
                setInvisBrewed(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("SpeedsBrewed")) {
                setSpeedBrewed(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FresBrewed")) {
                setFresBrewed(Integer.parseInt(lineParts[0]));
            }
            for (TeamBoosterType booster : TeamBoosterType.values()) {
                if (identifier.equals(booster.name())) {
                    boosters.put(booster, Long.parseLong(lineParts[0]));
                }
            }
            for (AllySetting setting : AllySetting.values()) {
                if (identifier.equals(setting.name())) {
                    allySettings.add(setting);
                }
            }
            for (Material material : BrewMenu.getBrewingMaterials()) {
                if (identifier.startsWith("MAT_") && identifier.endsWith(material.name())) {
                    brewingMats.put(material, Integer.parseInt(lineParts[0]));
                }
            }
        }

        for (UUID member : members) {
            FrozenUUIDCache.ensure(member);
        }

        if (uniqueId == null) {
            uniqueId = new ObjectId();
            Samurai.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
        }


        loading = false;
        needsSave = forceSave;
    }

    public String saveString(boolean toJedis) {
        if (toJedis) {
            needsSave = false;
        }

        if (loading) {
            return (null);
        }

        StringBuilder teamString = new StringBuilder();

        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        StringBuilder coleaders = new StringBuilder();
        StringBuilder invites = new StringBuilder();
        StringBuilder historicalMembers = new StringBuilder();
        StringBuilder allySettings = new StringBuilder();

        for (UUID member : getMembers()) {
            members.append(member.toString()).append(", ");
        }

        for (UUID captain : getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID co : getColeaders()) {
            coleaders.append(co.toString()).append(", ");
        }

        for (UUID invite : getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

        for (UUID member : getHistoricalMembers()) {
            historicalMembers.append(member.toString()).append(", ");
        }

        for (AllySetting setting : getAllySettings()) {
            allySettings.append(setting.name()).append(", ");
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        if (historicalMembers.length() > 2) {
            historicalMembers.setLength(historicalMembers.length() - 2);
        }

        if (allySettings.length() > 2) {
            allySettings.setLength(allySettings.length() - 2);
        }

        teamString.append("UUID:").append(getUniqueId().toString()).append("\n");
        teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("CoLeaders:").append(coleaders).append('\n');
        teamString.append("Captains:").append(captains).append('\n');
        teamString.append("Members:").append(members).append('\n');
        teamString.append("AllySettings:").append(allySettings).append('\n');
        teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString().replace("\n", "")).append('\n');
        teamString.append("Claims:").append(getClaims().toString().replace("\n", "")).append('\n');
        teamString.append("Allies:").append(getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
        teamString.append("HistoricalMembers:").append(historicalMembers).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("MaxOnline:").append(getMaxOnline()).append('\n');
        teamString.append("ForceInvites:").append(getForceInvites()).append('\n');
        teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getName().replace("\n", "")).append('\n');
        teamString.append("Announcement:").append(String.valueOf(getAnnouncement()).replace("\n", "")).append("\n");
        teamString.append("PowerFaction:").append(isPowerFaction()).append("\n");
        teamString.append("Kills:").append(getKills()).append("\n");
        teamString.append("AddedPoints:").append(getAddedPoints()).append("\n");
        teamString.append("Deaths:").append(getDeaths()).append("\n");
        teamString.append("TimesWentRaidable:").append(getTimesWentRaidable()).append("\n");
        teamString.append("DiamondsMined:").append(getDiamondsMined()).append("\n");
        teamString.append("KothCaptures:").append(getKothCaptures()).append("\n");
        teamString.append("VaultCaptures:").append(getVaultCaptures()).append("\n");
        teamString.append("CitadelsCaptured:").append(getCitadelsCapped()).append("\n");
        teamString.append("ConquestsCaptured:").append(getConquestCapped()).append("\n");
        teamString.append("HealthPotsBrewed:").append(getBrewedHealthPots()).append("\n");
        teamString.append("SpeedsBrewed:").append(getBrewedSpeedPots()).append("\n");
        teamString.append("InvisBrewed:").append(getBrewedInvisPots()).append("\n");
        teamString.append("FresBrewed:").append(getBrewedFresPots()).append("\n");
        teamString.append("ClaimLocked:").append(isClaimLocked()).append("\n");
        for (Map.Entry<TeamBoosterType, Long> entry : boosters.entrySet()) {
            teamString.append(entry.getKey().name()).append(":").append(entry.getValue()).append("\n");
        }
        for (Map.Entry<Material, Integer> entry : brewingMats.entrySet()) {
            teamString.append("MAT_" + entry.getKey().name()).append(":").append(entry.getValue()).append("\n");
        }

        if (getHQ() != null) {
            teamString.append("HQ:").append(getHQ().getWorld().getName()).append(",").append(getHQ().getX()).append(",").append(getHQ().getY()).append(",").append(getHQ().getZ()).append(",").append(getHQ().getYaw()).append(",").append(getHQ().getPitch()).append('\n');
        }

        return (teamString.toString());
    }

    public BasicDBObject toJSON() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("Owner", getOwner() == null ? null : getOwner().toString());
        dbObject.put("CoLeaders", uuidsToStrings(getColeaders()));
        dbObject.put("Captains", uuidsToStrings(getCaptains()));
        dbObject.put("Members", uuidsToStrings(getMembers()));
        dbObject.put("Invitations", uuidsToStrings(getInvitations()));
        dbObject.put("Allies", getAllies());
        dbObject.put("RequestedAllies", getRequestedAllies());
        dbObject.put("DTR", getDTR());
        dbObject.put("DTRCooldown", new Date(getDTRCooldown()));
        dbObject.put("Balance", getBalance());
        dbObject.put("MaxOnline", getMaxOnline());
        dbObject.put("Name", getName());
        dbObject.put("HQ", LocationSerializer.serialize(getHQ()));
        dbObject.put("Announcement", getAnnouncement());
        dbObject.put("PowerFaction", isPowerFaction());

        BasicDBList claims = new BasicDBList();
        BasicDBList subclaims = new BasicDBList();

        for (Claim claim : getClaims()) {
            claims.add(claim.json());
        }

        for (Subclaim subclaim : getSubclaims()) {
            subclaims.add(subclaim.json());
        }

        dbObject.put("Claims", claims);
        dbObject.put("Subclaims", subclaims);
        dbObject.put("Kills", this.kills);
        dbObject.put("Deaths", this.deaths);
        dbObject.put("TimesWentRaidable", this.timesWentRaidable);
        dbObject.put("DiamondsMined", this.diamondsMined);
        dbObject.put("CitadelsCaptured", this.citadelsCapped);
        dbObject.put("ConquestsCaptured", this.conquestCapped);
        dbObject.put("KothCaptures", this.kothCaptures);
        dbObject.put("VaultCaptures", this.vaultCaptures);
        dbObject.put("ClaimLocked", this.claimLocked);
        dbObject.put("HealthPotsBrewed", this.brewedHealthPots);
        dbObject.put("SpeedsBrewed", this.brewedSpeedPots);
        dbObject.put("InvisBrewed", this.brewedInvisPots);
        dbObject.put("FresBrewed", this.brewedFresPots);
        for (Map.Entry<TeamBoosterType, Long> entry : boosters.entrySet()) {
            dbObject.put(entry.getKey().name(), entry.getValue());
        }
        for (Map.Entry<Material, Integer> entry : brewingMats.entrySet()) {
            dbObject.put("MAT_" + entry.getKey().name(), entry.getValue());
        }


        return (dbObject);
    }

    public BasicDBObject getJSONIdentifier() {
        return (new BasicDBObject("_id", getUniqueId().toHexString()));
    }

    private Location parseLocation(String[] args) {
        if (args.length != 6) {
            return (null);
        }

        World world = Samurai.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return (new Location(world, x, y, z, yaw, pitch));
    }

    public void sendMessage(String message) {
        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(message);
            }
        }
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null) {
            player.sendMessage(GRAY_LINE);
            player.sendMessage(getName(player));

            if (getMembers().isEmpty() && getName().endsWith("Citadel")) {
                if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    player.sendMessage(ChatColor.WHITE + "Location: " + ChatColor.GOLD + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
                } else {
                    KOTH citadel = ((KOTH) Samurai.getInstance().getEventHandler().getEvent("Nether-Citadel"));
                    player.sendMessage(ChatColor.WHITE + "Location: " + ChatColor.GOLD + (citadel == null || citadel.getCapLocation() == null ? "None" : citadel.getCapLocation().getBlockX() + ", " + citadel.getCapLocation().getBlockZ()));
                }

                Set<ObjectId> cappers = Samurai.getInstance().getCitadelHandler().getCappers();
                Set<String> capperNames = new HashSet<>();

                for (ObjectId capper : cappers) {
                    Team capperTeam = Samurai.getInstance().getTeamHandler().getTeam(capper);

                    if (capperTeam != null) {
                        capperNames.add(capperTeam.getName());
                    }
                }

                if (!cappers.isEmpty()) {
                    player.sendMessage(ChatColor.WHITE + "Currently captured by: " + ChatColor.RED + Joiner.on(", ").join(capperNames));
                }
            } else {
                if (HQ != null && HQ.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    String world = HQ.getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "End"; // if it's not the nether, it's the end
                    player.sendMessage(ChatColor.WHITE + "Location: " + ChatColor.GOLD + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ() + " " + CC.GRAY + " (" + world + ")"));
                } else {
                    if (getName().toLowerCase().equalsIgnoreCase("endportal")) {
                        player.sendMessage(ChatColor.WHITE + "Public End Portal Locations:");
                        player.sendMessage(ChatColor.GOLD + CC.UNICODE_ARROWS_RIGHT + CC.WHITE + " 1000, 1000");
                        player.sendMessage(ChatColor.GOLD + CC.UNICODE_ARROWS_RIGHT + CC.WHITE + " -1000, 1000");
                        player.sendMessage(ChatColor.GOLD + CC.UNICODE_ARROWS_RIGHT + CC.WHITE + " 1000, -1000");
                        player.sendMessage(ChatColor.GOLD + CC.UNICODE_ARROWS_RIGHT + CC.WHITE + " -1000, -1000");
                    } else {
                        player.sendMessage(ChatColor.WHITE + "Location: " + ChatColor.GOLD + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
                    }
                }
            }

            if (getName().toLowerCase().contains("glowstone")) {
                long resetTime = Samurai.getInstance().getGlowHandler().getResetTime();
                String string = TimeUtils.formatLongIntoDetailedString(resetTime);
                player.sendMessage(CC.WHITE + "Resetting in: " + CC.RED + string);
            }

            if (getName().toLowerCase().contains("loothill")) {
                long resetTime = Samurai.getInstance().getLootHillHandler().getResetTime();
                String string = TimeUtils.formatLongIntoDetailedString(resetTime);
                player.sendMessage(CC.GRAY + "Resetting in: " + CC.RED + string);
                player.sendMessage(CC.YELLOW + "Loot Blocks Left: " + CC.GREEN + Samurai.getInstance().getLootHillHandler().getLootHill().getRemaining());
                player.sendMessage(" ");
                player.sendMessage(CC.translate("&fBreak any &bdiamond block&f to get"));
                player.sendMessage(CC.translate("&f1 random ability item set."));
            }

            if (getName().toLowerCase().contains("deepdark")) {
                player.sendMessage(" ");
                player.sendMessage(CC.translate("&fRight click the lecturn with a deep dark"));
                player.sendMessage(CC.translate("&fboss summoner to start your battle."));
            }

            if (getName().toLowerCase().contains("cavern")) {
                long resetTime = Samurai.getInstance().getCavernHandler().getResetTime();
                String string = TimeUtils.formatLongIntoDetailedString(resetTime);
                player.sendMessage(CC.WHITE + "Resetting in: " + CC.RED + string);
            }

            player.sendMessage(GRAY_LINE);
            return;
        }

        DeathbanMap deathbanMap = Samurai.getInstance().getDeathbanMap();
        FlashPlayer owner = new FlashPlayer(this.owner, UUIDUtils.name(this.owner));
        StringBuilder allies = new StringBuilder();

        FancyMessage coleadersJson = new FancyMessage("Co-Leaders: ").color(ChatColor.WHITE);

        FancyMessage captainsJson = new FancyMessage("Captains: ").color(ChatColor.WHITE);

        if (player.hasPermission("foxtrot.manage")) {
            captainsJson.command("/manageteam demote " + getName()).tooltip("§bClick to demote captains");
        }

        FancyMessage membersJson = new FancyMessage("Members: ").color(ChatColor.WHITE);

        if (player.hasPermission("foxtrot.manage")) {
            membersJson.command("/manageteam promote " + getName()).tooltip("§bClick to promote members");
        }

        int onlineMembers = 0;

        for (ObjectId allyId : getAllies()) {
            Team ally = Samurai.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.GOLD).append("[").append(ChatColor.GREEN).append(ally.getOnlineFlashMembers().size()).append("/").append(ally.getSize()).append(ChatColor.GOLD).append("]").append(ChatColor.GRAY).append(", ");
            }
        }

        for (FlashPlayer onlineMember : getOnlineFlashMembers()) {
            onlineMembers++;

            // There can only be one owner, so we special case it.
            if (isOwner(onlineMember.getPlayerUUID())) {
                continue;
            }

            FancyMessage appendTo = membersJson;
            if (isCoLeader(onlineMember.getPlayerUUID())) {
                appendTo = coleadersJson;
            } else if (isCaptain(onlineMember.getPlayerUUID())) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(onlineMember.getName()).color(ChatColor.GREEN).then("[").color(ChatColor.YELLOW);
            appendTo.then(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(onlineMember.getPlayerUUID()).getKills() + "").color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);
        }

        for (UUID offlineMember : getMembers().stream().filter(uuid -> !getOnlineFlashMembers().stream().map(FlashPlayer::getPlayerUUID).toList().contains(uuid)).toList()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            FancyMessage appendTo = membersJson;
            if (isCoLeader(offlineMember)) {
                appendTo = coleadersJson;
            } else if (isCaptain(offlineMember)) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(UUIDUtils.name(offlineMember)).color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED : ChatColor.GRAY);
            appendTo.then("[").color(ChatColor.YELLOW).then("" + Samurai.getInstance().getMapHandler().getStatsHandler().getStats(offlineMember).getKills()).color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);

        }

        // Now we can actually send all that info we just processed.
        player.sendMessage(GRAY_LINE);

        FancyMessage teamLine = new FancyMessage();

        teamLine.text(ChatColor.GOLD + getName());
        teamLine.then().text(ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.WHITE + " - ");
        teamLine.then().text(ChatColor.GRAY + "HQ: " + ChatColor.YELLOW + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));

        if (HQ != null && player.hasPermission("foxtrot.staff")) {
            teamLine.command("/tppos " + HQ.getBlockX() + " " + HQ.getBlockY() + " " + HQ.getBlockZ());
            teamLine.tooltip("§6Click to warp to HQ");
        }

        if (player.hasPermission("foxtrot.manage")) {
            teamLine.then().text("§f - §6[Manage]").color(ChatColor.YELLOW).command("/manageteam manage " + getName()).tooltip("§bClick to manage team");
        }

        teamLine.then().text("§f - §6[Focus]").color(ChatColor.YELLOW).command("/f focus " + getName()).tooltip("§eClick to focus team");

        teamLine.send(player);

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.WHITE + "Allies: " + allies);
        }

        ChatColor color = ChatColor.GRAY;
        if (owner.getPlayer() == null) {
            if (owner.getSpoofPlayer() != null) color = ChatColor.GREEN;
        } else {
            color = ChatColor.GREEN;
            if (ModUtils.isInvisible(owner.getPlayer())) color = ChatColor.GRAY;
            if (deathbanMap.isDeathbanned(getOwner())) color = ChatColor.RED;
        }

        FancyMessage leader = new FancyMessage(ChatColor.WHITE + "Leader: " + color + owner.getName() + ChatColor.YELLOW + "[" + ChatColor.GREEN + Samurai.getInstance().getMapHandler().getStatsHandler().getStats(getOwner()).getKills() + ChatColor.YELLOW + "]");

        if (player.hasPermission("foxtrot.manage")) {
            leader.command("/manageteam leader " + getName()).tooltip("§bClick to change leader");
        }

        leader.send(player);

        if (!ChatColor.stripColor(coleadersJson.toOldMessageFormat()).endsWith("s: ")) {
            coleadersJson.send(player);
        }

        if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
            captainsJson.send(player);
        }

        if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
            membersJson.send(player);
        }

        FancyMessage balance = new FancyMessage(ChatColor.WHITE + "Balance: " + ChatColor.GREEN + "$" + Math.round(getBalance()));

        if (player.hasPermission("foxtrot.manage")) {
            balance.command("/manageteam balance " + getName()).tooltip("§bClick to modify team balance");
        }

        balance.send(player);

        FancyMessage dtrMessage = new FancyMessage(ChatColor.WHITE + "Deaths until Raidable: " + getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix());


        if (player.hasPermission("foxtrot.manage.dtr")) {
            dtrMessage.command("/manageteam dtr " + getName()).tooltip("§bClick to modify team DTR");
        }

        dtrMessage.send(player);

        if (isMember(player.getUniqueId()) || player.hasPermission("foxtrot.manage")) {
            if (Samurai.getInstance().getServerHandler().isForceInvitesEnabled()) {
                player.sendMessage(ChatColor.WHITE + "Force Invites: " + ChatColor.GOLD + getForceInvites());
            }
            player.sendMessage(ChatColor.WHITE + "KOTH Captures: " + ChatColor.GOLD + getKothCaptures());
            if (!getActiveBoosters().isEmpty()) {
                for (TeamBoosterType booster : getActiveBoosters()) {
                    player.sendMessage(CC.translate(booster.getDisplayName()) + ChatColor.WHITE + " Booster: " + ChatColor.GOLD + TimeUtils.formatIntoDetailedString((int) ((getBoosters().get(booster) - System.currentTimeMillis()) / 1000)));
                }
            }
        }

        if (Samurai.getInstance().getServerHandler().isReducedDTR()) {
            player.sendMessage(CC.WHITE + "Reduced DTR Deaths: " + CC.GOLD + reducedDTR);
        }

        player.sendMessage(CC.WHITE + "Points: " + CC.GOLD + Samurai.getInstance().getTopHandler().getTotalPoints(this));

        if (player.hasPermission("foxtrot.managepoints")) {
            player.sendMessage(CC.WHITE + "Raw Points: " + CC.GOLD + (Samurai.getInstance().getTopHandler().getTotalPoints(this) - getAddedPoints()));

        }

        if (DTRHandler.isOnCooldown(this)) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.WHITE + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
            } else {
                FancyMessage message = new FancyMessage(ChatColor.WHITE + "Time Until Regen: ")
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.then(TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000)).color(ChatColor.GOLD)
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.send(player);
            }
        }

        // Only show this if they're a member.
        if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
            player.sendMessage(ChatColor.WHITE + "Announcement: " + ChatColor.GOLD + announcement);
        }

        player.sendMessage(GRAY_LINE);
        // .... and that is how we do a /f who.
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Team other)) {
            return false;
        }

        return other.uniqueId.equals(uniqueId);
    }

    public ChatColor getDTRColor() {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / getMaxDTR() <= 1) {
            if (isRaidable()) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                if (DTR == 1) {
                    dtrColor = ChatColor.YELLOW;
                } else {
                    dtrColor = ChatColor.GREEN;
                }
            }
        }

        return (dtrColor);
    }

    public String getDTRSuffix() {
        if (DTRHandler.isRegenerating(this)) {
            if (getOnlineMemberAmount() == 0) {
                return (ChatColor.GRAY + "◀");
            } else {
                return (ChatColor.GREEN + "▲");
            }
        } else if (DTRHandler.isOnCooldown(this)) {
            return (ChatColor.RED + "■");
        } else {
            return (ChatColor.GREEN + "◀");
        }
    }

    public int getWeight(Player player) {
        return (isOwner(player.getUniqueId()) ? 5 : isCoLeader(player.getUniqueId()) ? 4 : isCaptain(player.getUniqueId()) ? 3 : 2);
    }

    public List<Player> getSortedOnlineMembers() {
        List<Player> members = new ArrayList<>(this.getOnlineMembers());
        members.sort(Comparator.comparingInt(this::getWeight));
        Collections.reverse(members);
        return members;
    }

    public static BasicDBList uuidsToStrings(Collection<UUID> toConvert) {
        if (toConvert != null && !toConvert.isEmpty()) {
            BasicDBList dbList = new BasicDBList();

            for (UUID uuid : toConvert) {
                dbList.add(uuid.toString());
            }

            return dbList;
        } else {
            return new BasicDBList();
        }
    }

    public void createLog(UUID executor, String action, String command) {
        new TeamLog(UUID.randomUUID(), executor, this.getUniqueId(), action, System.currentTimeMillis(), command).save();
    }

    public void createLog(UUID executor, String action, String command, long time) {
        new TeamLog(UUID.randomUUID(), executor, this.getUniqueId(), action, time, command).save();
    }

    public List<TeamLog> getLogs() {
        List<TeamLog> documents = new ArrayList<>();

        for (Document document : Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("TeamLogs").find(Filters.eq("teamUUID", getUniqueId()))) {
            documents.add(TeamLog.fromDocument(document));
        }
        Collections.reverse(documents);

        return documents;
    }

}
