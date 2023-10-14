package dev.lbuddyboy.samurai.map;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventHandler;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.listener.BorderListener;
import dev.lbuddyboy.samurai.map.bounty.BountyManager;
import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.killstreaks.KillstreakHandler;
import dev.lbuddyboy.samurai.map.kits.KitManager;
import dev.lbuddyboy.samurai.map.kits.upgrades.KitUpgradesHandler;
import dev.lbuddyboy.samurai.map.stats.StatsHandler;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.impl.FoxtrotNametagProvider;
import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.server.deathban.Deathban;
import dev.lbuddyboy.samurai.team.track.TeamActionTracker;
import dev.lbuddyboy.samurai.util.FileHelper;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.duel.DuelHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class MapHandler {

    private transient File mapInfo;

    private boolean kitMap;
    private boolean halloween;
    private int allyLimit;
    private int teamSize;
    private long regenTimeDeath;
    private long regenTimeRaidable;
    private String scoreboardTitle;
    private String mapStartedString;
    private double baseLootingMultiplier;
    private double level1LootingMultiplier;
    private double level2LootingMultiplier;
    private double level3LootingMultiplier;
    private boolean craftingGopple;
    private boolean craftingReducedMelon;
    private int goppleCooldown;
    private int minForceInviteMembers = 10;
    private String endPortalLocation;
    private boolean fastSmeltEnabled;
    @Setter private int netherBuffer;
    @Setter private int worldBuffer;
    private float dtrIncrementMultiplier;

    private StatsHandler statsHandler;
    private KitManager kitManager;
    private GameHandler gameHandler;
    private DuelHandler duelHandler;

    // Kit-Map only stuff:

    private KillstreakHandler killstreakHandler;
    private BountyManager bountyManager;
    private KitUpgradesHandler kitUpgradesHandler;

    private boolean broadcastStarted;

    public MapHandler() {

    }

    public void load() {
        reloadConfig();

        FrozenNametagHandler.init();
        FrozenNametagHandler.registerProvider(new FoxtrotNametagProvider());

/*        Foxtrot.getInstance().setAssemble(new Assemble(Foxtrot.getInstance(), new FoxtrotScoreGetter()));
        Foxtrot.getInstance().getAssemble().setTicks(2);
        Foxtrot.getInstance().getAssemble().setAssembleStyle(AssembleStyle.KOHI);*/

        FrozenEconomyHandler.init();

        Iterator<Recipe> recipeIterator = Samurai.getInstance().getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            // Disallow the crafting of gopples.
            if (!craftingGopple && recipe.getResult().getDurability() == (short) 1 && recipe.getResult().getType() == org.bukkit.Material.GOLDEN_APPLE) {
                recipeIterator.remove();
            }

            // Remove vanilla glistering melon recipe
            if (craftingReducedMelon && recipe.getResult().getType() == Material.GLISTERING_MELON_SLICE) {
                recipeIterator.remove();
            }

            if (Samurai.getInstance().getConfig().getBoolean("rodPrevention") && recipe.getResult().getType() == Material.FISHING_ROD) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.TNT_MINECART) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.END_CRYSTAL) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_HELMET) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_CHESTPLATE) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_LEGGINGS) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_BOOTS) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_SWORD) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_AXE) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_PICKAXE) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.NETHERITE_SHOVEL) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.SHULKER_BOX) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.TNT) {
                recipeIterator.remove();
            }
        }

        // add our glistering melon recipe
        if (craftingReducedMelon) {
            Samurai.getInstance().getServer().addRecipe(new ShapelessRecipe(Material.GLISTERING_MELON_SLICE.getKey(), new ItemStack(Material.GLISTERING_MELON_SLICE)).addIngredient(Material.MELON).addIngredient(Material.GOLD_NUGGET));
        }

        statsHandler = new StatsHandler();
        gameHandler = new GameHandler();
        duelHandler = new DuelHandler();
        kitManager = new KitManager();

        if (isKitMap()) {
            kitUpgradesHandler = new KitUpgradesHandler();
            killstreakHandler = new KillstreakHandler();

            bountyManager = new BountyManager();

            // start a KOTH after 5 minutes of uptime
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                // Don't start a KOTH during EOTW.
                if (Samurai.getInstance().getServerHandler().isPreEOTW()) {
                    return;
                }

                // Don't start a KOTH if another one is active.
                for (Event koth : Samurai.getInstance().getEventHandler().getEvents()) {
                    if (koth.isActive()) {
                        return;
                    }
                }

                EventHandler kothHandler = Samurai.getInstance().getEventHandler();
                List<KOTH> koths = new ArrayList<>(kothHandler.getEvents().stream().filter(e -> e instanceof KOTH).map(e -> (KOTH) e).collect(Collectors.toList()));

                if (koths.isEmpty()) {
                    return;
                }

                KOTH selected = koths.get(Samurai.RANDOM.nextInt(koths.size()));
                selected.activate();
            }, 5 * 60 * 20);

            TeamActionTracker.setDatabaseLogEnabled(false);
        }

