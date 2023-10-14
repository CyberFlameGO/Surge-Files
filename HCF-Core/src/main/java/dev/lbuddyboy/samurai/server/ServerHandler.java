package dev.lbuddyboy.samurai.server;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.commands.staff.SamuraiCommand;
import dev.lbuddyboy.samurai.server.deathban.Deathban;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlHitListeners;
import dev.lbuddyboy.samurai.server.spectate.SpectateManager;
import dev.lbuddyboy.samurai.server.threads.ClearLagTask;
import dev.lbuddyboy.samurai.server.threads.PreSOTWTask;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.server.threads.TopRankBroadcastTask;
import dev.lbuddyboy.samurai.util.item.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.server.uhc.UHCListener;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.Logout;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class ServerHandler {

    @Getter
    public static int WARZONE_RADIUS = 1000;
    @Getter
    public static int WARZONE_BORDER = 3000;
    @Getter
    private static Map<String, Logout> tasks = new HashMap<>();
    @Getter
    private static Map<String, Long> homeTimer = new ConcurrentHashMap<>();

    // NEXT MAP //
    // http://minecraft.gamepedia.com/Potion#Data_value_table

    private final Map<PotionType, PotionStatus> potionStatus = new HashMap<>();
    private final String serverName;

    private final MongoCollection<Document> serverCollection;
    private long sotwStartedAt = -1;

    private final String eventWebhook;
    private final String scheduleWebhook;
    private final SpectateManager spectateManager;
    private FishingHandler fishingHandler;

    private final boolean nodus;
    private final boolean staffHCF;
    private final boolean spring;
    private final boolean idleCheckEnabled;
    private final boolean startingTimerEnabled;
    private final boolean forceInvitesEnabled;
    private final boolean uhcHealing;
    private final boolean passiveTagEnabled;
    private final boolean allowBoosting;
    private final boolean waterPlacementInClaimsAllowed;
    private final boolean blockRemovalEnabled;

    private final boolean rodPrevention;
    private final boolean skybridgePrevention;

    private final boolean teamHQInEnemyClaims;


    @Setter
    private boolean EOTW = false;
    @Setter
    private boolean PreEOTW = false;

    private final boolean reduceArmorDamage;
    private final boolean blockEntitiesThroughPortals;

    private final ChatColor archerTagColor;
    private final ChatColor stunTagColor;
    private final ChatColor defaultRelationColor;

    private final boolean hardcore;
    private final boolean placeBlocksInCombat;

    public ServerHandler() {
        serverName = Samurai.getInstance().getConfig().getString("serverName");
        eventWebhook = Samurai.getInstance().getConfig().getString("eventWebhook", "https://discord.com/api/webhooks/935353054339944509/v_1xudEO0E8DHloZ8ZFqLv2ErbwH-JjsWhfkFHXErQnBAsFLQYMvgUsvYCWL6gN7YCOu");
        scheduleWebhook = Samurai.getInstance().getConfig().getString("scheduleWebhook", "https://discord.com/api/webhooks/935355563418726430/L7O7cdtuw6EcCafztYJac2Fivy5kG1Ko-yRmyWxRN2zM5p7hABoPmSrJElQr46BnTrE6");
        spectateManager = new SpectateManager();
        fishingHandler = new FishingHandler();

        staffHCF = Samurai.getInstance().getConfig().getBoolean("staff-hcf", false);
        spring = Samurai.getInstance().getConfig().getBoolean("spring");
        nodus = Samurai.getInstance().getConfig().getBoolean("nodus", false);
        idleCheckEnabled = Samurai.getInstance().getConfig().getBoolean("idleCheck");
        startingTimerEnabled = Samurai.getInstance().getConfig().getBoolean("startingTimer");
        forceInvitesEnabled = Samurai.getInstance().getConfig().getBoolean("forceInvites");
        uhcHealing = Samurai.getInstance().getConfig().getBoolean("uhcHealing");
        passiveTagEnabled = Samurai.getInstance().getConfig().getBoolean("passiveTag");
        allowBoosting = Samurai.getInstance().getConfig().getBoolean("allowBoosting");
        waterPlacementInClaimsAllowed = Samurai.getInstance().getConfig().getBoolean("waterPlacementInClaims");
        blockRemovalEnabled = Samurai.getInstance().getConfig().getBoolean("blockRemoval");
        rodPrevention = Samurai.getInstance().getConfig().getBoolean("rodPrevention", true);
        skybridgePrevention = Samurai.getInstance().getConfig().getBoolean("skybridgePrevention", true);
        teamHQInEnemyClaims = Samurai.getInstance().getConfig().getBoolean("teamHQInEnemyClaims", true);

        for (PotionType type : PotionType.values()) {
            if (type == PotionType.WATER) {
                continue;
            }

            PotionStatus status = new PotionStatus(Samurai.getInstance().getConfig().getBoolean("potions." + type + ".drinkables"), Samurai.getInstance().getConfig().getBoolean("potions." + type + ".splash"), Samurai.getInstance().getConfig().getInt("potions." + type + ".maxLevel", -1));
            potionStatus.put(type, status);
        }

        if (uhcHealing) {
            Bukkit.getPluginManager().registerEvents(new UHCListener(), Samurai.getInstance());
        }

        this.reduceArmorDamage = Samurai.getInstance().getConfig().getBoolean("reduceArmorDamage", true);
        this.blockEntitiesThroughPortals = Samurai.getInstance().getConfig().getBoolean("blockEntitiesThroughPortals", true);
        this.archerTagColor = ChatColor.valueOf(Samurai.getInstance().getConfig().getString("archerTagColor", "YELLOW"));
        this.stunTagColor = ChatColor.valueOf(Samurai.getInstance().getConfig().getString("stunTagColor", "BLUE"));
        this.defaultRelationColor = ChatColor.valueOf(Samurai.getInstance().getConfig().getString("defaultRelationColor", "RED"));
        this.hardcore = Samurai.getInstance().getConfig().getBoolean("hardcore", false);
        this.placeBlocksInCombat = Samurai.getInstance().getConfig().getBoolean("placeBlocksInCombat", true);

        registerPlayerDamageRestrictionListener();
//
//        GlobalTimer globalTimer = Core.getInstance().getNetworkHandler().getTimerHandler().getGlobalTimerById("SOTW");
//        if (globalTimer != null) {
//            CustomTimerCreateCommand.getCustomTimers().put("&a&lSOTW", System.currentTimeMillis() + globalTimer.getTimeLeft());
//        }
//
//        if (Foxtrot.getInstance().getServer().hasWhitelist()) {
//            globalTimer = Core.getInstance().getNetworkHandler().getTimerHandler().getGlobalTimerById("SOTWIn");
//            if (globalTimer != null) {
        new PreSOTWTask().runTaskTimer(Samurai.getInstance(), 20 * 30, 20 * 60);
        new ClearLagTask().runTaskTimer(Samurai.getInstance(), 20 * 30, 20 * 30);
        new TopRankBroadcastTask().runTaskTimerAsynchronously(Samurai.getInstance(), 20 * 60, 20 * 60);
//            }
//        }

        this.serverCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME()).getCollection("ServerInfo");

        Document document = this.serverCollection.find().first();
        if (document == null) {
            document = new Document();
            this.serverCollection.insertOne(document);
        }
        if (document.containsKey("sotwStartedAt")) {
            this.sotwStartedAt = document.getLong("sotwStartedAt");
        }

        Bukkit.getServer().getPluginManager().registerEvents(new EnderpearlHitListeners(), Samurai.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(), Samurai.getInstance());

        try {
            World end = Bukkit.getWorld("world_the_end");

            end.spawn(end.getSpawnLocation().add(0, 100, 0), EnderDragon.class);
            SamuraiCommand.scanWorld(Bukkit.getConsoleSender());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isReducedDTR() {
        if (sotwStartedAt == -1) return false;

        return (sotwStartedAt + JavaUtils.parse("24h")) - System.currentTimeMillis() > 0;
    }

    public void setSotwStartedAt() {
        this.sotwStartedAt = System.currentTimeMillis();
        Document document = this.serverCollection.find().first();
        if (document == null) {
            document = new Document();
            this.serverCollection.insertOne(document);
        }
        document.put("sotwStartedAt", sotwStartedAt);
        this.serverCollection.drop();
        this.serverCollection.insertOne(document);
    }

    public void save() {

    }

    public String getEnchants() {
        if (Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0) {
            return "No Enchants";
        } else {
            return "Prot 1" + ", Sharp 1";
        }
    }

    public boolean isWarzone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
            return (false);
        }

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            return false;
        }

        return (Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS) || ((Math.abs(loc.getBlockX()) > WARZONE_BORDER || Math.abs(loc.getBlockZ()) > WARZONE_BORDER));
    }

    public boolean isSplashPotionAllowed(PotionType type) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).splash);
    }

    public boolean isDrinkablePotionAllowed(PotionType type) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).drinkables);
    }

    public boolean isPotionLevelAllowed(PotionType type, int amplifier) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).maxLevel == -1 || potionStatus.get(type).maxLevel >= amplifier);
    }

    public void startLogoutSequence(final Player player) {
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " + ChatColor.YELLOW + "Please wait" + ChatColor.RED + " 30" + ChatColor.YELLOW + " seconds.");

        BukkitTask taskid = new BukkitRunnable() {

            int seconds = 30;

            @Override
            public void run() {
                if (player.hasMetadata("frozen")) {
                    player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
                    cancel();
                    return;
                }

                seconds--;
                //player.sendMessage(ChatColor.RED + "" + seconds + "§e seconds..."); // logout is now in the scoreboard, don't bother spamming them

                if (seconds == 0) {
                    if (tasks.containsKey(player.getName())) {
                        tasks.remove(player.getName());
                        player.setMetadata("loggedout", new FixedMetadataValue(Samurai.getInstance(), true));
                        player.kickPlayer("§cYou have been safely logged out of the server!");
                        cancel();
                    }
                }

            }
        }.runTaskTimer(Samurai.getInstance(), 20L, 20L);

        tasks.put(player.getName(), new Logout(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)));
    }

    public RegionData getRegion(Team ownerTo, Location location) {
        if (ownerTo != null && ownerTo.getOwner() == null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                return (new RegionData(RegionType.SPAWN, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
                return (new RegionData(RegionType.KOTH, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (new RegionData(RegionType.CITADEL, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.ROAD)) {
                return (new RegionData(RegionType.ROAD, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (new RegionData(RegionType.CONQUEST, ownerTo));
            }
        }

        if (ownerTo != null) {
            return (new RegionData(RegionType.CLAIMED_LAND, ownerTo));
        } else if (isWarzone(location)) {
            return (new RegionData(RegionType.WARZONE, null));
        }

        return (new RegionData(RegionType.WILDNERNESS, null));
    }

    public boolean isUnclaimed(Location loc) {
        return (LandBoard.getInstance().getClaim(loc) == null && !isWarzone(loc));
    }

    public boolean isAdminOverride(Player player) {
        return (player.getGameMode() == GameMode.CREATIVE && player.hasMetadata("Build"));
    }

    public Location getSpawnLocation() {
        return (Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 1, 0.5));
    }

    public boolean isUnclaimedOrRaidable(Location loc) {
        Team owner = LandBoard.getInstance().getTeam(loc);
        return (owner == null || owner.isRaidable());
    }

    public int getDTRLoss(Player player) {
        return (getDTRLoss(player.getLocation()));
    }

    public int getDTRLoss(Location location) {
        int dtrLoss = 1;

        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            dtrLoss = 0;
        }

        return (dtrLoss);
    }

    public long getDeathban(Player player) {
        return (getDeathban(player.getUniqueId(), player.getLocation()));
    }

    public long getDeathban(UUID playerUUID, Location location) {
        // Things we already know and can easily eliminate.
        if (isEOTW()) {
            return (TimeUnit.DAYS.toSeconds(1000));
        } else if (Samurai.getInstance().getMapHandler().isKitMap()) {
            return (TimeUnit.SECONDS.toSeconds(5));
        }

        Team ownerTo = LandBoard.getInstance().getTeam(location);
        Player player = Samurai.getInstance().getServer().getPlayer(playerUUID); // Used in various checks down below.

        // Check DTR flags, which will also take priority over playtime.
        if (ownerTo != null && ownerTo.getOwner() == null) {
            Event linkedKOTH = Samurai.getInstance().getEventHandler().getEvent(ownerTo.getName());

            // Only respect the reduced deathban if
            // The KOTH is non-existant (in which case we're probably
            // something like a 1v1 arena) or it is active.
            // If it's there but not active,
            // the null check will be false, the .isActive will be false, so we'll ignore
            // the reduced DB check.
            if (linkedKOTH == null || linkedKOTH.isActive()) {
                if (ownerTo.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(5));
                } else if (ownerTo.hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(15));
                }
            }
        }

        int max = Deathban.getDeathbanSeconds(player);

        long ban = Samurai.getInstance().getPlaytimeMap().getPlaytime(playerUUID);

        if (player != null && Samurai.getInstance().getPlaytimeMap().hasPlayed(playerUUID)) {
            ban += Samurai.getInstance().getPlaytimeMap().getCurrentSession(playerUUID) / 1000L;
        }

        return (Math.min(max, ban));
    }

    public void beginHQWarp(final Player player, final Team team, int warmup, boolean charge) {
        Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());

        // quick fix
        if (team.getBalance() < 0) {
            team.setBalance(0);
        }

        if (inClaim != null) {
            if (Samurai.getInstance().getServerHandler().isHardcore() && inClaim.getOwner() != null && !inClaim.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from an enemy's claim! Use '/f stuck' first.");
                return;
            }

            if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL))) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from inside of events!");
                return;
            }

            if (inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                if (player.getWorld().getEnvironment() != Environment.THE_END) {
                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot teleport to your end headquarters while you're in end spawn!");
                }
                return;
            }
        }


        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not go to your team headquarters while spawn tagged!");
            return;
        }

        boolean isSpawn = inClaim != null && inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE);

        if (charge && !isSpawn) {
            team.setBalance(team.getBalance() - (Samurai.getInstance().getServerHandler().isHardcore() ? 20 : 50));
        }

        player.sendMessage(ChatColor.YELLOW + "Teleporting to your team's HQ in " + ChatColor.LIGHT_PURPLE + warmup + " seconds" + ChatColor.YELLOW + "... Stay still and do not take damage.");

        /**
         * Give player heads up now. They should have 10 seconds to move even just an inch to cancel the tp if they want
         */
        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Your PvP Timer will be removed if the teleport is not cancelled.");
        }

        homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));

        final int finalWarmup = warmup;

        new BukkitRunnable() {

            int time = finalWarmup;
            Location startLocation = player.getLocation();
            double startHealth = player.getHealth();

            @Override
            public void run() {
                time--;

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) || player.getLocation().distanceSquared(startLocation) >= 0.1 || player.getHealth() < startHealth) {
                    player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                // Reset their previous health, so players can't start on 1/2 a heart, splash, and then be able to take damage before warping.
                startHealth = player.getHealth();

                // Prevent server lag from making the home time inaccurate.
                if (homeTimer.containsKey(player.getName()) && homeTimer.get(player.getName()) <= System.currentTimeMillis()) {
                    if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        Samurai.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                // After testing, this code is actually run sometimes. I'm going to leave it. FIXME
                if (time == 0) {
                    // Remove their PvP timer.
                    if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        Samurai.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.RED + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                }
            }

        }.runTaskTimer(Samurai.getInstance(), 20L, 20L);
    }

    private Map<UUID, Long> playerDamageRestrictMap = Maps.newHashMap();

    public void disablePlayerAttacking(final Player player, int seconds) {
        if (seconds == 10) {
            player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
        }

        playerDamageRestrictMap.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
    }

    private void registerPlayerDamageRestrictionListener() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onDamage(EntityDamageByEntityEvent event) {
                Long expiry = playerDamageRestrictMap.get(event.getDamager().getUniqueId());
                if (expiry != null && System.currentTimeMillis() < expiry) {
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                playerDamageRestrictMap.remove(event.getPlayer().getUniqueId());
            }
        }, Samurai.getInstance());
    }

    public boolean isSpawnBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
            return (false);
        }

        if (loc.getWorld().getName().equalsIgnoreCase("kits_events")) {
            return (false);
        }

        int radius = Samurai.getInstance().getMapHandler().getWorldBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isNetherBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NETHER) {
            return (false);
        }

        int radius = Samurai.getInstance().getMapHandler().getNetherBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    //itemstack -> amount -> price
    public Pair<ItemStack, Pair<Integer, Long>> extractSignData(Sign sign) {
        ItemStack itemStack = (sign.getLine(2).contains("Crowbar") ? InventoryUtils.CROWBAR : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", "")));

        if (itemStack == null && sign.getLine(2).contains("Antidote")) {
            itemStack = InventoryUtils.ANTIDOTE;
        }

        if (itemStack == null) {
            System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
            return null;
        }

        return null;
    }

    public void handleShopSign(Sign sign, Player player) {
        ItemStack itemStack = ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", ""));

        if (itemStack == null) {
            if (sign.getLine(2).contains("Crowbar")) {
                itemStack = InventoryUtils.CROWBAR;
            } else if (sign.getLine(2).contains("Antidote")) {
                itemStack = InventoryUtils.ANTIDOTE;
            } else if (sign.getLine(2).contains("Zombie Spawner")) {
                itemStack = InventoryUtils.ZOMBIE_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Skele Spawner")) {
                itemStack = InventoryUtils.SKELETON_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Spider Spawner")) {
                itemStack = InventoryUtils.SPIDER_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Cave Spawner")) {
                itemStack = InventoryUtils.CAVE_SPIDER_SPAWNER.clone();
            }
        }

        if (itemStack == null) {
            System.err.println("Failed to handle shop sign, null item: " + sign.getLine(2).toLowerCase().replace(" ", ""));
            return;
        }

        if (sign.getLine(0).toLowerCase().contains("buy")) {
            int price;
            int amount;

            try {
                price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException e) {
                return;
            }

            if (FrozenEconomyHandler.getBalance(player.getUniqueId()) >= price) {
                if (FrozenEconomyHandler.getBalance(player.getUniqueId()) > 100000) {
                    player.sendMessage("§cYour balance is too high. Please contact an admin to do this.");
                    Bukkit.getLogger().severe("[ECONOMY] " + player.getName() + " tried to buy shit at spawn with over 100K.");
                    return;
                }


                if (Double.isNaN(FrozenEconomyHandler.getBalance(player.getUniqueId()))) {
                    FrozenEconomyHandler.setBalance(player.getUniqueId(), 0);
                    player.sendMessage("§cYour balance was fucked, but we unfucked it.");
                    return;
                }

                if (player.getInventory().firstEmpty() != -1) {
                    FrozenEconomyHandler.withdraw(player.getUniqueId(), price);

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    player.updateInventory();

                    showSignPacket(player, sign,
                            "§aBOUGHT§r " + amount,
                            "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(price),
                            "New Balance:",
                            "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) FrozenEconomyHandler.getBalance(player.getUniqueId()))
                    );
                } else {
                    showSignPacket(player, sign,
                            "§c§lError!",
                            "",
                            "§cNo space",
                            "§cin inventory!"
                    );
                }
            } else {
                showSignPacket(player, sign,
                        "§cInsufficient",
                        "§cfunds for",
                        sign.getLine(2),
                        sign.getLine(3)
                );
            }
        } else if (sign.getLine(0).toLowerCase().contains("sell")) {
            double pricePerItem;
            int amount;

            try {
                int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));

                pricePerItem = (float) price / (float) amount;
            } catch (NumberFormatException e) {
                return;
            }

            int amountInInventory = Math.min(amount, countItems(player, itemStack.getType(), (int) itemStack.getDurability()));

            if (amountInInventory == 0) {
                showSignPacket(player, sign,
                        "§cYou do not",
                        "§chave any",
                        sign.getLine(2),
                        "§con you!"
                );
            } else {
                int totalPrice = (int) (amountInInventory * pricePerItem);

                removeItem(player, itemStack, amountInInventory);
                player.updateInventory();

                FrozenEconomyHandler.deposit(player.getUniqueId(), totalPrice);

                if (Samurai.getInstance().getBattlePassHandler() != null) {
                    Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                        progress.setValuablesSold(progress.getValuablesSold() + totalPrice);
                        progress.requiresSave();

                        Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                    });
                }

                showSignPacket(player, sign,
                        "§aSOLD§r " + amountInInventory,
                        "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
                        "New Balance:",
                        "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) FrozenEconomyHandler.getBalance(player.getUniqueId()))
                );
            }
        }
    }

    public void handleKitSign(Sign sign, Player player) {
        String kit = ChatColor.stripColor(sign.getLine(1));

        if (kit.equalsIgnoreCase("Fishing")) {
            int uses = Samurai.getInstance().getFishingKitMap().getUses(player.getUniqueId());

            if (uses == 3) {
                showSignPacket(player, sign, "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!");
            } else {
                ItemStack rod = new ItemStack(Material.FISHING_ROD);

                rod.addEnchantment(Enchantment.LURE, 2);
                player.getInventory().addItem(rod);
                player.updateInventory();
                player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
                Samurai.getInstance().getFishingKitMap().setUses(player.getUniqueId(), uses + 1);
                showSignPacket(player, sign, "§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses + 1) + "/3");
            }
        }
    }

    public void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

        for (int a = 0; a < amount; a++) {
            for (ItemStack i : p.getInventory()) {
                if (i != null) {
                    if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                        if (i.getAmount() == 1) {
                            p.getInventory().clear(p.getInventory().first(i));
                            break;
                        } else {
                            i.setAmount(i.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }

    }

    public ItemStack generateDeathSign(String killed, String killer) {
        ItemStack deathsign = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = deathsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§4" + killed);
        lore.add("§eSlain By:");
        lore.add("§a" + killer);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dDeath Sign");
        deathsign.setItemMeta(meta);

        return (deathsign);
    }

    public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
        ItemStack kothsign = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = kothsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§9" + koth);
        lore.add("§eCaptured By:");
        lore.add("§a" + capper);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add("§d" + sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dCapture Sign");
        kothsign.setItemMeta(meta);

        return (kothsign);
    }

    private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

    public void showSignPacket(Player player, final Sign sign, String... lines) {
        player.sendSignChange(sign.getLocation(), lines);

        if (showSignTasks.containsKey(sign)) {
            showSignTasks.remove(sign).cancel();
        }

        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run() {
                sign.update();
                showSignTasks.remove(sign);
            }

        };

        showSignTasks.put(sign, br);
        br.runTaskLater(Samurai.getInstance(), 90L);
    }

    public int countItems(Player player, Material material, int damageValue) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int amount = 0;

        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = material.getMaxDurability() == (short) 0;

                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
                    amount += item.getAmount();
                }
            }
        }

        return (amount);
    }

    @AllArgsConstructor
    private class PotionStatus {

        private boolean drinkables;
        private boolean splash;
        private int maxLevel;

    }

}
