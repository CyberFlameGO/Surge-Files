package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.FlashLanguage;
import dev.lbuddyboy.flash.user.User;
import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.listener.BorderListener;
import dev.lbuddyboy.samurai.listener.EndListener;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.server.threads.EcoFixTask;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.persist.RedisSaveTask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import dev.lbuddyboy.samurai.util.object.RedisCommand;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.args.FlushMode;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@CommandAlias("samurai")
@CommandPermission("op")
public class SamuraiCommand extends BaseCommand {

    public static EcoFixTask fixTask;
    public static boolean reset = false;

    @Subcommand("reloadconfig")
    public static void reloadConfig(Player sender) {
        Samurai.getInstance().getScoreboardHandler().reload();
        Samurai.getInstance().getShardHandler().loadChances();
        if (!Samurai.getInstance().getMapHandler().isKitMap()) {
            Samurai.getInstance().getBattlePassHandler().loadTiers();
        }
        Samurai.getInstance().getMapHandler().getGameHandler().reload();
        Samurai.getInstance().getDeepDarkHandler().reload();
        Samurai.getInstance().getDailyHandler().reload();
        Samurai.getInstance().getSlotHandler().reload();
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().reload();
        Samurai.getInstance().getPetHandler().reload();
        Samurai.getInstance().getAbilityItemHandler().getAbilityItems().forEach(ai -> ai.reload(Samurai.getInstance().getAbilityItemHandler().getABILITY_FOLDER()));
        Samurai.getInstance().setMessageConfig(new Config(Samurai.getInstance(), "messages"));
        Samurai.getInstance().reloadConfig();

        for (MessageConfiguration language : MessageConfiguration.values()) language.loadDefault();

        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded samurai plugin.");
    }

    @Subcommand("nbtboolean")
    public static void nbttag(Player sender, @Name("key") String key, @Name("value") boolean toggle) {
        NBTItem item = new NBTItem(sender.getItemInHand());
        item.setBoolean(key, toggle);

        sender.setItemInHand(item.getItem());
    }

    @Subcommand("nbtstring")
    public static void nbtstring(Player sender, @Name("key") String key, @Name("value") String toggle) {
        NBTItem item = new NBTItem(sender.getItemInHand());
        item.setString(key, toggle);

        sender.setItemInHand(item.getItem());
    }

