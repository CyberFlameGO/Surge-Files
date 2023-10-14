package dev.lbuddyboy.samurai;

import co.aikar.commands.PaperCommandManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.libs.lLib;
import dev.lbuddyboy.samurai.api.impl.FoxtrotAPI;
import dev.lbuddyboy.samurai.api.papi.SamuraiExtension;
import dev.lbuddyboy.samurai.chat.ChatHandler;
import dev.lbuddyboy.samurai.commands.menu.playtime.PlayTimeRewardsManager;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.commands.staff.SamuraiCommand;
import dev.lbuddyboy.samurai.custom.FixListener;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.custom.ability.crate.PartnerCrateHandler;
import dev.lbuddyboy.samurai.custom.airdrop.AirdropHandler;
import dev.lbuddyboy.samurai.custom.arena.ArenaHandler;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassHandler;
import dev.lbuddyboy.samurai.custom.daily.DailyHandler;
import dev.lbuddyboy.samurai.custom.deepdark.DeepDarkHandler;
import dev.lbuddyboy.samurai.custom.feature.FeatureHandler;
import dev.lbuddyboy.samurai.custom.pets.PetHandler;
import dev.lbuddyboy.samurai.custom.power.PowerHandler;
import dev.lbuddyboy.samurai.custom.reclaim.ReclaimHandler;
import dev.lbuddyboy.samurai.custom.redeem.RedeemHandler;
import dev.lbuddyboy.samurai.custom.schedule.ScheduleHandler;
import dev.lbuddyboy.samurai.custom.schedule.command.ScheduleCommand;
import dev.lbuddyboy.samurai.custom.shop.ShopHandler;
import dev.lbuddyboy.samurai.custom.slots.SlotHandler;
import dev.lbuddyboy.samurai.custom.supplydrops.SupplyDropHandler;
import dev.lbuddyboy.samurai.custom.teamwar.TeamWarHandler;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.deathmessage.DeathMessageHandler;
import dev.lbuddyboy.samurai.events.EventHandler;
import dev.lbuddyboy.samurai.events.citadel.CitadelHandler;
import dev.lbuddyboy.samurai.events.conquest.ConquestHandler;
import dev.lbuddyboy.samurai.events.region.loothill.LootHillHandler;
import dev.lbuddyboy.samurai.events.region.cavern.CavernHandler;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowHandler;
import dev.lbuddyboy.samurai.listener.*;
import dev.lbuddyboy.samurai.listener.patch.*;
import dev.lbuddyboy.samurai.lunar.LunarHandler;
import dev.lbuddyboy.samurai.map.MapHandler;
import dev.lbuddyboy.samurai.map.crates.CrateHandler;
import dev.lbuddyboy.samurai.map.game.arena.select.SelectionListeners;
import dev.lbuddyboy.samurai.map.game.impl.crystal.CrystalListeners;
import dev.lbuddyboy.samurai.map.game.impl.ffa.FFAListeners;
import dev.lbuddyboy.samurai.map.game.impl.gungame.GunGameListeners;
import dev.lbuddyboy.samurai.map.game.impl.spleef.SpleefListeners;
import dev.lbuddyboy.samurai.map.game.impl.sumo.SumoListeners;
import dev.lbuddyboy.samurai.map.game.impl.tntrun.TNTRunListeners;
import dev.lbuddyboy.samurai.map.game.listener.GameListeners;
import dev.lbuddyboy.samurai.map.kits.listener.KitEditorListener;
import dev.lbuddyboy.samurai.map.listener.BlockDecayListeners;
import dev.lbuddyboy.samurai.map.offline.OfflineHandler;
import dev.lbuddyboy.samurai.map.shards.ShardHandler;
import dev.lbuddyboy.samurai.server.threads.PacketBorderThread;
import dev.lbuddyboy.samurai.persist.RedisSaveTask;
import dev.lbuddyboy.samurai.persist.maps.*;
import dev.lbuddyboy.samurai.persist.maps.statistics.*;
import dev.lbuddyboy.samurai.persist.maps.toggle.*;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardHandler;
import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.server.deathban.DeathbanListener;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.TeamHandler;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRHandler;
import dev.lbuddyboy.samurai.team.ftop.FTopHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.RegenUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.object.RedisCommand;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.api.HalfHourEvent;
import dev.lbuddyboy.samurai.api.HourEvent;
import dev.lbuddyboy.samurai.util.serialization.*;
import dev.lbuddyboy.samurai.util.item.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.server.timer.TimerHandler;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Getter
public class Samurai extends JavaPlugin {