/*        if (!broadcastStarted) {
            new ServerBroadcastTask().runTaskTimerAsynchronously(Foxtrot.getInstance(), 20 * 90L, 20 * 90L);
            broadcastStarted = true;
        }*/
    }

    public void reloadConfig() {
        try {
            mapInfo = new File(Samurai.getInstance().getDataFolder(), "mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbObject = getDefaults();

                FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            } else {
                // basically check for any new keys in the defaults which aren't contained in the actual file
                // if there are any, add them to the file.
                BasicDBObject file = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

                BasicDBObject defaults = getDefaults();

                defaults.keySet().stream().filter(key -> !file.containsKey(key)).forEach(key -> file.put(key, defaults.get(key)));

                FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(file.toString())));
            }

            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

            if (dbObject != null) {
                this.kitMap = dbObject.getBoolean("kitMap", false);
                this.halloween = dbObject.getBoolean("halloween", false);
                this.allyLimit = dbObject.getInt("allyLimit", 0);
                this.teamSize = dbObject.getInt("teamSize", 30);
                this.regenTimeDeath = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeDeath", 60));
                this.regenTimeRaidable = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeRaidable", 60));
                this.scoreboardTitle = ChatColor.translateAlternateColorCodes('&', dbObject.getString("scoreboardTitle"));
                this.mapStartedString = dbObject.getString("mapStartedString");
                ServerHandler.WARZONE_RADIUS = dbObject.getInt("warzone", 1000);
                BorderListener.BORDER_SIZE = dbObject.getInt("border", 3000);
                BorderListener.BORDER_SIZE_NETHER = dbObject.getInt("netherborder", 1000);
                this.goppleCooldown = dbObject.getInt("goppleCooldown");
                this.netherBuffer = dbObject.getInt("netherBuffer");
                this.worldBuffer = dbObject.getInt("worldBuffer");
                this.endPortalLocation = dbObject.getString("endPortalLocation");
                this.fastSmeltEnabled = dbObject.getBoolean("fastSmeltEnabled", true);

                BasicDBObject looting = (BasicDBObject) dbObject.get("looting");

                this.baseLootingMultiplier = looting.getDouble("base");
                this.level1LootingMultiplier = looting.getDouble("level1");
                this.level2LootingMultiplier = looting.getDouble("level2");
                this.level3LootingMultiplier = looting.getDouble("level3");

                BasicDBObject crafting = (BasicDBObject) dbObject.get("crafting");

                this.craftingGopple = crafting.getBoolean("gopple");
                this.craftingReducedMelon = crafting.getBoolean("reducedMelon");

                if (dbObject.containsKey("deathban")) {
                    BasicDBObject deathban = (BasicDBObject) dbObject.get("deathban");

                    Deathban.load(deathban);
                }

                if (dbObject.containsKey("minForceInviteMembers")) {
                    minForceInviteMembers = dbObject.getInt("minForceInviteMembers");
                }

                this.dtrIncrementMultiplier = (float) dbObject.getDouble("dtrIncrementMultiplier", 4.5F);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BasicDBObject getDefaults() {
        BasicDBObject dbObject = new BasicDBObject();

        BasicDBObject looting = new BasicDBObject();
        BasicDBObject crafting = new BasicDBObject();
        BasicDBObject deathban = new BasicDBObject();

        dbObject.put("kitMap", false);
        dbObject.put("allyLimit", 0);
        dbObject.put("teamSize", 5);
        dbObject.put("regenTimeDeath", 30);
        dbObject.put("regenTimeRaidable", 30);
        dbObject.put("scoreboardTitle", "&g&lSTEEL");
        dbObject.put("mapStartedString", "Map 1");
        dbObject.put("warzone", 1000);
        dbObject.put("netherBuffer", 150);
        dbObject.put("worldBuffer", 300);
        dbObject.put("endPortalLocation", "1000, 1000");
        dbObject.put("border", 3000);
        dbObject.put("netherborder", 1000);
        dbObject.put("goppleCooldown", TimeUnit.HOURS.toMinutes(3));
        dbObject.put("fastSmeltEnabled", true);

        looting.put("base", 1D);
        looting.put("level1", 1.2D);
        looting.put("level2", 1.4D);
        looting.put("level3", 2D);

        dbObject.put("looting", looting);

        crafting.put("gopple", true);
        crafting.put("reducedMelon", true);

        dbObject.put("crafting", crafting);

        deathban.put("STEEL", 5);
        deathban.put("PARTNER", 5);
        deathban.put("FAMOUS", 5);
        deathban.put("YOUTUBER", 5);
        deathban.put("QUARTZ", 10);
        deathban.put("TIN", 15);
        deathban.put("COPPER", 20);
        deathban.put("DEFAULT", 30);

        dbObject.put("deathban", deathban);

        dbObject.put("minForceInviteMembers", 10);

        dbObject.put("dtrIncrementMultiplier", 4.5F);
        return dbObject;
    }

    public void saveBorder() {
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

        if (dbObject != null) {
            dbObject.put("border", BorderListener.BORDER_SIZE); // update the border

            FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the border that was changed.
        }
    }

    public void saveNetherBorder() {
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

        if (dbObject != null) {
            dbObject.put("netherborder", BorderListener.BORDER_SIZE_NETHER); // update the border

            FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the border that was changed.
        }
    }

    public void saveNetherBuffer() {
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

        if (dbObject != null) {
            dbObject.put("netherBuffer", Samurai.getInstance().getMapHandler().getNetherBuffer()); // update the nether buffer

            FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
        }
    }

    public void saveWorldBuffer() {
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileHelper.readFile(mapInfo));

        if (dbObject != null) {
            dbObject.put("worldBuffer", Samurai.getInstance().getMapHandler().getWorldBuffer()); // update the world buffer

            FileHelper.writeFile(mapInfo, Samurai.GSON.toJson(new JsonParser().parse(dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
        }
    }

}