    @Subcommand("scanfakeitems")
    public static void scanfakeitems(Player sender) {
        Map<Player, List<ItemStack>> fakes = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack stack : player.getInventory()) {
                if (stack == null) continue;
                if (stack.getType() == Material.AIR) continue;
                if (stack.getAmount() <= 0) continue;
                if (stack.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) && stack.getEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL) > 2) {
                    NBTItem item = new NBTItem(stack);
                    if (item.hasTag("event-item")) continue;

                    List<ItemStack> stacks = new ArrayList<>(fakes.getOrDefault(player, new ArrayList<>()));
                    stacks.add(stack);

                    fakes.put(player, stacks);
                }
                if (stack.getEnchantments().containsKey(Enchantment.ARROW_DAMAGE) && stack.getEnchantments().get(Enchantment.ARROW_DAMAGE) > 5) {
                    NBTItem item = new NBTItem(stack);
                    if (item.hasTag("event-item")) continue;

                    List<ItemStack> stacks = new ArrayList<>(fakes.getOrDefault(player, new ArrayList<>()));
                    stacks.add(stack);

                    fakes.put(player, stacks);
                }
                if (stack.getType().name().contains("NETHERITE")) {
                    NBTItem item = new NBTItem(stack);
                    if (item.hasTag("event-item")) continue;

                    List<ItemStack> stacks = new ArrayList<>(fakes.getOrDefault(player, new ArrayList<>()));
                    stacks.add(stack);

                    fakes.put(player, stacks);
                }
            }
        }

        for (Map.Entry<Player, List<ItemStack>> entry : fakes.entrySet()) {
            sender.sendMessage(CC.GREEN + entry.getKey().getName() + ": " + entry.getValue().size());
        }
    }

    @Subcommand("reloadmapconfig")
    public static void reloadMapConfig(Player sender) {
        Samurai.getInstance().getMapHandler().reloadConfig();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded mapInfo.json from file.");
    }

    @Subcommand("saveredis")
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(sender, false);
    }

    @Subcommand("redisforcesave")
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(sender, true);
    }

    @Subcommand("saveall")
    public static void save(CommandSender sender) {
        Samurai.getInstance().getMapHandler().getStatsHandler().save();
    }

    @Subcommand("setendexit")
    public static void setendexit(Player sender) {
        Location previous = EndListener.getEndReturn();
        EndListener.setEndReturn(sender.getLocation());
        Location current = EndListener.getEndReturn();

        sender.sendMessage(
                ChatColor.GREEN + "End exit (" + ChatColor.WHITE + previous.getBlockX() + ":" + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> " +
                        ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")"
        );

        EndListener.saveEndReturn();
    }

    @Subcommand("importmongo")
    public static void importMongo(CommandSender sender) {
        Tasks.runAsync(() -> {
            for (DBObject object : Samurai.getInstance().getTeamHandler().getTeamsCollection().find()) {
                if (!(object instanceof BasicDBObject obj)) continue;

                Team team = new Team(obj.getString("Name"));
                team.load(obj);

                Samurai.getInstance().getTeamHandler().setupTeam(team, true);
            }

            LandBoard.getInstance().loadFromTeams(); // to update land board shit
            Samurai.getInstance().getTeamHandler().recachePlayerTeams();
        });
    }

    @Subcommand("setcreeperloc")
    public static void setcreeperloc(Player sender) {
        Location previous = EndListener.getCreepers();
        EndListener.setCreepers(sender.getLocation());
        Location current = EndListener.getCreepers();

        sender.sendMessage(
                ChatColor.GREEN + "Creepers (" + ChatColor.WHITE + previous.getBlockX() + ":" + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> " +
                        ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")"
        );

        EndListener.saveCreeper();
    }

    @Subcommand("setendwaterloc")
    public static void setendwaterloc(Player sender) {
        Location previous = EndListener.getEndExit();
        EndListener.setEndExit(sender.getLocation());
        Location current = EndListener.getEndExit();

        sender.sendMessage(
                ChatColor.GREEN + "End Water (" + ChatColor.WHITE + previous.getBlockX() + ":" + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> " +
                        ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")"
        );

        EndListener.saveEndExit();
    }

    @Subcommand("testbug")
    public static void testbug(Player sender) {
        DiscordLogger.logRetard("Deathban arena isn't setup (SET IT UP!!!!) - Code Error #1 - " + sender.getName());
    }

    @Subcommand("giveendportalsummoner")
    public static void giveendportalsummoner(CommandSender sender, @Name("player") Player target) {
        InventoryUtils.tryFit(target.getInventory(), endportalsummoner);
    }

    @Subcommand("setnetherbuffer")
    public static void setNetherBuffer(Player sender, @Name("nether-buffer") int newBuffer) {
        Samurai.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    @Subcommand("scanworlds")
    public static void scanWorld(CommandSender sender) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.ENDER_DRAGON) continue;

                ((EnderDragon)entity).setPhase(EnderDragon.Phase.DYING);
                ((EnderDragon)entity).damage(100000);
                ((EnderDragon)entity).remove();
                if (sender instanceof Player) ((Player) sender).teleport(entity);
            }
        }
    }

    @Subcommand("resetmap")
    public static void resetmap(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME()).drop();
        Samurai.getInstance().runRedisCommand(redis -> {
            redis.flushDB(FlushMode.ASYNC);
            return null;
        });
        reset = true;
    }

    @Subcommand("stopecofix")
    public static void fixStopEco(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        fixTask.cancel();
    }

    @Subcommand("fixeco")
    public static void fixEco(CommandSender sender, @Name("name") UUID player) {
        if ((sender instanceof Player)) return;
        if (player == null) {
            return;
        }

        try {
            new EcoFixTask(Flash.getInstance().getUserHandler().relativeAltsAsync(FlashLanguage.EXEMPT_IPS.getStringList().toArray(new String[0])).get()).runTaskTimer(Samurai.getInstance(), 20, 20);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("showeco")
    public static void showEco(CommandSender sender) {
        if ((sender instanceof Player)) return;

        User premieres = Flash.getInstance().getUserHandler().tryUser(FrozenUUIDCache.uuid("Premieres"), true);
        try {
            List<UUID> uuids = Flash.getInstance().getUserHandler().relativeAltsAsync(premieres.getIp()).get();

            for (UUID uuid : uuids) {
                Team team = Samurai.getInstance().getTeamHandler().getTeam(uuid);

                sender.sendMessage(team.getName() + ": " + team.getMembers().size() + " man");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("setnetherborder")
    public static void setnetherborder(Player sender, @Name("border") int border) {
        BorderListener.BORDER_SIZE_NETHER = border;
        sender.sendMessage(ChatColor.GRAY + "The nether border is now set to " + BorderListener.BORDER_SIZE_NETHER + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMapHandler().saveBorder();
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    @Subcommand("setworldborder")
    public static void setworldborder(Player sender, @Name("border") int border) {
        BorderListener.BORDER_SIZE = border;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMapHandler().saveBorder();
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    @Subcommand("setworldbuffer")
    public static void setWorldBuffer(Player sender, @Name("worldbuffer") int newBuffer) {
        Samurai.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMapHandler().saveWorldBuffer();
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    @Subcommand("setslots|setmaxplayers")
    public static void setslots(CommandSender sender, @Name("slots") int slots) {

        sender.sendMessage(ChatColor.GOLD + "Set the slots to " + slots);

        try {
            changeSlots(slots);
        }

        catch (ReflectiveOperationException expeption) {
            expeption.printStackTrace();
        }

    }

    @Subcommand("setsimulationdistance|setsimdistance|setsimdist")
    public static void setsimulationdistance(CommandSender sender, @Name("distance") int slots) {

        sender.sendMessage(ChatColor.GOLD + "Set the simulation distance to " + slots);

        try {
            changeSimDistance(slots);
        }

        catch (ReflectiveOperationException expeption) {
            expeption.printStackTrace();
        }

    }

    @Subcommand("setviewdistance|setviewdist")
    public static void setviewdist(CommandSender sender, @Name("distance") int slots) {

        sender.sendMessage(ChatColor.GOLD + "Set the view distance to " + slots);

        try {
            changeViewDistance(slots);
        }

        catch (ReflectiveOperationException expeption) {
            expeption.printStackTrace();
        }

    }

    private static Field maxPlayersField;
    private static Field viewDistanceField;
    private static Field simulationDistanceField;

    private static void changeSlots(int slots) throws ReflectiveOperationException {
        Method serverGetHandle = Bukkit.getServer().getClass().getDeclaredMethod("getHandle");
        Object playerList = serverGetHandle.invoke(Bukkit.getServer());

        if (maxPlayersField == null) {
            maxPlayersField = getMaxPlayersField(playerList);
        }

        maxPlayersField.setInt(playerList, slots);
        updateServerProperties();
    }

    private static void changeViewDistance(int dist) throws ReflectiveOperationException {
        Method serverGetHandle = Bukkit.getServer().getClass().getDeclaredMethod("getHandle");
        Object playerList = serverGetHandle.invoke(Bukkit.getServer());

        if (viewDistanceField == null) {
            viewDistanceField = getViewField(playerList);
        }

        viewDistanceField.setInt(playerList, dist);
        updateServerProperties();
    }

    private static void changeSimDistance(int dist) throws ReflectiveOperationException {
        Method serverGetHandle = Bukkit.getServer().getClass().getDeclaredMethod("getHandle");
        Object playerList = serverGetHandle.invoke(Bukkit.getServer());

        if (simulationDistanceField == null) {
            simulationDistanceField = getSimulationField(playerList);
        }

        simulationDistanceField.setInt(playerList, dist);
        updateServerProperties();
    }

    private static Field getMaxPlayersField(Object playerList) throws ReflectiveOperationException {
        Class<?> playerListClass = playerList.getClass().getSuperclass();

        try {
            Field field = playerListClass.getDeclaredField("maxPlayers");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            for (Field field : playerListClass.getDeclaredFields()) {
                if (field.getType() != int.class) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getInt(playerList) == Bukkit.getMaxPlayers()) {
                    return field;
                }
            }

            throw new NoSuchFieldException("Unable to find maxPlayers field in " + playerListClass.getName());
        }
    }

    private static Field getSimulationField(Object playerList) throws ReflectiveOperationException {
        Class<?> playerListClass = playerList.getClass().getSuperclass();

        try {
            Field field = playerListClass.getDeclaredField("simulationDistance");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            for (Field field : playerListClass.getDeclaredFields()) {
                if (field.getType() != int.class) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getInt(playerList) == Bukkit.getSimulationDistance()) {
                    return field;
                }
            }

            throw new NoSuchFieldException("Unable to find simulationDistance field in " + playerListClass.getName());
        }
    }

    private static Field getViewField(Object playerList) throws ReflectiveOperationException {
        Class<?> playerListClass = playerList.getClass().getSuperclass();

        try {
            Field field = playerListClass.getDeclaredField("viewDistance");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            for (Field field : playerListClass.getDeclaredFields()) {
                if (field.getType() != int.class) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getInt(playerList) == Bukkit.getViewDistance()) {
                    return field;
                }
            }

            throw new NoSuchFieldException("Unable to find viewDistance field in " + playerListClass.getName());
        }
    }

    private static void updateServerProperties() {
        Properties properties = new Properties();
        File propertiesFile = new File("server.properties");

        try {
            try (InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            }

            String maxPlayers = Integer.toString(Bukkit.getServer().getMaxPlayers());
            String simulationDistance = Integer.toString(Bukkit.getServer().getSimulationDistance());
            String viewDistance = Integer.toString(Bukkit.getServer().getViewDistance());

            Bukkit.getLogger().info("Saving max players to server.properties...");
            properties.setProperty("max-players", maxPlayers);
            properties.setProperty("simulation-distance", simulationDistance);
            properties.setProperty("view-distance", viewDistance);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while updating the server properties", e);
        }
    }

    public static ItemStack endportalsummoner = ItemBuilder.of(Material.END_PORTAL_FRAME).name(CC.translate("&6Instant End Portal"))
            .addToLore("&7")
            .addToLore("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fPlace this somewhere in your claim to spawn an end portal instantly!")
            .addToLore("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fEVERY block it will place will have to be in your claim.")
            .addToLore("&7")
            .build();

}