    @Getter
    @Setter
    public static String MONGO_DB_NAME = "HCTeams";
    @Getter
    private static Samurai instance;
    public static final Random RANDOM = new Random();
    public static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(org.bukkit.util.Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).setPrettyPrinting().serializeNulls().create();
    public static final Gson PLAIN_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls().create();

    private MongoClient mongoPool;
    @Setter private Config messageConfig;

    private AirdropHandler airdropHandler;
    private ArenaHandler arenaHandler;
    private ChatHandler chatHandler;
    private DailyHandler dailyHandler;
    private PvPClassHandler pvpClassHandler;
    private TeamHandler teamHandler;
    private ServerHandler serverHandler;
    private MapHandler mapHandler;
    private CitadelHandler citadelHandler;
    private DeepDarkHandler deepDarkHandler;
    private EventHandler eventHandler;
    private OfflineHandler offlineHandler;
    private ConquestHandler conquestHandler;
    private CavernHandler cavernHandler;
    private GlowHandler glowHandler;
    private LootHillHandler lootHillHandler;
    private CrateHandler crateHandler;
    private ReclaimHandler reclaimHandler;
    private ShardHandler shardHandler;
    private BattlePassHandler battlePassHandler;
    private RedeemHandler redeemHandler;
    private AbilityItemHandler abilityItemHandler;
    private ScheduleHandler scheduleHandler;
    private PartnerCrateHandler partnerCrateHandler;
    private PetHandler petHandler;
    private SupplyDropHandler supplyDropHandler;
    private FTopHandler topHandler;
    private LunarHandler lunarHandler;
    private ShopHandler shopHandler;
    private FeatureHandler featureHandler;
    private VaultHandler vaultHandler;
    private PlayTimeRewardsManager playTimeRewardsManager;
    private CombatLoggerListener combatLoggerListener;
    private PowerHandler powerHandler;
    private PaperCommandManager paperCommandManager;
    private ScoreboardHandler scoreboardHandler;
    private TimerHandler timerHandler;
    private LootTableHandler lootTableHandler;
    private SlotHandler slotHandler;
    private TeamWarHandler teamWarHandler;

    private JedisPool jedisPool;

    @Setter
    private Predicate<Player> inDuelPredicate = (player) -> mapHandler.getDuelHandler().isInDuel(player);

    @Override
    public void onEnable() {

        instance = this;
        saveDefaultConfig();

        try {
            if (getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
                mongoPool = new MongoClient(
                        new ServerAddress(
                                getConfig().getString("MONGO.HOST"),
                                getConfig().getInt("MONGO.PORT")),
                        MongoCredential.createCredential(
                                getConfig().getString("MONGO.AUTHENTICATION.USERNAME"),
                                "admin", getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()),
                        MongoClientOptions.builder().build()
                );
            } else {
                mongoPool = new MongoClient(getConfig().getString("MONGO.HOST"), getConfig().getInt("MONGO.PORT"));
            }

            Bukkit.getConsoleSender().sendMessage(CC.translate("&fSuccessfully connected the &6Mongo Database"));

            setMONGO_DB_NAME(getConfig().getString("serverName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.jedisPool = lLib.getInstance().getJedisPool();

        this.messageConfig = new Config(this, "messages");
        for (MessageConfiguration language : MessageConfiguration.values()) language.loadDefault();

        (new DTRHandler()).runTaskTimer(this, 20L, 450L);
        (new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
        (new PacketBorderThread()).start();

        setupCommands();
        setupHandlers();

        setupPersistence();
        setupListeners();
        setupTasks();

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setAutoSave(true);
            world.setTicksPerMonsterSpawns(300);
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doPatrolSpawning", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("mobSpawning", "false");
            world.setGameRuleValue("doImmediateRespawn", "true");

            world.getEntitiesByClasses(Villager.class).forEach(Entity::remove);
        }

        ItemUtils.load();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getPluginManager().registerEvents(new FoxtrotAPI(), this);
        new SamuraiExtension(this).register();

        if (scheduleHandler.getScheduledTimes().containsKey("Scheduled-Reboot")) {
            ScheduleCommand.scheduleRemove(Bukkit.getConsoleSender(), scheduleHandler.getScheduledTimes().get("Scheduled-Reboot"));
        }
        ScheduleCommand.scheduleAdd(Bukkit.getConsoleSender(), "Scheduled-Reboot", "12h", "reboot");
    }

    @Override
    public void onDisable() {

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Villager)) continue;

                entity.remove();
            }
        }

