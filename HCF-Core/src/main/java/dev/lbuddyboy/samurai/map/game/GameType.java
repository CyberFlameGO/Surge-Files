package dev.lbuddyboy.samurai.map.game;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class GameType {

    public static GameType SUMO, SPLEEF, FFA, CRYSTAL_FFA, TNT_RUN, GUN_GAME;

    static {
        SUMO = new GameType("SUMO", "Sumo");
        SPLEEF = new GameType("SPLEEF", "Spleef");
        FFA = new GameType("FFA", "FFA");
        CRYSTAL_FFA = new GameType("CRYSTAL_FFA", "Crystal-FFA");
        TNT_RUN = new GameType("TNT_RUN", "Tnt-Run");
        GUN_GAME = new GameType("GUN_GAME", "GunGame");

        GameHandler.GAMES_FOLDER = new File(Samurai.getInstance().getDataFolder(), "games");

        if (!GameHandler.GAMES_FOLDER.exists()) GameHandler.GAMES_FOLDER.mkdir();

        for (GameType type : values()) {
            GameHandler.getConfigs().put(type.name(), new Config(Samurai.getInstance(), type.name().toLowerCase(), GameHandler.GAMES_FOLDER));
        }

        SUMO = new GameType("SUMO", "Sumo",
                GameHandler.getConfigs().get("SUMO").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("SUMO").getString("host-item.material"))),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("SUMO").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("SUMO").getBoolean("game-settings.disabled"));
        SPLEEF = new GameType("SPLEEF", "Spleef",
                GameHandler.getConfigs().get("SPLEEF").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("SPLEEF").getString("host-item.material"))),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("SPLEEF").getInt("game-settings.max-players"), GameHandler.getConfigs().get("SPLEEF").getBoolean("game-settings.disabled"));
        FFA = new GameType("FFA", "FFA",
                GameHandler.getConfigs().get("FFA").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("FFA").getString("host-item.material"))),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("FFA").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("FFA").getBoolean("game-settings.disabled")
        );
        CRYSTAL_FFA = new GameType("CRYSTAL_FFA", "Crystal-FFA",
                GameHandler.getConfigs().get("CRYSTAL_FFA").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("CRYSTAL_FFA").getString("host-item.material"))),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("CRYSTAL_FFA").getBoolean("game-settings.disabled")
        );
        TNT_RUN = new GameType("TNT_RUN", "TnT-Run",
                GameHandler.getConfigs().get("TNT_RUN").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("TNT_RUN").getString("host-item.material"))),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("TNT_RUN").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("TNT_RUN").getBoolean("game-settings.disabled")
        );
        GUN_GAME = new GameType("GUN_GAME", "Gun-Game",
                GameHandler.getConfigs().get("GUN_GAME").getString("host-item.description"),
                new ItemStack(Material.getMaterial(GameHandler.getConfigs().get("GUN_GAME").getString("host-item.material"))),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.minimum-start-players"),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.minimum-players"),
                GameHandler.getConfigs().get("GUN_GAME").getInt("game-settings.max-players"),
                GameHandler.getConfigs().get("GUN_GAME").getBoolean("game-settings.disabled")
        );
    }

    private final String name;
    private final String displayName;
    private String description;
    private ItemStack icon;
    private int minForceStartPlayers;
    private int minPlayers;
    private int maxPlayers;
    private boolean disabled;

    public boolean canHost(Player player) {
        return player.hasPermission("game.host." + name().toLowerCase());
    }

    public String name() {
        return name;
    }

    public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> completions = new ArrayList<>();
            Player player = context.getPlayer();
            String input = context.getInput();

            for (GameType gameType : GameType.values()) {
                if (StringUtils.startsWithIgnoreCase(gameType.name(), input)) {
                    completions.add(gameType.name());
                }
            }

            return completions;
        }

    }

    public static class Type implements ContextResolver<GameType, BukkitCommandExecutionContext> {

        @Override
        public GameType getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
            Player sender = c.getPlayer();
            String source = c.popFirstArg();

            try {
                return GameType.valueOf(source.toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Game Type '" + source + "' couldn't be found.");
                return null;
            }
        }
    }

    public static GameType[] values() {
        return Arrays.asList(SUMO, SPLEEF, FFA, CRYSTAL_FFA, TNT_RUN, GUN_GAME).toArray(new GameType[0]);
    }

    public static GameType valueOf(String string) {
        for (GameType type : values()) {
            if (type.name().equals(string.toUpperCase())) return type;
        }
        return null;
    }

}
