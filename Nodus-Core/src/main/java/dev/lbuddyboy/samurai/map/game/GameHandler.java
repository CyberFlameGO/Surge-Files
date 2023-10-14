package dev.lbuddyboy.samurai.map.game;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.map.game.impl.crystal.CrystalGame;
import dev.lbuddyboy.samurai.map.game.impl.gungame.GunGameGame;
import dev.lbuddyboy.samurai.map.game.impl.tntrun.TNTRunGame;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.impl.ffa.FFAGame;
import dev.lbuddyboy.samurai.map.game.impl.spleef.SpleefGame;
import dev.lbuddyboy.samurai.map.game.impl.sumo.SumoGame;
import org.bukkit.*;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class GameHandler {

    private static final long GAME_COOLDOWN = TimeUnit.MINUTES.toMillis(30L);

    private static final File CONFIG_FILE = new File(Samurai.getInstance().getDataFolder(), "kitmap-games-config.json");
    public static File GAMES_FOLDER;
    private static final Type CONFIG_DATA_TYPE = new TypeToken<GameHandlerConfig>() {
    }.getType();

    private GameHandlerConfig config = new GameHandlerConfig();

    @Getter
    private static final Map<String, Config> configs = new HashMap<>();

    @Setter private Game ongoingGame;
    private Long nextRuntime = System.currentTimeMillis() - GAME_COOLDOWN;

    @Setter
    private boolean disabled;

    private World world;

    public GameHandler() {
        loadWorld();
        loadConfig();
    }

    public void reload() {
        for (GameType type : GameType.values()) {
            configs.put(type.name(), new Config(Samurai.getInstance(), type.name().toLowerCase(), GAMES_FOLDER));
        }
        GameType.SUMO = new GameType("SUMO", "Sumo",
                GameHandler.getConfigs().get("SUMO").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("SUMO").getString("host-item.material"))),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("SUMO").getBoolean("game-settings.disabled"));
        GameType.SPLEEF = new GameType("SPLEEF", "Spleef",
                GameHandler.getConfigs().get("SPLEEF").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("SPLEEF").getString("host-item.material"))),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.max-players"), GameHandler.getConfigs().get("SPLEEF").getBoolean("game-settings.disabled"));
        GameType.FFA = new GameType("FFA", "FFA",
                GameHandler.getConfigs().get("FFA").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("FFA").getString("host-item.material"))),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("FFA").getBoolean("game-settings.disabled")
        );
        GameType.CRYSTAL_FFA = new GameType("CRYSTAL_FFA", "Crystal-FFA",
                GameHandler.getConfigs().get("CRYSTAL_FFA").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("CRYSTAL_FFA").getString("host-item.material"))),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getBoolean("game-settings.disabled")
        );
        GameType.TNT_RUN = new GameType("TNT_RUN", "TnT-Run",
                GameHandler.getConfigs().get("TNT_RUN").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("TNT_RUN").getString("host-item.material"))),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("TNT_RUN").getBoolean("game-settings.disabled")
        );
        GameType.GUN_GAME = new GameType("GUN_GAME", "Gun-Game",
                GameHandler.getConfigs().get("GUN_GAME").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("GUN_GAME").getString("host-item.material"))),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("GUN_GAME").getBoolean("game-settings.disabled")
        );
    }

    private void loadWorld() {
        WorldCreator worldCreator = new WorldCreator("kits_events");
        world = Bukkit.createWorld(worldCreator);
    }

    private void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (Reader reader = Files.newReader(CONFIG_FILE, Charsets.UTF_8)) {
                config = Samurai.PLAIN_GSON.fromJson(reader, CONFIG_DATA_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to import kitmap-games-config.json!");
            }
        }
    }

    public void saveConfig() {
        try {
            Files.write(Samurai.PLAIN_GSON.toJson(config, CONFIG_DATA_TYPE), CONFIG_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Samurai.getInstance().getLogger().severe(ChatColor.RED + "Failed to export kitmap-games-config.json!");
        }
    }

    public int getCooldownSeconds() {
        if (System.currentTimeMillis() < nextRuntime)
            return (int) ((nextRuntime - System.currentTimeMillis()) / 1000F);
        return 0;
    }

    public boolean isOngoingGame() {
        return ongoingGame != null;
    }

    public boolean isHostCooldown() {
        return System.currentTimeMillis() < nextRuntime;
    }

    public boolean isJoinable() {
        return ongoingGame != null && ongoingGame.getState() == GameState.WAITING;
    }

    public boolean canStartGame(Player player, GameType gameType) {
        if (disabled) {
            player.sendMessage(ChatColor.RED + "Events are currently disabled.");
            return false;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou cannot do this whilst your PvP Timer is active."));
            return false;
        }

        if (!gameType.canHost(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to host " + gameType.getDisplayName() + " events.");
            return false;
        }

        if (isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is an ongoing game, and only one game can run at a time.");
            return false;
        }

        if (findArenas(gameType).isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no arenas compatible with that game type!");
            return false;
        }

        if (!player.hasPermission("game.host.cooldown-bypass") && isHostCooldown()) {
            double cooldown = ((double) Math.round(10.0D * ((double) (nextRuntime - System.currentTimeMillis()) / 1000F)) / 10.0D);
            player.sendMessage(ChatColor.RED + "Another game can't be hosted for another " + cooldown + "s.");
            return false;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return false;
        }

        return true;
    }

    public Game startGame(Player host, GameType gameType) throws IllegalStateException {
        if (ongoingGame != null) {
            throw new IllegalStateException("There is an ongoing game!");
        }

        if (gameType == GameType.SUMO) {
            ongoingGame = new SumoGame(host.getUniqueId(), findArenas(gameType));
//        } else if (gameType == GameType.TAG) {
//            ongoingGame = new TagGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.FFA) {
            ongoingGame = new FFAGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.CRYSTAL_FFA) {
            ongoingGame = new CrystalGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.SPLEEF) {
            ongoingGame = new SpleefGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.GUN_GAME) {
            ongoingGame = new GunGameGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.TNT_RUN) {
            ongoingGame = new TNTRunGame(host.getUniqueId(), findArenas(gameType));
        } else {
            throw new IllegalStateException("Game type not supported yet!");
        }

        ongoingGame.startGame();

        return ongoingGame;
    }

    public void endGame() {
        if (ongoingGame == null || !ongoingGame.isHasExpired()) // Don't apply cooldown for events that expired
            nextRuntime = System.currentTimeMillis() + GAME_COOLDOWN;

        ongoingGame = null;
    }

    public List<GameArena> findArenas(GameType gameType) {
        List<GameArena> compatible = new ArrayList<>();
        for (GameArena arena : config.getArenas()) {
            if (arena.isSetup() && arena.getCompatibleGameTypes().contains(gameType.name().toLowerCase())) {
                compatible.add(arena);
            }
        }
        return compatible;
    }

}