        saveAll();

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            Samurai.getInstance().getMapHandler().getBountyManager().save();
        }

        RegenUtils.resetAll();

        try {
            Samurai.getInstance().runRedisCommand((jedis) -> {
                jedis.save();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveAll() {
        if (SamuraiCommand.reset) return;

        if (this.mapHandler.isKitMap()) this.mapHandler.getKitManager().saveData();
        if (deepDarkHandler.getDarkEntity() != null) deepDarkHandler.getDarkEntity().getWarden().remove();
        this.offlineHandler.disable();
        this.playtimeMap.unload();

        RedisSaveTask.save(null, false);

        eventHandler.saveEvents();
        scoreboardHandler.unload();
        supplyDropHandler.disable();

        serverHandler.save();
        mapHandler.getStatsHandler().save();

        if (Samurai.getInstance().getMapHandler().getGameHandler() != null) {
            if (Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
                Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
            }
        }

        try {
            if (FrozenEconomyHandler.isInitiated()) {
                FrozenEconomyHandler.saveAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            return null;
        }

        return CoreProtect;
    }

    private void setupCommands() {
        this.paperCommandManager = new PaperCommandManager(this);
        this.paperCommandManager.enableUnstableAPI("help");
        new CommandHandler(this.paperCommandManager);
    }

    private void setupHandlers() {
        lootTableHandler = new LootTableHandler(this);
        serverHandler = new ServerHandler();
        arenaHandler = new ArenaHandler();
        mapHandler = new MapHandler();
        mapHandler.load();

        this.airdropHandler = new AirdropHandler();
        dailyHandler = new DailyHandler();
        teamWarHandler = new TeamWarHandler();

        teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();

        chatHandler = new ChatHandler();
        citadelHandler = new CitadelHandler();
        deepDarkHandler = new DeepDarkHandler();
        pvpClassHandler = new PvPClassHandler();
        eventHandler = new EventHandler();
        conquestHandler = new ConquestHandler();

        abilityItemHandler = new AbilityItemHandler();
        partnerCrateHandler = new PartnerCrateHandler();
        petHandler = new PetHandler();
        offlineHandler = new OfflineHandler();
        topHandler = new FTopHandler();
        slotHandler = new SlotHandler();
        lunarHandler = new LunarHandler(this);
        playTimeRewardsManager = new PlayTimeRewardsManager();

        if (getConfig().getBoolean("glowstoneMountain", false)) {
            glowHandler = new GlowHandler();
        }

        if (getConfig().getBoolean("cavern", false)) {
            cavernHandler = new CavernHandler();
        }

        crateHandler = new CrateHandler();
        reclaimHandler = new ReclaimHandler();

        shardHandler = new ShardHandler();
        shardHandler.loadChances();

        if (!mapHandler.isKitMap()) {
            battlePassHandler = new BattlePassHandler();
        }

        redeemHandler = new RedeemHandler();
        supplyDropHandler = new SupplyDropHandler();
        vaultHandler = new VaultHandler();
        scheduleHandler = new ScheduleHandler();

        DeathMessageHandler.init();

        shopHandler = new ShopHandler();
        featureHandler = new FeatureHandler();
        lootHillHandler = new LootHillHandler();
        powerHandler = new PowerHandler();
        scoreboardHandler = new ScoreboardHandler();
        timerHandler = new TimerHandler();

        setupHourEvents();
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("qLib - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(Calendar.MINUTE);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = minToHour >= 30 ? minToHour : 30 - minOfHour;
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))), minToHour, 60L, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HalfHourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE)))), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new AutoRebuildListener(), this);
        getServer().getPluginManager().registerEvents(new BeaconFixListener(), this);
        getServer().getPluginManager().registerEvents(new MapListener(), this);
        getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BorderListener(), this);
        getServer().getPluginManager().registerEvents(new LunarClientListener(this), this);
        getServer().getPluginManager().registerEvents((combatLoggerListener = new CombatLoggerListener()), this);
        getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
        getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
        getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantLimitListener(), this);
        getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
        getServer().getPluginManager().registerEvents(new EndListener(this), this);
        getServer().getPluginManager().registerEvents(new FastBowListener(), this);
        getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
        getServer().getPluginManager().registerEvents(new FoxListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
        getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
        getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
        getServer().getPluginManager().registerEvents(new PotionLimitListener(), this);
        getServer().getPluginManager().registerEvents(new NetherPortalListener(), this);
        getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
        getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
        getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
        getServer().getPluginManager().registerEvents(new DomainListener(), this);
        getServer().getPluginManager().registerEvents(new TeamRequestSpamListener(), this);
        getServer().getPluginManager().registerEvents(new FixListener(), this);
        getServer().getPluginManager().registerEvents(new OptimisationListener(), this);
        getServer().getPluginManager().registerEvents(new JumpPadListener(), this);
        getServer().getPluginManager().registerEvents(new EntityStackListener(), this);
        getServer().getPluginManager().registerEvents(new EntityLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new ArmorDamageListener(), this);

        if (getServerHandler().isBlockEntitiesThroughPortals()) {
            getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
        }

        if (getServerHandler().isBlockRemovalEnabled()) {
            getServer().getPluginManager().registerEvents(new BlockRegenListener(), this);
        }

        // Register KitMap specific listeners
        getServer().getPluginManager().registerEvents(new KitMapListener(), this);
        getServer().getPluginManager().registerEvents(new KitEditorListener(), this);
        getServer().getPluginManager().registerEvents(new BlockDecayListeners(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        getServer().getPluginManager().registerEvents(new SumoListeners(), this);
        getServer().getPluginManager().registerEvents(new FFAListeners(), this);
        getServer().getPluginManager().registerEvents(new GunGameListeners(), this);
        getServer().getPluginManager().registerEvents(new TNTRunListeners(), this);
        getServer().getPluginManager().registerEvents(new CrystalListeners(), this);
        getServer().getPluginManager().registerEvents(new SpleefListeners(), this);
        getServer().getPluginManager().registerEvents(new SelectionListeners(), this);

        getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), this);
    }

	/*
	Persist Redis Stuff
	 */

    private void setupPersistence() {
        (supplyCrateSBMap = new SupplyCrateSBMap()).loadFromRedis();
        (saleSBMap = new SaleSBMap()).loadFromRedis();
        (scoreboardMap = new ScoreboardMap()).loadFromRedis();
        (playtimeMap = new PlaytimeMap()).loadFromRedis();
        (afkMap = new AFKMap()).loadFromRedis();
        (oppleMap = new OppleMap()).loadFromRedis();
        (deathbanMap = new DeathbanMap()).loadFromRedis();
        (PvPTimerMap = new PvPTimerMap()).loadFromRedis();
        (startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
        (killstreakMap = new KillstreakMap()).loadFromRedis();
        (chatModeMap = new ChatModeMap()).loadFromRedis();
        (toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
        (fishingKitMap = new FishingKitMap()).loadFromRedis();
        (friendLivesMap = new FriendLivesMap()).loadFromRedis();
        (chatSpyMap = new ChatSpyMap()).loadFromRedis();
        (diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
        (goldMinedMap = new GoldMinedMap()).loadFromRedis();
        (ironMinedMap = new IronMinedMap()).loadFromRedis();
        (coalMinedMap = new CoalMinedMap()).loadFromRedis();
        (redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
        (lapisMinedMap = new LapisMinedMap()).loadFromRedis();
        (emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
        (firstJoinMap = new FirstJoinMap()).loadFromRedis();
        (lastJoinMap = new LastJoinMap()).loadFromRedis();
        (leftGameMap = new LeftGameMap()).loadFromRedis();

        (duelsWonMap = new DuelsWonMap()).loadFromRedis();
        (eventsWonMap = new EventsWonMap()).loadFromRedis();
        (highestBalanceMap = new HighestBalanceMap()).loadFromRedis();
        (uniqueLoginsMap = new UniqueLoginsMap()).loadFromRedis();

        (wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
        (toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
        (toggleDeathMessageMap = new ToggleDeathMessageMap()).loadFromRedis();
        (toggleClaimDisplayMap = new ToggleClaimDisplayMap()).loadFromRedis();
        (diedMap = new DiedMap()).loadFromRedis();
        (toggleClaimMessageMap = new ToggleClaimMessageMap()).loadFromRedis();
        (toggleFocusDisplayMap = new ToggleFocusDisplayMap()).loadFromRedis();
        (battlePassTitleMap = new BattlePassTitleMap()).loadFromRedis();
        (reclaimBCToggleMap = new ReclaimBCToggleMap()).loadFromRedis();
        (offlineInvEditedMap = new OfflineInvEditedMap()).loadFromRedis();
        (redeemBCToggleMap = new RedeemBCToggleMap()).loadFromRedis();
        (powerMap = new PetMap()).loadFromRedis();
        (cobblePickupMap = new CobblePickupMap()).loadFromRedis();
        (mobDropsPickupMap = new MobDropsPickupMap()).loadFromRedis();
        (shardMap = new ShardMap()).loadFromRedis();
        (bountyCooldownMap = new BountyCooldownMap()).loadFromRedis();
        (teamBundleMap = new TeamBundleMap()).loadFromRedis();
        (filterModeMap = new FilterModeMap()).loadFromRedis();
    }

    private void setupTasks() {
        // unlocks claims at 10 minutes left of SOTW timer
        new BukkitRunnable() {
            @Override
            public void run() {
                if (SOTWCommand.isSOTWTimer()) {
                    long endsAt = SOTWCommand.getCustomTimers().get("&a&lSOTW");
                    if (endsAt - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(10L)) {
                        for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                            if (team.isClaimLocked()) {
                                team.setClaimLocked(false);
                                team.sendMessage(CC.YELLOW + "Your team's claims have been unlocked due to SOTW ending in 10 minutes!");
                            }
                        }
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
    }

    private FilterModeMap filterModeMap;
    private PlaytimeMap playtimeMap;
    private AFKMap afkMap;
    private OfflineInvEditedMap offlineInvEditedMap;
    private OppleMap oppleMap;
    private SaleSBMap saleSBMap;
    private DeathbanMap deathbanMap;
    private PvPTimerMap PvPTimerMap;
    private StartingPvPTimerMap startingPvPTimerMap;
    private KillstreakMap killstreakMap;
    private ChatModeMap chatModeMap;
    private FishingKitMap fishingKitMap;
    private ToggleGlobalChatMap toggleGlobalChatMap;
    private ChatSpyMap chatSpyMap;
    private DiamondMinedMap diamondMinedMap;
    private GoldMinedMap goldMinedMap;
    private IronMinedMap ironMinedMap;
    private CoalMinedMap coalMinedMap;
    private DiedMap diedMap;
    private RedstoneMinedMap redstoneMinedMap;
    private LapisMinedMap lapisMinedMap;
    private EmeraldMinedMap emeraldMinedMap;
    private FirstJoinMap firstJoinMap;
    private LastJoinMap lastJoinMap;
    private FriendLivesMap friendLivesMap;
    private BaseStatisticMap duelsWonMap, eventsWonMap, highestBalanceMap, uniqueLoginsMap;
    private WrappedBalanceMap wrappedBalanceMap;
    private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
    private ToggleDeathMessageMap toggleDeathMessageMap;
    private ToggleClaimDisplayMap toggleClaimDisplayMap;
    private ToggleClaimMessageMap toggleClaimMessageMap;
    private ToggleFocusDisplayMap toggleFocusDisplayMap;
    private CobblePickupMap cobblePickupMap;
    private MobDropsPickupMap mobDropsPickupMap;
    private ShardMap shardMap;
    private BountyCooldownMap bountyCooldownMap;
    private ScoreboardMap scoreboardMap;
    private RedeemBCToggleMap redeemBCToggleMap;
    private PetMap powerMap;
    private ReclaimBCToggleMap reclaimBCToggleMap;
    private BattlePassTitleMap battlePassTitleMap;
    private SupplyCrateSBMap supplyCrateSBMap;
    private TeamBundleMap teamBundleMap;
    private LeftGameMap leftGameMap;

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.jedisPool.getResource();
        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception var9) {
            var9.printStackTrace();
            if (jedis != null) {
                this.jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                this.jedisPool.returnResource(jedis);
            }

        }
        return result;
    }

    public boolean inEvent(Player player) {
        return player != null && player.hasMetadata("gaming");
    }

    public static boolean isDev() {
        return instance.getServerHandler().getServerName().contains("Dev");
    }

}
